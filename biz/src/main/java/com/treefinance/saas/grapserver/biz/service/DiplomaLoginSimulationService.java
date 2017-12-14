package com.treefinance.saas.grapserver.biz.service;

import com.alibaba.fastjson.JSON;
import com.datatrees.rawdatacentral.api.RpcEducationService;
import com.datatrees.rawdatacentral.domain.education.EducationParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.google.common.collect.Maps;
import com.treefinance.saas.grapserver.common.enums.EBizType;
import com.treefinance.saas.grapserver.common.exception.CrawlerBizException;
import com.treefinance.saas.grapserver.dao.entity.TaskAttribute;
import com.treefinance.saas.grapserver.facade.enums.ETaskAttribute;
import com.treefinance.saas.knife.result.SimpleResult;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 学信网
 * Created by haojiahong on 2017/12/11.
 */
@Service
public class DiplomaLoginSimulationService {
    private static final Logger logger = LoggerFactory.getLogger(DiplomaLoginSimulationService.class);

    @Autowired
    private RpcEducationService rpcEducationService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private MerchantConfigService merchantConfigService;
    @Autowired
    private TaskAttributeService taskAttributeService;
    @Autowired
    private AppBizLicenseService appBizLicenseService;


    /**
     * 登陆初始化
     *
     * @param educationParam
     * @return
     */
    public Object loginInit(EducationParam educationParam) {
        HttpResult<Map<String, Object>> result;
        try {
            result = rpcEducationService.loginInit(educationParam);
        } catch (Exception e) {
            logger.error("学信网:调用爬数登陆初始化异常,param={}", JSON.toJSONString(educationParam), e);
            throw e;
        }
        if (!result.getStatus()) {
            logger.info("学信网:调用爬数登陆初始化失败,param={},result={}",
                    JSON.toJSONString(educationParam), JSON.toJSONString(result));
            throw new CrawlerBizException(result.getMessage());
        }
        return SimpleResult.successResult(result.getData());
    }

    /**
     * 登陆
     *
     * @param param
     * @return
     */
    public Object loginSubmit(EducationParam param) {
        HttpResult<Map<String, Object>> result;
        try {
            result = rpcEducationService.loginSubmit(param);
        } catch (Exception e) {
            logger.error("学信网:调用爬数登陆异常,param={}", JSON.toJSONString(param), e);
            throw e;
        }
        if (!result.getStatus()) {
            logger.info("学信网:调用爬数登陆失败,param={},result={}", JSON.toJSONString(param), JSON.toJSONString(result));
            if (StringUtils.isNotBlank(result.getMessage())) {
                throw new CrawlerBizException(result.getMessage());
            } else {
                throw new CrawlerBizException("登陆失败,请重试");
            }
        }
        Long taskId = param.getTaskId();
        String website = param.getWebsiteName();
        String loginName = param.getLoginName();
        taskService.updateTask(taskId, loginName, website);
        return SimpleResult.successResult(result.getData());
    }

    /**
     * 注册时刷新图片验证码
     *
     * @param param
     * @return
     */
    public Object registerRefreshPicCode(EducationParam param) {
        HttpResult<Map<String, Object>> result;
        try {
            result = rpcEducationService.registerRefeshPicCode(param);
        } catch (Exception e) {
            logger.error("学信网:调用爬数注册时刷新图片验证码异常,param={}", JSON.toJSONString(param), e);
            throw e;
        }
        if (!result.getStatus()) {
            logger.info("学信网:调用爬数注册时刷新图片验证码失败,param={},result={}", JSON.toJSONString(param), JSON.toJSONString(result));
            throw new CrawlerBizException(result.getMessage());
        }
        return SimpleResult.successResult(result.getData());
    }

    /**
     * 注册时验证图片验证码并发送短信
     *
     * @param param
     * @return
     */
    public Object registerValidatePicCodeAndSendSmsCode(EducationParam param) {
        HttpResult<Map<String, Object>> result;
        try {
            result = rpcEducationService.registerValidatePicCodeAndSendSmsCode(param);
        } catch (Exception e) {
            logger.error("学信网:调用爬数注册时验证图片验证码并发送短信异常,param={}", JSON.toJSONString(param), e);
            throw e;
        }
        if (!result.getStatus()) {
            logger.info("学信网:调用爬数注册时验证图片验证码并发送短信失败,param={},result={}", JSON.toJSONString(param), JSON.toJSONString(result));
            throw new CrawlerBizException(result.getMessage());
        }
        return SimpleResult.successResult(result.getData());
    }

    /**
     * 注册提交
     *
     * @param param
     * @return
     */
    public Object registerSubmit(EducationParam param) {
        HttpResult<Map<String, Object>> result;
        try {
            result = rpcEducationService.registerSubmit(param);
        } catch (Exception e) {
            logger.error("学信网:调用爬数注册提交异常,param={}", JSON.toJSONString(param), e);
            throw e;
        }
        if (!result.getStatus()) {
            logger.info("学信网:调用爬数注册提交失败,param={},result={}", JSON.toJSONString(param), JSON.toJSONString(result));
            throw new CrawlerBizException(result.getMessage());
        }
        return SimpleResult.successResult(result.getData());
    }

    /**
     * 获取配置
     *
     * @param appId
     * @param taskId
     * @return
     */
    public Object getConfig(String appId, Long taskId) {
        Map<String, Object> colorMap = merchantConfigService.getColorConfig(appId);
        Map<String, Object> map = Maps.newHashMap();
        map.put("color", colorMap);
        map.put("license", appBizLicenseService.isShowLicense(appId, EBizType.OPERATOR.getText()));
        return SimpleResult.successResult(map);
    }

    /**
     * 获取合作方属性值引用
     *
     * @param taskId
     * @return
     */
    public Object getThirdPartyReference(Long taskId) {
        Map<String, TaskAttribute> taskAttributeMap
                = taskAttributeService.findByNames(taskId, true, ETaskAttribute.MOBILE.getAttribute(),
                ETaskAttribute.NAME.getAttribute(), ETaskAttribute.ID_CARD.getAttribute());
        Map<String, Object> map = Maps.newHashMap();
        if (MapUtils.isNotEmpty(taskAttributeMap)) {
            map.putAll(taskAttributeMap);
        }
        return SimpleResult.successResult(map);
    }
}
