package com.treefinance.saas.grapserver.web.controller;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.assistant.exception.FailureInSendingToMQException;
import com.treefinance.saas.grapserver.biz.service.EnterpriseInformationService;
import org.apache.commons.lang.StringUtils;
import com.treefinance.saas.grapserver.common.result.SaasResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author guimeichao
 * @date 2018/12/29
 */
@RestController
@RequestMapping(value = {"/enterprise", "/grap/enterprise"})
public class EnterpriseInformationController {

    private static final Logger logger = LoggerFactory.getLogger(EnterpriseInformationController.class);

    @Autowired
    private EnterpriseInformationService enterpriseInformationService;

    /**
     * 获取工商信息
     *
     * @param appid
     * @param uniqueId
     * @param extra
     * @return
     */
    @RequestMapping(value = "/process", method = {RequestMethod.POST})
    public Object process(@RequestParam String appid, @RequestParam("uniqueId") String uniqueId, @RequestParam("extra") String extra) throws
        FailureInSendingToMQException {
        logger.info("工商信息:获取工商信息,传入参数,appid={},uniqueId={},extra={}", appid, uniqueId, extra);
        if (StringUtils.isEmpty(extra)) {
            logger.error("工商信息:获取工商信息,参数缺失,appid={},uniqueId={},extra={}", appid, uniqueId, extra);
            throw new IllegalArgumentException("工商信息:获取工商信息,参数缺失");
        }
        Long taskId = enterpriseInformationService.creatTask(appid, uniqueId);
        return enterpriseInformationService.startCrawler(taskId, extra);
    }

    @RequestMapping(value = "/pynerStart", method = RequestMethod.POST)
    public Object createTask(@RequestParam String appid, @RequestParam("uniqueId") String uniqueId, @RequestParam("platform") String platform,
        @RequestParam("extra") String extra) {
        Map<String, Object> map = JSON.parseObject(extra);
        String enterpriseName = (String)map.get("business");
        boolean flag = enterpriseInformationService.isStartCrawler(enterpriseName);
        if (!flag) {
            SaasResult<Object> saasResult = (SaasResult<Object>)enterpriseInformationService.getResult(enterpriseName);
            saasResult.setCode(3);
            return saasResult;
        }
        Long taskId = enterpriseInformationService.creatTask(appid, uniqueId);
        return enterpriseInformationService.startPynerCrawler(taskId, platform, extra);
    }

    @RequestMapping(value = "/getEnterpriseData", method = RequestMethod.POST)
    public Object getEnterpriseData(@RequestParam String appid, @RequestParam("uniqueId") String uniqueId, @RequestParam("saasid") Long saasid,
        @RequestParam(value = "enterpriseName") String enterpriseName) {
        return enterpriseInformationService.getEnterpriseData(appid, uniqueId, saasid, enterpriseName);
    }
}
