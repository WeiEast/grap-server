package com.treefinance.saas.grapserver.biz.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.datatrees.spider.bank.api.*;
import com.datatrees.spider.share.api.SpiderTaskApi;
import com.datatrees.spider.share.domain.CommonPluginParam;
import com.datatrees.spider.share.domain.ProcessResult;
import com.datatrees.spider.share.domain.http.HttpResult;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.treefinance.proxy.api.ProxyProvider;
import com.treefinance.saas.grapserver.biz.cache.RedisDao;
import com.treefinance.saas.grapserver.common.exception.CrawlerBizException;
import com.treefinance.saas.grapserver.common.model.Constants;
import com.treefinance.saas.grapserver.common.utils.RedisKeyUtils;
import com.treefinance.saas.knife.result.Results;
import com.treefinance.saas.knife.result.SimpleResult;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 邮箱账单模拟登陆
 * @author haojiahong on 2017/12/26.
 */
@Service
public class EmailLoginSimulationService {

    private static final Logger logger = LoggerFactory.getLogger(EmailLoginSimulationService.class);

    private static final String EMAIL_LOGIN_PROCESS_KEY_PREFIX = "email_login_key";
    
    private static final String SUCCESS = "SUCCESS";
    
    private static final String DIRECTIVE = "directive";
    
    private static final String LOGIN_SUCCESS = "login_success";

    @Autowired
    private MailServiceApiForQQ mailServiceApiForQQ;
    @Autowired
    private MailServiceApiFor163 mailServiceApiFor163;
    @Autowired
    private MailServiceApiFor126 mailServiceApiFor126;
    @Autowired
    private MailServiceApiForSina mailServiceApiForSina;
    @Autowired
    private MailServiceApiForExMailQQ mailServiceApiForExMailQQ;
    @Autowired
    private SpiderTaskApi spiderTaskApi;
    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskTimeService taskTimeService;
    @Autowired
    private ProxyProvider proxyProvider;
    @Autowired
    private RedisDao redisDao;

    /**
     * QQ邮箱登陆(异步)
     */
    public Object login(CommonPluginParam param) {
        HttpResult<Object> result;
        try {
            result = mailServiceApiForQQ.login(param);
        } catch (Exception e) {
            logger.error("邮箱账单:调用爬数邮箱账单登陆异常,param={}", JSON.toJSONString(param), e);
            throw e;
        }
        if (!result.getStatus()) {
            loginFailProcess(param, result);
        }
        logEmailLoginInfo(param.getTaskId(), param.getUsername(), "qq.com", result.getData());
        return SimpleResult.successResult(result);
    }

    /**
     * QQ邮箱刷新二维码(异步)
     */
    public Object refreshQRCode(CommonPluginParam param) {
        HttpResult<Object> result;
        try {
            result = mailServiceApiForQQ.refeshQRCode(param);
        } catch (Exception e) {
            logger.error("邮箱账单:调用爬数邮箱账单刷新二维码异常,param={}", JSON.toJSONString(param), e);
            throw e;
        }
        if (!result.getStatus()) {
            logger.info("邮箱账单:调用爬数邮箱账单刷新二维码失败,param={},result={}",
                    JSON.toJSONString(param), JSON.toJSONString(result));
            if (StringUtils.isNotBlank(result.getMessage())) {
                throw new CrawlerBizException(result.getMessage());
            }
        }
        return SimpleResult.successResult(result);
    }

    /**
     * QQ邮箱查询二维码状态
     */
    public Object queryQRStatus(CommonPluginParam param) {
        HttpResult<Object> result;
        try {
            result = mailServiceApiForQQ.queryQRStatus(param);
        } catch (Exception e) {
            logger.error("邮箱账单:调用爬数邮箱账单查询二维码状态异常,param={}", JSON.toJSONString(param), e);
            throw e;
        }
        if (!result.getStatus()) {
            logger.info("邮箱账单:调用爬数邮箱账单查询二维码状态失败,param={},result={}",
                    JSON.toJSONString(param), JSON.toJSONString(result));
            if (StringUtils.isNotBlank(result.getMessage())) {
                throw new CrawlerBizException(result.getMessage());
            }
        }
        if (result.getData() != null && StringUtils.equals(SUCCESS, String.valueOf(result.getData()))) {
            taskService.updateWebSite(param.getTaskId(), "qq.com");
            taskTimeService.updateLoginTime(param.getTaskId(), new Date());
        }
        return SimpleResult.successResult(result);
    }

    /**
     * qq企业邮箱登录初始化
     *
     * @param param taskId必传
     */
    public Object loginInitForQQExMail(CommonPluginParam param) {
        HttpResult<Object> result;
        try {
            result = mailServiceApiForExMailQQ.init(param);
        } catch (Exception e) {
            logger.error("邮箱账单:调用爬数登陆初始化异常,param={}", JSON.toJSONString(param), e);
            throw e;
        }
        if (!result.getStatus()) {
            logger.info("邮箱账单:调用爬数登陆初始化失败,param={},result={}",
                    JSON.toJSONString(param), JSON.toJSONString(result));
            throw new CrawlerBizException(result.getMessage());
        }
        return SimpleResult.successResult(result.getData());
    }

    /**
     * qq企业邮箱登录
     */
    public Object loginForQQExMail(CommonPluginParam param) {
        Map<String, Object> lockMap = Maps.newHashMap();
        String key = RedisKeyUtils.genLoginLockKey(param.getTaskId());
        try {
            lockMap = redisDao.acquireLock(key, 60 * 1000L);
            if (lockMap != null) {
                HttpResult<Object> result;
                try {
                    result = mailServiceApiForExMailQQ.login(param);
                } catch (Exception e) {
                    logger.error("邮箱账单:调用爬数邮箱账单登陆异常,param={}", JSON.toJSONString(param), e);
                    throw e;
                }
                if (!result.getStatus()) {
                    return loginFailProcess(param, result);
                }
                if (result.getData() != null) {
                    Map<String, Object> map = JSONObject.parseObject(JSON.toJSONString(result.getData()));
                    if (MapUtils.isNotEmpty(map) && map.get(DIRECTIVE) != null) {
                        if (StringUtils.equalsIgnoreCase(LOGIN_SUCCESS, map.get(DIRECTIVE).toString())) {
                            if (StringUtils.isNotEmpty(param.getUsername())) {
                                taskService.setAccountNo(param.getTaskId(), param.getUsername());
                            }
                            taskService.updateWebSite(param.getTaskId(), "exmail.qq.com");
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
        logger.info("邮箱账单:调用爬数邮箱账单登陆失败,param={},result={}",
                JSON.toJSONString(param), JSON.toJSONString(result));
        if (StringUtils.isNotBlank(result.getMessage())) {
            throw new CrawlerBizException(result.getMessage());
        } else {
            throw new CrawlerBizException("登陆失败,请重试");
        }
    }


    /**
     * 163邮箱登陆(异步)
     */
    public Object loginFor163(CommonPluginParam param) {
        HttpResult<Object> result;
        try {
            result = mailServiceApiFor163.login(param);
        } catch (Exception e) {
            logger.error("邮箱账单:调用爬数邮箱账单登陆异常,param={}", JSON.toJSONString(param), e);
            throw e;
        }
        if (!result.getStatus()) {
            loginFailProcess(param, result);
        }
        logEmailLoginInfo(param.getTaskId(), param.getUsername(), "163.com", result.getData());
        return SimpleResult.successResult(result);
    }

    /**
     * 163邮箱刷新二维码
     */
    public Object refreshQRCodeFor163(CommonPluginParam param) {
        HttpResult<Object> result;
        try {
            result = mailServiceApiFor163.refeshQRCode(param);
        } catch (Exception e) {
            logger.error("邮箱账单:调用爬数邮箱账单刷新二维码异常,param={}", JSON.toJSONString(param), e);
            throw e;
        }
        if (!result.getStatus()) {
            logger.info("邮箱账单:调用爬数邮箱账单刷新二维码失败,param={},result={}",
                    JSON.toJSONString(param), JSON.toJSONString(result));
            if (StringUtils.isNotBlank(result.getMessage())) {
                throw new CrawlerBizException(result.getMessage());
            }
        }
        return SimpleResult.successResult(result);
    }

    /**
     * 163邮箱查询二维码状态
     */
    public Object queryQRStatusFor163(CommonPluginParam param) {
        HttpResult<Object> result;
        try {
            result = mailServiceApiFor163.queryQRStatus(param);
        } catch (Exception e) {
            logger.error("邮箱账单:调用爬数邮箱账单查询二维码状态异常,param={}", JSON.toJSONString(param), e);
            throw e;
        }
        if (!result.getStatus()) {
            logger.info("邮箱账单:调用爬数邮箱账单查询二维码状态失败,param={},result={}",
                    JSON.toJSONString(param), JSON.toJSONString(result));
            if (StringUtils.isNotBlank(result.getMessage())) {
                throw new CrawlerBizException(result.getMessage());
            }
        }
        if (result.getData() != null && StringUtils.equals(SUCCESS, String.valueOf(result.getData()))) {
            taskService.updateWebSite(param.getTaskId(), "163.com");
            taskTimeService.updateLoginTime(param.getTaskId(), new Date());
        }
        return SimpleResult.successResult(result);
    }


    /**
     * 126邮箱登陆(异步)
     */
    public Object loginFor126(CommonPluginParam param) {
        HttpResult<Object> result;
        try {
            result = mailServiceApiFor126.login(param);
        } catch (Exception e) {
            logger.error("邮箱账单:调用爬数邮箱账单登陆异常,param={}", JSON.toJSONString(param), e);
            throw e;
        }
        if (!result.getStatus()) {
            logger.info("邮箱账单:调用爬数邮箱账单登陆失败,param={},result={}",
                    JSON.toJSONString(param), JSON.toJSONString(result));
            if (StringUtils.isNotBlank(result.getMessage())) {
                throw new CrawlerBizException(result.getMessage());
            } else {
                throw new CrawlerBizException("登陆失败,请重试");
            }
        }
        logEmailLoginInfo(param.getTaskId(), param.getUsername(), "126.com", result.getData());
        return SimpleResult.successResult(result);
    }

    /**
     * 126邮箱刷新二维码
     */
    public Object refreshQRCodeFor126(CommonPluginParam param) {
        HttpResult<Object> result;
        try {
            result = mailServiceApiFor126.refeshQRCode(param);
        } catch (Exception e) {
            logger.error("邮箱账单:调用爬数邮箱账单刷新二维码异常,param={}", JSON.toJSONString(param), e);
            throw e;
        }
        if (!result.getStatus()) {
            logger.info("邮箱账单:调用爬数邮箱账单刷新二维码失败,param={},result={}",
                    JSON.toJSONString(param), JSON.toJSONString(result));
            if (StringUtils.isNotBlank(result.getMessage())) {
                throw new CrawlerBizException(result.getMessage());
            }
        }
        return SimpleResult.successResult(result);
    }

    /**
     * 126邮箱查询二维码状态
     */
    public Object queryQRStatusFor126(CommonPluginParam param) {
        HttpResult<Object> result;
        try {
            result = mailServiceApiFor126.queryQRStatus(param);
        } catch (Exception e) {
            logger.error("邮箱账单:调用爬数邮箱账单查询二维码状态异常,param={}", JSON.toJSONString(param), e);
            throw e;
        }
        if (!result.getStatus()) {
            logger.info("邮箱账单:调用爬数邮箱账单查询二维码状态失败,param={},result={}",
                    JSON.toJSONString(param), JSON.toJSONString(result));
            if (StringUtils.isNotBlank(result.getMessage())) {
                throw new CrawlerBizException(result.getMessage());
            }
        }
        if (result.getData() != null && StringUtils.equals(SUCCESS, String.valueOf(result.getData()))) {
            taskService.updateWebSite(param.getTaskId(), "126.com");
            taskTimeService.updateLoginTime(param.getTaskId(), new Date());
        }
        return SimpleResult.successResult(result);
    }

    /**
     * 新浪邮箱登录初始化
     *
     * @param param taskId必传
     */
    public Object loginInitForSina(CommonPluginParam param) {
        HttpResult<Object> result;
        try {
            result = mailServiceApiForSina.init(param);
        } catch (Exception e) {
            logger.error("邮箱账单:调用爬数登陆初始化异常,param={}", JSON.toJSONString(param), e);
            throw e;
        }
        if (!result.getStatus()) {
            logger.info("邮箱账单:调用爬数登陆初始化失败,param={},result={}",
                    JSON.toJSONString(param), JSON.toJSONString(result));
            throw new CrawlerBizException(result.getMessage());
        }
        return SimpleResult.successResult(result.getData());
    }


    /**
     * 新浪邮箱登录
     */
    public Object loginForSina(CommonPluginParam param) {
        Map<String, Object> lockMap = Maps.newHashMap();
        String key = RedisKeyUtils.genLoginLockKey(param.getTaskId());
        try {
            lockMap = redisDao.acquireLock(key, 60 * 1000L);
            if (lockMap != null) {
                HttpResult<Object> result;
                try {
                    result = mailServiceApiForSina.login(param);
                } catch (Exception e) {
                    logger.error("邮箱账单:调用爬数邮箱账单登陆异常,param={}", JSON.toJSONString(param), e);
                    throw e;
                }
                if (!result.getStatus()) {
                    loginFailProcess(param, result);
                }
                if (result.getData() != null) {
                    Map<String, Object> map = JSONObject.parseObject(JSON.toJSONString(result.getData()));
                    if (MapUtils.isNotEmpty(map) && map.get(DIRECTIVE) != null) {
                        if (StringUtils.equalsIgnoreCase(LOGIN_SUCCESS, map.get(DIRECTIVE).toString())) {
                            if (StringUtils.isNotEmpty(param.getUsername())) {
                                taskService.setAccountNo(param.getTaskId(), param.getUsername());
                            }
                            taskService.updateWebSite(param.getTaskId(), "sina.com");
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

    public Object refreshPicCodeForSina(CommonPluginParam param) {
        HttpResult<Object> result;
        try {
            result = mailServiceApiForSina.refeshPicCode(param);
        } catch (Exception e) {
            logger.error("邮箱账单:调用爬数刷新图片验证码异常,param={}", JSON.toJSONString(param), e);
            throw e;
        }
        if (!result.getStatus()) {
            logger.info("邮箱账单:调用爬数刷新图片验证码失败,param={},result={}",
                    JSON.toJSONString(param), JSON.toJSONString(result));
            throw new CrawlerBizException(result.getMessage());
        }
        return SimpleResult.successResult(result.getData());
    }

    /**
     * 轮询处理状态(通用接口)
     */
    public Object processStatus(Long processId, Long taskId) {
        ProcessResult result;
        try {
            result = spiderTaskApi.queryProcessResult(processId);
        } catch (Exception e) {
            logger.error("邮箱账单:调用爬数邮箱账单轮询处理状态异常,processId={},taskId={}", processId, taskId, e);
            throw e;
        }
        if (StringUtils.equalsIgnoreCase(SUCCESS, result.getProcessStatus())) {
            String key = Joiner.on(":").join(EMAIL_LOGIN_PROCESS_KEY_PREFIX, processId);
            boolean hasKey = redisDao.getRedisTemplate().hasKey(key);
            if (hasKey) {
                Map<Object, Object> map = redisDao.getRedisTemplate().opsForHash().entries(key);
                //记录账号
                taskService.setAccountNo(Long.valueOf(map.get("taskId").toString()), map.get("userName").toString());
                //记录webSite
                taskService.updateWebSite(Long.valueOf(map.get("taskId").toString()), map.get("webSite").toString());
                //更新登录成功时间
                taskTimeService.updateLoginTime(Long.valueOf(map.get("taskId").toString()), new Date());
            }

        }
        return SimpleResult.successResult(result);
    }


    /**
     * 是否支持当前ip的省份代理(通用接口)
     */
    public Object supportProvinceProxy(CommonPluginParam param) {
        Boolean flag;
        try {
            flag = proxyProvider.supportProvinceName(param.getUserIp(), null);
        } catch (Exception e) {
            logger.error("邮箱账单:调用爬数邮箱账单查询是否支持当前IP的省份代理异常,param={}", JSON.toJSONString(param), e);
            throw e;
        }
        return Results.newSuccessResult(flag);
    }


    /**
     * 账号密码登录,记录登录信息
     */
    private void logEmailLoginInfo(Long taskId, String userName, String webSite, Object result) {
        try {
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(result));
            Long processId = jsonObject.getLong("processId");
            if (StringUtils.isNotEmpty(userName) && processId != null) {
                String key = Joiner.on(":").join(EMAIL_LOGIN_PROCESS_KEY_PREFIX, processId);
                Map<String, Object> map = Maps.newHashMap();
                map.put("taskId", taskId + "");
                map.put("userName", userName);
                map.put("webSite", webSite);
                redisDao.getRedisTemplate().opsForHash().putAll(key, map);
                if (redisDao.getRedisTemplate().getExpire(key) == -1) {
                    redisDao.getRedisTemplate().expire(key, 10, TimeUnit.MINUTES);
                }
            }
        } catch (Exception e) {
            logger.error("邮箱账单:账号密码登陆时,记录登录信息异常.taskId={}", taskId, e);
        }
    }

}
