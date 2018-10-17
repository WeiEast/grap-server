package com.treefinance.saas.grapserver.web.controller;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.grapserver.biz.service.TongdunService;
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
 * @author:guoguoyun
 * @date:Created in 2018/10/17下午2:13
 */
@RestController
@RequestMapping(value = {"/car_info", "/grap/car_info"})
public class TongdunController {

    private static final Logger logger = LoggerFactory.getLogger(CarInfoController.class);

    @Autowired
    private TongdunService tongdunService;


    /**
     * 同盾信息采集
     *
     * @return
     */
    @RequestMapping(value = "/collect", method = {RequestMethod.POST})
    public Object collect(@RequestParam("appid") String appId,
        @RequestParam("name") String name,@RequestParam("idcard") String idcard,@RequestParam("modelNum") String modelNum) {
        logger.info("车辆信息采集,输入参数:appid={},modelNum={}", appId, modelNum);
        if (StringUtils.isBlank(modelNum)) {
            throw new BizException("车型编码不能为空");
        }
        Long taskId = tongdunService.startCollectTask(appId, modelNum);
        Object result = tongdunService.processCollectTask(taskId, appId, modelNum);
        logger.info("车辆信息采集,返回结果:result={},taskId={},appid={},modelNum={}",
            JSON.toJSONString(result), taskId, appId, modelNum);
        return result;
    }
}
