package com.treefinance.saas.grapserver.web.controller;

import com.alibaba.fastjson.JSON;
import com.datatrees.spider.share.domain.CommonPluginParam;
import com.treefinance.saas.grapserver.biz.service.EmailLoginSimulationService;
import com.treefinance.saas.knife.result.Results;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author haojiahong on 2017/12/26.
 */
@RestController
@RequestMapping(value = {"/h5/email", "/grap/h5/email",})
public class EmailH5Controller {

    private static final Logger logger = LoggerFactory.getLogger(EmailH5Controller.class);

    @Autowired
    private EmailLoginSimulationService emailLoginSimulationService;

    //================================================ QQ邮箱 ================================================== //

    /**
     * 登陆(异步)
     */
    @RequestMapping(value = {"/login/submit", "/qq/login/submit"}, method = {RequestMethod.POST})
    public Object login(CommonPluginParam param, String userIp) {
        logger.info("邮箱账单:登陆,传入参数,param={},userIp={}", JSON.toJSONString(param), userIp);
        if (param == null || param.getTaskId() == null) {
            logger.error("邮箱账单:登陆,参数缺失,taskId必传,param={}", JSON.toJSONString(param));
            throw new IllegalArgumentException("邮箱账单:登陆,参数缺失,taskId必传");
        }
        param.setUserIp(userIp);
        Object result = emailLoginSimulationService.login(param);
        logger.info("邮箱账单:登陆,返回结果,param={},result={}", JSON.toJSONString(param), JSON.toJSONString(result));
        return result;
    }

    /**
     * 刷新二维码(异步)
     */
    @RequestMapping(value = {"/refresh/qr_code", "/qq/refresh/qr_code"}, method = {RequestMethod.POST})
    public Object refreshQRCode(CommonPluginParam param) {
        logger.info("邮箱账单:刷新二维码,传入参数,param={}", JSON.toJSONString(param));
        if (param == null || param.getTaskId() == null) {
            logger.error("邮箱账单:刷新二维码,参数缺失,taskId必传,param={}", JSON.toJSONString(param));
            throw new IllegalArgumentException("邮箱账单:刷新二维码,参数缺失,taskId必传");
        }
        Object result = emailLoginSimulationService.refreshQRCode(param);
        logger.info("邮箱账单:刷新二维码,返回结果,param={},result={}", JSON.toJSONString(param), JSON.toJSONString(result));
        return result;
    }


    /**
     * 查询二维码状态
     *
     * @param param
     * @return
     */
    @RequestMapping(value = {"/qr_code/status", "/qq/qr_code/status"}, method = {RequestMethod.POST})
    public Object queryQRStatus(CommonPluginParam param) {
        logger.info("邮箱账单:查询二维码状态,传入参数,param={}", JSON.toJSONString(param));
        if (param == null || param.getTaskId() == null) {
            logger.error("邮箱账单:查询二维码状态,参数缺失,taskId必传,param={}", JSON.toJSONString(param));
            throw new IllegalArgumentException("邮箱账单:查询二维码状态,参数缺失,taskId必传");
        }
        Object result = emailLoginSimulationService.queryQRStatus(param);
        logger.info("邮箱账单:查询二维码状态,返回结果,param={},result={}", JSON.toJSONString(param), JSON.toJSONString(result));
        return result;
    }

    //================================================ qq企业邮箱 ================================================== //

    /**
     * 登陆(异步)
     */
    @RequestMapping(value = {"/qq/exmail/login/submit"}, method = {RequestMethod.POST})
    public Object loginForQQExMail(CommonPluginParam param, String userIp) {
        logger.info("邮箱账单:登陆,传入参数,param={},userIp={}", JSON.toJSONString(param), userIp);
        if (param == null || param.getTaskId() == null) {
            logger.error("邮箱账单:登陆,参数缺失,taskId必传,param={}", JSON.toJSONString(param));
            throw new IllegalArgumentException("邮箱账单:登陆,参数缺失,taskId必传");
        }
        param.setUserIp(userIp);
        Object result = emailLoginSimulationService.loginForQQExMail(param);
        logger.info("邮箱账单:登陆,返回结果,param={},result={}", JSON.toJSONString(param), JSON.toJSONString(result));
        return result;
    }

    /**
     * 登陆初始化
     */
    @RequestMapping(value = "/qq/exmail/login/init", method = {RequestMethod.POST})
    public Object loginInitForQQExMail(CommonPluginParam param) {
        logger.info("邮箱账单:登陆初始化,传入参数,param={}", JSON.toJSONString(param));
        Object result = emailLoginSimulationService.loginInitForQQExMail(param);
        logger.info("邮箱账单:登陆初始化,返回结果,param={},result={}", JSON.toJSONString(param), JSON.toJSONString(result));
        return result;
    }

    //================================================ 163邮箱 ================================================== //

    /**
     * 登陆(异步)
     */
    @RequestMapping(value = {"/163/login/submit"}, method = {RequestMethod.POST})
    public Object loginFor163(CommonPluginParam param, String userIp) {
        if (param == null || param.getTaskId() == null) {
            logger.error("邮箱账单:登陆,参数缺失,taskId必传,param={}", JSON.toJSONString(param));
            throw new IllegalArgumentException("邮箱账单:登陆,参数缺失,taskId必传");
        }
        param.setUserIp(userIp);
        logger.info("邮箱账单:登陆,传入参数,param={},userIp={}", JSON.toJSONString(param), userIp);
        Object result = emailLoginSimulationService.loginFor163(param);
        logger.info("邮箱账单:登陆,返回结果,param={},result={}", JSON.toJSONString(param), JSON.toJSONString(result));
        return result;
    }

    /**
     * 刷新二维码(异步)
     */
    @RequestMapping(value = {"/163/refresh/qr_code"}, method = {RequestMethod.POST})
    public Object refreshQRCodeFor163(CommonPluginParam param) {
        logger.info("邮箱账单:刷新二维码,传入参数,param={}", JSON.toJSONString(param));
        if (param == null || param.getTaskId() == null) {
            logger.error("邮箱账单:刷新二维码,参数缺失,taskId必传,param={}", JSON.toJSONString(param));
            throw new IllegalArgumentException("邮箱账单:刷新二维码,参数缺失,taskId必传");
        }
        Object result = emailLoginSimulationService.refreshQRCodeFor163(param);
        logger.info("邮箱账单:刷新二维码,返回结果,param={},result={}", JSON.toJSONString(param), JSON.toJSONString(result));
        return result;
    }


    /**
     * 查询二维码状态
     */
    @RequestMapping(value = {"/163/qr_code/status"}, method = {RequestMethod.POST})
    public Object queryQRStatusFor163(CommonPluginParam param) {
        logger.info("邮箱账单:查询二维码状态,传入参数,param={}", JSON.toJSONString(param));
        if (param == null || param.getTaskId() == null) {
            logger.error("邮箱账单:查询二维码状态,参数缺失,taskId必传,param={}", JSON.toJSONString(param));
            throw new IllegalArgumentException("邮箱账单:查询二维码状态,参数缺失,taskId必传");
        }
        Object result = emailLoginSimulationService.queryQRStatusFor163(param);
        logger.info("邮箱账单:查询二维码状态,返回结果,param={},result={}", JSON.toJSONString(param), JSON.toJSONString(result));
        return result;
    }

    //================================================ 126邮箱 ================================================== //

    /**
     * 登陆(异步)
     */
    @RequestMapping(value = {"/126/login/submit"}, method = {RequestMethod.POST})
    public Object loginFor126(CommonPluginParam param, String userIp) {
        if (param == null || param.getTaskId() == null) {
            logger.error("邮箱账单:登陆,参数缺失,taskId必传,param={}", JSON.toJSONString(param));
            throw new IllegalArgumentException("邮箱账单:登陆,参数缺失,taskId必传");
        }
        param.setUserIp(userIp);
        logger.info("邮箱账单:登陆,传入参数,param={},userIp={}", JSON.toJSONString(param), userIp);
        Object result = emailLoginSimulationService.loginFor126(param);
        logger.info("邮箱账单:登陆,返回结果,param={},result={}", JSON.toJSONString(param), JSON.toJSONString(result));
        return result;
    }

    /**
     * 刷新二维码(异步)
     */
    @RequestMapping(value = {"/126/refresh/qr_code"}, method = {RequestMethod.POST})
    public Object refreshQRCodeFor126(CommonPluginParam param) {
        logger.info("邮箱账单:刷新二维码,传入参数,param={}", JSON.toJSONString(param));
        if (param == null || param.getTaskId() == null) {
            logger.error("邮箱账单:刷新二维码,参数缺失,taskId必传,param={}", JSON.toJSONString(param));
            throw new IllegalArgumentException("邮箱账单:刷新二维码,参数缺失,taskId必传");
        }
        Object result = emailLoginSimulationService.refreshQRCodeFor126(param);
        logger.info("邮箱账单:刷新二维码,返回结果,param={},result={}", JSON.toJSONString(param), JSON.toJSONString(result));
        return result;
    }


    /**
     * 查询二维码状态
     */
    @RequestMapping(value = {"/126/qr_code/status"}, method = {RequestMethod.POST})
    public Object queryQRStatusFor126(CommonPluginParam param) {
        logger.info("邮箱账单:查询二维码状态,传入参数,param={}", JSON.toJSONString(param));
        if (param == null || param.getTaskId() == null) {
            logger.error("邮箱账单:查询二维码状态,参数缺失,taskId必传,param={}", JSON.toJSONString(param));
            throw new IllegalArgumentException("邮箱账单:查询二维码状态,参数缺失,taskId必传");
        }
        Object result = emailLoginSimulationService.queryQRStatusFor126(param);
        logger.info("邮箱账单:查询二维码状态,返回结果,param={},result={}", JSON.toJSONString(param), JSON.toJSONString(result));
        return result;
    }

    //================================================ 新浪邮箱 ================================================== //

    /**
     * 登陆初始化
     */
    @RequestMapping(value = "/sina/login/init", method = {RequestMethod.POST})
    public Object loginInitForSina(CommonPluginParam param) {
        logger.info("邮箱账单:登陆初始化,传入参数,param={}", JSON.toJSONString(param));
        Object result = emailLoginSimulationService.loginInitForSina(param);
        logger.info("邮箱账单:登陆初始化,返回结果,param={},result={}", JSON.toJSONString(param), JSON.toJSONString(result));
        return result;
    }

    /**
     * 登陆(轮询,有可能需要验证码登录)
     */
    @RequestMapping(value = "/sina/login/submit", method = {RequestMethod.POST})
    public Object loginForSina(CommonPluginParam param) {
        if (param == null || param.getTaskId() == null) {
            logger.error("邮箱账单:登陆,参数缺失,taskId必传,param={}", JSON.toJSONString(param));
            throw new IllegalArgumentException("邮箱账单:登陆,参数缺失,taskId必传");
        }
        logger.info("邮箱账单:登陆,传入参数,param={}", JSON.toJSONString(param));
        Object result = emailLoginSimulationService.loginForSina(param);
        logger.info("邮箱账单:登陆,返回结果,param={},result={}", JSON.toJSONString(param), JSON.toJSONString(result));
        return result;
    }


    /**
     * 刷新图片验证码
     */
    @RequestMapping(value = "/sina/refresh/picCode", method = RequestMethod.POST)
    public Object refreshPicCodeForSina(CommonPluginParam param) {
        logger.info("邮箱账单:刷新图片验证码,传入参数,param={}", JSON.toJSONString(param));
        Object result = emailLoginSimulationService.refreshPicCodeForSina(param);
        logger.info("邮箱账单:刷新图片验证码,返回结果,param={},result={}", JSON.toJSONString(param), JSON.toJSONString(result));
        return result;
    }


    //================================================ 通用接口 ================================================== //

    /**
     * 是否支持当前ip的省份代理(通用接口)
     *
     * @return true:支持.false:不支持
     */
    @RequestMapping(value = "/support/province_proxy", method = {RequestMethod.POST})
    public Object supportProvinceProxy(@RequestParam(required = false) CommonPluginParam param, String userIp) {
        if (StringUtils.isBlank(userIp)) {
            logger.info("邮箱账单:查询是否支持当前IP的省份代理,没有获取到当前用户的IP信息");
            return Results.newSuccessResult(false);
        }
        if (param == null) {
            param = new CommonPluginParam();
        }
        param.setUserIp(userIp);
        logger.info("邮箱账单:查询是否支持当前IP的省份代理,传入参数,param={}", JSON.toJSONString(param));
        Object result = emailLoginSimulationService.supportProvinceProxy(param);
        logger.info("邮箱账单:查询是否支持当前IP的省份代理,返回结果,param={},result={}",
                JSON.toJSONString(param), JSON.toJSONString(result));
        return result;
    }


    /**
     * 轮询处理状态接口(通用接口)
     */
    @RequestMapping(value = "/process/status", method = {RequestMethod.POST})
    public Object processStatus(@RequestParam("processId") Long processId, @RequestParam("taskId") Long taskId) {
        logger.info("邮箱账单:轮询处理状态,传入参数,processId={},taskId={}", processId, taskId);
        if (taskId == null) {
            logger.error("邮箱账单:轮询处理状态,参数缺失,taskId必传");
            throw new IllegalArgumentException("邮箱账单:轮询处理状态,参数缺失,taskId必传");
        }
        Object result = emailLoginSimulationService.processStatus(processId, taskId);
        logger.info("邮箱账单:轮询处理状态,返回结果,processId={},taskId={},result={}", processId, taskId, JSON.toJSONString(result));
        return result;
    }

}
