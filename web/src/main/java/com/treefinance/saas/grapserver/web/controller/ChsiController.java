package com.treefinance.saas.grapserver.web.controller;

import com.alibaba.fastjson.JSON;
import com.datatrees.rawdatacentral.domain.education.EducationParam;
import com.treefinance.saas.grapserver.biz.service.ChsiLoginSimulationService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 学信网抓取
 * Created by haojiahong on 2017/12/11.
 */
@RestController
@RequestMapping(value = {"/chsi", "/h5/chsi", "/grap/h5/chsi", "/grap/chsi"})
public class ChsiController {
    private static final Logger logger = LoggerFactory.getLogger(ChsiController.class);

    @Autowired
    private ChsiLoginSimulationService chsiLoginSimulationService;

    /**
     * 登陆初始化
     *
     * @param educationParam
     * @return
     */
    @RequestMapping(value = "/login/init", method = {RequestMethod.POST})
    public Object loginInit(EducationParam educationParam) {
        logger.info("学信网:登陆初始化,传入参数,param={}", JSON.toJSONString(educationParam));
        Object result = chsiLoginSimulationService.loginInit(educationParam);
        logger.info("运营商:登陆初始化,返回结果,param={},result={}", JSON.toJSONString(educationParam), JSON.toJSONString(result));
        return result;
    }

    /**
     * 登陆
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "/login/submit", method = {RequestMethod.POST})
    public Object loginSubmit(EducationParam param) {
        logger.info("学信网:登陆,传入参数,param={}", JSON.toJSONString(param));
        if (param == null || param.getTaskId() == null || StringUtils.isBlank(param.getLoginName())) {
            logger.error("学信网:登陆,参数缺失,taskId,loginName必传,param={}", JSON.toJSONString(param));
            throw new IllegalArgumentException("学信网:登陆,参数缺失,taskId,loginName必传");
        }
        Object result = chsiLoginSimulationService.loginSubmit(param);
        logger.info("学信网:登陆,返回结果,param={},result={}", JSON.toJSONString(param), JSON.toJSONString(result));
        return result;
    }

    /**
     * 注册时刷新图片验证码
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "/register/refresh/picCode", method = RequestMethod.POST)
    public Object registerRefreshPicCode(EducationParam param) {
        logger.info("学信网:注册时刷新图片验证码,传入参数,param={}", JSON.toJSONString(param));
        Object result = chsiLoginSimulationService.registerRefreshPicCode(param);
        logger.info("学信网:注册时刷新图片验证码,返回结果,param={},result={}", JSON.toJSONString(param), JSON.toJSONString(result));
        return result;
    }

    /**
     * 注册时验证图片验证码并发送短信
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "/register/validate/picCode/send/smsCode", method = RequestMethod.POST)
    public Object registerValidatePicCodeAndSendSmsCode(EducationParam param) {
        logger.info("学信网:注册时验证图片验证码并发送短信,传入参数,param={}", JSON.toJSONString(param));
        Object result = chsiLoginSimulationService.registerValidatePicCodeAndSendSmsCode(param);
        logger.info("学信网:注册时验证图片验证码并发送短信,返回结果,param={},result={}", JSON.toJSONString(param), JSON.toJSONString(result));
        return result;
    }

    /**
     * 注册提交
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "/register/submit", method = RequestMethod.POST)
    public Object registerSubmit(EducationParam param) {
        logger.info("学信网:注册提交,传入参数,param={}", JSON.toJSONString(param));
        Object result = chsiLoginSimulationService.registerSubmit(param);
        logger.info("学信网:注册提交,返回结果,param={},result={}", JSON.toJSONString(param), JSON.toJSONString(result));
        return result;
    }


}
