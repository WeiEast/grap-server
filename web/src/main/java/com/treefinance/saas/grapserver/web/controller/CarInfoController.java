package com.treefinance.saas.grapserver.web.controller;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.grapserver.biz.service.CarInfoService;
import com.treefinance.saas.grapserver.common.exception.BizException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Good Luck Bro , No Bug !
 * 车辆信息
 *
 * @author haojiahong
 * @date 2018/5/31
 */
@RestController
@RequestMapping(value = {"/car_info", "/grap/car_info"})
public class CarInfoController {

    private static final Logger logger = LoggerFactory.getLogger(CarInfoController.class);

    @Autowired
    private CarInfoService carInfoService;

    /**
     * 车辆信息采集
     *
     * @return
     */
    @RequestMapping(value = "/collect", method = {RequestMethod.POST})
    public Object collect(@RequestParam("appid") String appId,
                          @RequestParam("modelNum") String modelNum) {
        logger.info("车辆信息采集,输入参数:appid={},modelNum={}", appId, modelNum);
        if (StringUtils.isBlank(modelNum)) {
            throw new BizException("车型编码不能为空");
        }
        Long taskId = carInfoService.startCollectTask(appId, modelNum);
        Object result = carInfoService.processCollectTask(taskId, appId, modelNum);
        logger.info("车辆信息采集,返回结果:result={},taskId={},appid={},modelNum={}",
                JSON.toJSONString(result), taskId, appId, modelNum);
        return result;
    }

}
