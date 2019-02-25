package com.treefinance.saas.grapserver.biz.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.datatrees.spider.ecommerce.api.EconomicApiForTaoBaoH5;
import com.datatrees.spider.ecommerce.api.EconomicApiForTaoBaoQR;
import com.datatrees.spider.share.domain.CommonPluginParam;
import com.datatrees.spider.share.domain.http.HttpResult;
import com.google.common.collect.Maps;
import com.treefinance.saas.grapserver.context.Constants;
import com.treefinance.saas.grapserver.exception.CrawlerBizException;
import com.treefinance.saas.grapserver.share.cache.redis.RedisDao;
import com.treefinance.saas.grapserver.share.cache.redis.RedisKeyUtils;
import com.treefinance.saas.knife.result.SimpleResult;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * @author haojiahong on 2018/1/11.
 */
@Service
public class EcommerceLoginSimulationService {

    private static final Logger logger = LoggerFactory.getLogger(EcommerceLoginSimulationService.class);

    @Autowired
    private EconomicApiForTaoBaoQR economicApiForTaoBaoQR;

    @Autowired
    private EconomicApiForTaoBaoH5 economicApiForTaoBaoH5;

    @Autowired
    private TaskTimeService        taskTimeService;

    @Autowired
    private RedisDao               redisDao;

    @Autowired
    private TaskService taskService;

    private static final String SUCCESS = "SUCCESS";

    private static final String DIRECTIVE = "directive";

    private static final String LOGIN_SUCCESS = "login_success";

    /**
     * 获取二维码
     */
    public Object refreshQRCode(CommonPluginParam param) {
        HttpResult<Object> result;
        try {
            result = economicApiForTaoBaoQR.refeshQRCode(param);
        } catch (Exception e) {
            logger.error("电商:调用爬数获取二维码异常,param={}", JSON.toJSONString(param), e);
            throw e;
        }
        if (!result.getStatus()) {
            logger.info("电商:调用爬数获取二维码失败,param={},result={}",
                    JSON.toJSONString(param), JSON.toJSONString(result));
            throw new CrawlerBizException(result.getMessage());
        }
        return SimpleResult.successResult(result);
    }

    /**
     * 轮询获取二维码状态
     */
    public Object queryQRStatus(CommonPluginParam param) {
        HttpResult<Object> result;
        try {
            result = economicApiForTaoBaoQR.queryQRStatus(param);
        } catch (Exception e) {
            logger.error("电商:调用爬数轮询获取二维码状态异常,param={}", JSON.toJSONString(param), e);
            throw e;
        }
        if (!result.getStatus()) {
            logger.info("电商:调用爬数轮询获取二维码状态失败,param={},result={}",
                    JSON.toJSONString(param), JSON.toJSONString(result));
            throw new CrawlerBizException(result.getMessage());
        }
        if (result.getData() != null) {
            String status = String.valueOf(result.getData());
            if (StringUtils.isNotBlank(status) && StringUtils.equalsIgnoreCase("CONFIRMED", status)) {
                taskTimeService.updateLoginTime(param.getTaskId(), new Date());
            }
        }
        return SimpleResult.successResult(result);
    }

    /**
     * 淘宝h5账号密码登陆
     */
    public Object loginForTaoBaoH5(CommonPluginParam param) {
        Map<String, Object> lockMap = Maps.newHashMap();
        String key = RedisKeyUtils.genLoginLockKey(param.getTaskId());
        try {
            lockMap = redisDao.acquireLock(key, 60 * 1000L);
            if (lockMap != null) {
                HttpResult<Object> result;
                try {
                    result = economicApiForTaoBaoH5.login(param);
                } catch (Exception e) {
                    logger.error("电商:调用爬数淘宝H5登陆异常,param={}", JSON.toJSONString(param), e);
                    throw e;
                }
                if (!result.getStatus()) {
                    return loginFailProcess(param, result);
                }
                if (result.getData() != null) {
                    Map<String, Object> map = JSONObject.parseObject(JSON.toJSONString(result.getData()));
                    if (MapUtils.isNotEmpty(map) && map.get(DIRECTIVE) != null) {
                        if (StringUtils.equalsIgnoreCase(LOGIN_SUCCESS, map.get(DIRECTIVE).toString())) {
                            taskService.recordAccountNoAndWebsite(param.getTaskId(), param.getUsername(), "password.taobao.com.h5");
                            taskTimeService.updateLoginTime(param.getTaskId(), new Date());
                        }
                    }
                }
                return SimpleResult.successResult(result);

            }
            throw new CrawlerBizException(Constants.REDIS_LOCK_ERROR_MSG);
        } finally {
            redisDao.releaseLock(key, lockMap, 60 * 1000L);
        }
    }

    private Object loginFailProcess(CommonPluginParam param, HttpResult<Object> result) {
        logger.info("电商:调用爬数淘宝H5登陆失败,param={},result={}",
                JSON.toJSONString(param), JSON.toJSONString(result));
        if (StringUtils.isNotBlank(result.getMessage())) {
            throw new CrawlerBizException(result.getMessage());
        } else {
            throw new CrawlerBizException("登陆失败,请重试");
        }
    }

}
