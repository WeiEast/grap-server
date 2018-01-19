package com.treefinance.saas.grapserver.web.controller;

import com.alibaba.fastjson.JSON;
import com.datatrees.rawdatacentral.domain.plugin.CommonPluginParam;
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
 * Created by haojiahong on 2017/12/26.
 */
@RestController
@RequestMapping(value = {"/h5/email", "/grap/h5/email",})
public class EmailH5Controller {
    private static final Logger logger = LoggerFactory.getLogger(EmailH5Controller.class);

    @Autowired
    private EmailLoginSimulationService emailLoginSimulationService;

    /**
     * 登陆(异步)
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "/login/submit", method = {RequestMethod.POST})
    public Object login(CommonPluginParam param, String userIp) {
        param.setUserIp(userIp);
        logger.info("邮箱账单:登陆,传入参数,param={},userIp={}", JSON.toJSONString(param), userIp);
        if (param == null || param.getTaskId() == null) {
            logger.error("邮箱账单:登陆,参数缺失,taskId必传,param={}", JSON.toJSONString(param));
            throw new IllegalArgumentException("邮箱账单:登陆,参数缺失,taskId必传");
        }
        Object result = emailLoginSimulationService.login(param);
        logger.info("邮箱账单:登陆,返回结果,param={},result={}", JSON.toJSONString(param), JSON.toJSONString(result));
        return result;
    }

    /**
     * 刷新二维码(异步)
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "/refresh/qr_code", method = {RequestMethod.POST})
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
    @RequestMapping(value = "/qr_code/status", method = {RequestMethod.POST})
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

    /**
     * 是否支持当前ip的省份代理(通用接口)
     *
     * @return true:支持.false:不支持
     */
    public Object supportProvinceProxy(CommonPluginParam param, String userIp) {
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
        logger.info("邮箱账单:查询是否支持当前IP的省份代理,返回结果,param={},result={}", JSON.toJSONString(param), JSON.toJSONString(result));
        return result;
    }


    /**
     * 轮询处理状态接口(通用接口)
     *
     * @param processId
     * @param taskId
     * @return
     */
    @RequestMapping(value = "/process/status", method = {RequestMethod.POST})
    public Object processStatus(@RequestParam("processId") Long processId,
                                @RequestParam("taskId") Long taskId) {
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
