package com.treefinance.saas.grapserver.web.controller;

import com.alibaba.fastjson.JSON;
import com.datatrees.spider.share.domain.CommonPluginParam;
import com.treefinance.saas.grapserver.biz.service.EcommerceLoginSimulationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by haojiahong on 2018/1/11.
 */
@RestController
@RequestMapping(value = {"/h5/ecommerce", "/grap/h5/ecommerce"})
public class EcommerceH5Controller {

    private static final Logger logger = LoggerFactory.getLogger(EcommerceH5Controller.class);

    @Autowired
    private EcommerceLoginSimulationService ecommerceLoginSimulationService;

    /**
     * 获取二维码
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "/refresh/qr_code", method = {RequestMethod.POST})
    public Object refreshQRCode(CommonPluginParam param, String userIp) {
        logger.info("电商:获取二维码,传入参数,param={},userIp={}", JSON.toJSONString(param), userIp);
        if (param == null || param.getTaskId() == null) {
            logger.error("电商:获取二维码,参数缺失,taskId必传,param={}", JSON.toJSONString(param));
            throw new IllegalArgumentException("电商:获取二维码,参数缺失,taskId必传");
        }
        param.setUserIp(userIp);
        Object result = ecommerceLoginSimulationService.refreshQRCode(param);
        logger.info("电商:获取二维码,返回结果,param={},result={}", JSON.toJSONString(param), JSON.toJSONString(result));
        return result;
    }

    /**
     * 轮询获取二维码状态
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "/qr_code/status", method = {RequestMethod.POST})
    public Object queryQRStatus(CommonPluginParam param) {
        logger.info("电商:轮询二维码状态,传入参数,param={}", JSON.toJSONString(param));
        if (param == null || param.getTaskId() == null) {
            logger.error("电商:轮询二维码状态,参数缺失,taskId必传,param={}", JSON.toJSONString(param));
            throw new IllegalArgumentException("电商:轮询二维码状态,参数缺失,taskId必传");
        }
        Object result = ecommerceLoginSimulationService.queryQRStatus(param);
        logger.info("电商:轮询二维码状态,返回结果,param={},result={}", JSON.toJSONString(param), JSON.toJSONString(result));
        return result;
    }


}
