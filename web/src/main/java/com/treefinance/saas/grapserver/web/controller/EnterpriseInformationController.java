package com.treefinance.saas.grapserver.web.controller;

import com.treefinance.saas.grapserver.biz.service.EnterpriseInformationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
     * @param appid
     * @param uniqueId
     * @param extra
     * @return
     */
    @RequestMapping(value = "/process", method = {RequestMethod.POST})
    public Object process(@RequestParam String appid, @RequestParam("uniqueId") String uniqueId, @RequestParam("extra") String extra) {
        logger.info("工商信息:获取工商信息,传入参数,appid={},uniqueId={},extra={}", appid, uniqueId, extra);
        if (extra == null) {
            logger.error("工商信息:获取工商信息,参数缺失,appid={},uniqueId={},extra={}", appid, uniqueId, extra);
            throw new IllegalArgumentException("工商信息:获取工商信息,参数缺失");
        }
        Long taskId = enterpriseInformationService.creatTask(appid, uniqueId);
        return enterpriseInformationService.startCrawler(taskId, extra);
    }
}
