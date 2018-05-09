package com.treefinance.saas.grapserver.web.controller;

import com.alibaba.fastjson.JSON;
import com.datatrees.rawdatacentral.domain.operator.OperatorParam;
import com.treefinance.saas.grapserver.biz.service.OperatorExtendLoginService;
import com.treefinance.saas.grapserver.common.enums.EBizType;
import com.treefinance.saas.knife.result.SimpleResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@RestController
@RequestMapping(value = {"/operator", "/h5/operator", "/grap/h5/operator", "/grap/operator"})
public class OperatorController {
    private static final Logger logger = LoggerFactory.getLogger(OperatorController.class);

    @Autowired
    private OperatorExtendLoginService operatorExtendLoginService;


    /**
     * 创建任务
     *
     * @return
     */
    @RequestMapping(value = "/start", method = {RequestMethod.POST})
    public ModelAndView createTask() {
        ModelAndView modelAndView = new ModelAndView("forward:/start");
        modelAndView.addObject("bizType", EBizType.OPERATOR.getText());
        return modelAndView;
    }

    /**
     * 运营商获取配置
     * 返回商户配置信息,运营商分组信息,以及各运营商登录配置信息
     *
     * @param appid
     * @return
     */
    @RequestMapping(value = "/config", method = {RequestMethod.POST})
    public Object getConfig(@RequestParam String appid,
                            @RequestParam("taskid") Long taskId,
                            @RequestParam(value = "style", required = false) String style) {
        if (StringUtils.isBlank(appid) || taskId == null) {
            logger.error("运营商:获取配置,参数缺失,appid,taskid必传,appid={},taskId={}", appid, taskId);
            throw new IllegalArgumentException("运营商:获取配置,参数缺失,appid,taskid必传");
        }
        Map<String, Object> map = operatorExtendLoginService.getConfig(appid, taskId, style);
        return SimpleResult.successResult(map);
    }

    /**
     * 获取商户配置与运营商分组信息
     *
     * @param appid  商户id
     * @param taskId 任务id
     * @param style  颜色风格配置
     * @return
     */
    @RequestMapping(value = "/config/groups", method = {RequestMethod.POST})
    public Object getConfigAndGroups(@RequestParam String appid,
                                     @RequestParam("taskid") Long taskId,
                                     @RequestParam(value = "style", required = false) String style) {
        if (StringUtils.isBlank(appid) || taskId == null) {
            logger.error("运营商:获取商户配置与运营商分组信息,参数缺失,appid,taskid必传,appid={},taskId={}", appid, taskId);
            throw new IllegalArgumentException("运营商:获取配置,参数缺失,appid,taskid必传");
        }
        Object result = operatorExtendLoginService.getConfigAndGroups(appid, taskId, style);
        logger.info("运营商:获取商户配置与运营商分组信息,返回结果,result={}", JSON.toJSONString(result));
        return result;
    }

    /**
     * 获取运营商登陆配置信息
     *
     * @param operatorParam
     * @return
     */
    @RequestMapping(value = "/config/prelogin", method = {RequestMethod.POST})
    public Object preLoginConfig(OperatorParam operatorParam) {
        logger.info("运营商:获取运营商登陆配置信息,传入参数,operatorParam={}", JSON.toJSONString(operatorParam));
        Object result = operatorExtendLoginService.preLoginConfig(operatorParam);
        logger.info("运营商:获取运营商登陆配置信息,返回结果,operatorParam={},result={}", JSON.toJSONString(operatorParam), JSON.toJSONString(result));
        return result;
    }


    /**
     * 根据输入号码查找该号码的归属地
     *
     * @param mobile
     * @return
     */
    @RequestMapping(value = "/mobile/attribution", method = {RequestMethod.POST})

    public Object mobileAttribution(@RequestParam("mobile") String mobile) {
        Map<String, Object> map = operatorExtendLoginService.getMobileAttribution(mobile);
        return SimpleResult.successResult(map);
    }

    /**
     * 准备登陆(登陆初始化)
     *
     * @param operatorParam
     * @return
     */
    @RequestMapping(value = "/loginpage/prepare", method = {RequestMethod.POST})
    public Object prepare(OperatorParam operatorParam) {
        logger.info("运营商:准备登陆(登陆初始化),获取基本信息,传入参数,operatorParam={}", JSON.toJSONString(operatorParam));
        if (operatorParam == null || operatorParam.getTaskId() == null || StringUtils.isBlank(operatorParam.getWebsiteName())
                || operatorParam.getMobile() == null || StringUtils.isBlank(operatorParam.getGroupCode())
                || StringUtils.isBlank(operatorParam.getGroupName())) {
            logger.error("运营商:准备登陆(登陆初始化),获取基本信息,参数缺失,taskId,websiteName,mobile,groupCode,groupName必传,operatorParam={}",
                    JSON.toJSONString(operatorParam));
            throw new IllegalArgumentException("运营商:准备登陆(登陆初始化),获取基本信息,参数缺失,taskId,websiteName,mobile,groupCode,groupName必传");
        }
        Object result = operatorExtendLoginService.prepare(operatorParam);
        logger.info("运营商:准备登陆(登陆初始化),获取基本信息,返回结果,operatorParam={},result={}", JSON.toJSONString(operatorParam), JSON.toJSONString(result));
        return result;
    }

    /**
     * 刷新图片验证码
     *
     * @param operatorParam
     * @return
     */
    @RequestMapping(value = "/loginpage/pic/captcha", method = {RequestMethod.POST})
    public Object picCaptcha(OperatorParam operatorParam) {
        logger.info("运营商:刷新图片验证码,传入参数,operatorParam={}", JSON.toJSONString(operatorParam));
        Object result = operatorExtendLoginService.refreshPicCode(operatorParam);
        logger.info("运营商:刷新图片验证码,返回结果,operatorParam={},result={}", JSON.toJSONString(operatorParam), JSON.toJSONString(result));
        return result;
    }

    /**
     * 刷新短信验证码
     *
     * @param operatorParam
     * @return
     */
    @RequestMapping(value = "/loginpage/sms/captcha", method = {RequestMethod.POST})
    public Object smsCaptcha(OperatorParam operatorParam) {
        logger.info("运营商:刷新短信验证码,传入参数,operatorParam={}", JSON.toJSONString(operatorParam));
        Object result = operatorExtendLoginService.refreshSmsCode(operatorParam);
        logger.info("运营商:刷新短信验证码,返回结果,operatorParam={},result={}", JSON.toJSONString(operatorParam), JSON.toJSONString(result));
        return result;
    }


    /**
     * 登陆
     *
     * @param operatorParam
     * @return
     */
    @RequestMapping(value = "/loginpage/submit", method = {RequestMethod.POST})
    public Object login(OperatorParam operatorParam) {
        logger.info("运营商:登陆,传入参数,operatorParam={}", JSON.toJSONString(operatorParam));
        if (operatorParam == null || operatorParam.getTaskId() == null) {
            logger.error("运营商:登陆,参数缺失,taskId必传,operatorParam={}", JSON.toJSONString(operatorParam));
            throw new IllegalArgumentException("运营商:登陆,参数缺失,taskId必传");
        }
        Object result = operatorExtendLoginService.login(operatorParam);
        logger.info("运营商:登陆,返回结果,operatorParam={},result={}", JSON.toJSONString(operatorParam), JSON.toJSONString(result));
        return result;
    }
}
