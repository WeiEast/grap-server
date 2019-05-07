package com.treefinance.saas.grapserver.web.controller;

import com.alibaba.fastjson.JSON;
import com.datatrees.spider.share.domain.CommonPluginParam;
import com.treefinance.saas.grapserver.biz.domain.ChsiUserInfo;
import com.treefinance.saas.grapserver.biz.service.DiplomaLoginSimulationService;
import com.treefinance.saas.knife.result.SimpleResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 学信网抓取
 * @author haojiahong on 2017/12/11.
 */
@RestController
@RequestMapping(value = {"/diploma", "/h5/diploma", "/grap/h5/diploma", "/grap/diploma"})
public class DiplomaController {

    private static final Logger logger = LoggerFactory.getLogger(DiplomaController.class);

    @Autowired
    private DiplomaLoginSimulationService diplomaLoginSimulationService;

    /**
     * 获取配置
     */
    @RequestMapping(value = "config", method = RequestMethod.POST)
    public Object getConfig(@RequestParam String appid,
                            @RequestParam("taskid") Long taskId,
                            @RequestParam(value = "style", required = false) String style) {
        if (StringUtils.isBlank(appid) || taskId == null) {
            logger.error("学信网:获取配置,参数缺失,appid,taskid必传,appid={},taskid={}", appid, taskId);
            throw new IllegalArgumentException("学信网:获取配置,参数缺失,appid,taskid必传");
        }
        logger.info("学信网:获取配置,传入参数,appid={},taskid={}", appid, taskId);
        Object result = diplomaLoginSimulationService.getConfig(appid, style);
        logger.info("学信网:获取配置,返回结果,appid={},taskid={},result={}", appid, taskId, JSON.toJSONString(result));
        return result;
    }

    /**
     * 登陆初始化
     */
    @RequestMapping(value = "/login/init", method = {RequestMethod.POST})
    public Object loginInit(CommonPluginParam educationParam) {
        logger.info("学信网:登陆初始化,传入参数,param={}", JSON.toJSONString(educationParam));
        Object result = diplomaLoginSimulationService.loginInit(educationParam);
        logger.info("学信网:登陆初始化,返回结果,param={},result={}", JSON.toJSONString(educationParam), JSON.toJSONString(result));
        return result;
    }

    /**
     * 登陆
     */
    @RequestMapping(value = "/login/submit", method = {RequestMethod.POST})
    public Object loginSubmit(CommonPluginParam param) {
        logger.info("学信网:登陆,传入参数,param={}", JSON.toJSONString(param));
        if (param == null || param.getTaskId() == null) {
            logger.error("学信网:登陆,参数缺失,taskId必传,param={}", JSON.toJSONString(param));
            throw new IllegalArgumentException("学信网:登陆,参数缺失,taskId必传");
        }
        Object result = diplomaLoginSimulationService.loginSubmit(param);
        logger.info("学信网:登陆,返回结果,param={},result={}", JSON.toJSONString(param), JSON.toJSONString(result));
        return result;
    }

    /**
     * 注册初始化
     */
    @RequestMapping(value = "/register/init", method = {RequestMethod.POST})
    public Object registerInit(CommonPluginParam educationParam) {
        logger.info("学信网:注册初始化,传入参数,param={}", JSON.toJSONString(educationParam));
        Object result = diplomaLoginSimulationService.registerInit(educationParam);
        logger.info("学信网:注册初始化,返回结果,param={},result={}", JSON.toJSONString(educationParam), JSON.toJSONString(result));
        return result;
    }

    /**
     * 注册时刷新图片验证码
     */
    @RequestMapping(value = "/register/refresh/picCode", method = RequestMethod.POST)
    public Object registerRefreshPicCode(CommonPluginParam param) {
        logger.info("学信网:注册时刷新图片验证码,传入参数,param={}", JSON.toJSONString(param));
        Object result = diplomaLoginSimulationService.registerRefreshPicCode(param);
        logger.info("学信网:注册时刷新图片验证码,返回结果,param={},result={}", JSON.toJSONString(param), JSON.toJSONString(result));
        return result;
    }

    /**
     * 注册时验证图片验证码并发送短信
     */
    @RequestMapping(value = "/register/validate/picCode/send/smsCode", method = RequestMethod.POST)
    public Object registerValidatePicCodeAndSendSmsCode(CommonPluginParam param) {
        logger.info("学信网:注册时验证图片验证码并发送短信,传入参数,param={}", JSON.toJSONString(param));
        Object result = diplomaLoginSimulationService.registerValidatePicCodeAndSendSmsCode(param);
        logger.info("学信网:注册时验证图片验证码并发送短信,返回结果,param={},result={}", JSON.toJSONString(param), JSON.toJSONString(result));
        return result;
    }

    /**
     * 注册提交
     */
    @RequestMapping(value = "/register/submit", method = RequestMethod.POST)
    public Object registerSubmit(CommonPluginParam param) {
        logger.info("学信网:注册提交,传入参数,param={}", JSON.toJSONString(param));
        Object result = diplomaLoginSimulationService.registerSubmit(param);
        logger.info("学信网:注册提交,返回结果,param={},result={}", JSON.toJSONString(param), JSON.toJSONString(result));
        return result;
    }

    /**
     * 获取合作方属性值引用
     */
    @RequestMapping(value = "/third/party/reference", method = RequestMethod.POST)
    public Object getThirdPartyReference(@RequestParam("taskid") Long taskId) {
        if (taskId == null) {
            logger.error("学信网:获取合作方属性值引用,参数缺失,taskid必传");
            throw new IllegalArgumentException("学信网:获取合作方属性值引用,参数缺失,taskid必传");
        }
        logger.info("学信网:获取合作方属性值引用,传入参数,taskid={}", taskId);
        Object result = diplomaLoginSimulationService.getThirdPartyReference(taskId);
        logger.info("学信网:获取合作方属性值引用,返回结果,taskId={},result={}", taskId, JSON.toJSONString(result));
        return result;
    }

    @RequestMapping(value = "/register/check",method = RequestMethod.POST)
    public Object registerCheck(Long taskId,String id){
        if (taskId == null) {
            logger.error("学信网:获取合作方属性值引用,参数缺失,taskid必传");
            throw new IllegalArgumentException("学信网:获取合作方属性值引用,参数缺失,taskid必传");
        }
        ChsiUserInfo userInfo = diplomaLoginSimulationService.checkRegister(taskId, id);
        return SimpleResult.successResult(userInfo);
    }

    @RequestMapping(value = "/chsiUserInfo",method = RequestMethod.POST)
    public Object chsiUserInfo(ChsiUserInfo userInfo){
        String id = diplomaLoginSimulationService.saveUserInfo(userInfo);
        if (id == null) {
            return SimpleResult.failResult("用户信息缓存失败");
        }
        return SimpleResult.successResult(id);
    }









}
