package com.treefinance.saas.grapserver.web.controller;

import com.alibaba.fastjson.JSON;
import com.datatrees.rawdatacentral.domain.plugin.CommonPluginParam;
import com.treefinance.saas.grapserver.biz.service.EmailLoginSimulationService;
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
     * 登陆
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "/login/submit", method = {RequestMethod.POST})
    public Object login(CommonPluginParam param) {
        logger.info("邮箱账单:登陆,传入参数,param={}", JSON.toJSONString(param));
        if (param == null || param.getTaskId() == null) {
            logger.error("邮箱账单:登陆,参数缺失,taskId必传,param={}", JSON.toJSONString(param));
            throw new IllegalArgumentException("邮箱账单:登陆,参数缺失,taskId必传");
        }
        Object result = emailLoginSimulationService.login(param);
        logger.info("邮箱账单:登陆,返回结果,param={},result={}", JSON.toJSONString(param), JSON.toJSONString(result));
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
            throw new IllegalArgumentException("邮箱账单:轮询登陆状态,参数缺失,taskId必传");
        }
        Object result = emailLoginSimulationService.processStatus(processId, taskId);
        logger.info("邮箱账单:轮询登陆状态,返回结果,processId={},taskId={},result={}", processId, taskId, JSON.toJSONString(result));
        return result;
    }


}
