package com.treefinance.saas.grapserver.web.controller;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.grapserver.biz.service.TongdunService;
import com.treefinance.saas.grapserver.common.exception.BizException;
import com.treefinance.saas.grapserver.common.request.TongdunRequest;
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
@RequestMapping(value = {"/loan/special/ss"})
public class TongdunController {

    private static final Logger logger = LoggerFactory.getLogger(CarInfoController.class);

    @Autowired
    private TongdunService tongdunService;

    /**
     * 同盾信息采集
     *
     * @return
     */
    @RequestMapping(value = "/query", method = {RequestMethod.POST})
    public Object collect(@RequestParam("appid") String appId, @RequestParam("name") String name,
        @RequestParam("idcard") String idcard, @RequestParam("mobile") String mobile,
        @RequestParam(value = "email", required = false) String email) {
        if (StringUtils.isBlank(name) || StringUtils.isBlank(idcard) || StringUtils.isBlank(mobile)) {
            throw new BizException("姓名，身份证号，手机号必填信息不能为空");
        }
        logger.info("同盾信息采集,输入参数:appid={},name={},idcard={},mobile={}", appId, name, idcard, mobile);
        TongdunRequest tongdunRequest = new TongdunRequest();
        tongdunRequest.setUserName(name);
        tongdunRequest.setIdCard(idcard);
        tongdunRequest.setTelNum(mobile);
        if (StringUtils.isNotBlank(email)) {
            tongdunRequest.setAccountEmail(email);
            logger.info("同盾信息采集,输入参数:email={}", email);
        }
        Long taskId = tongdunService.startCollectTask(appId,tongdunRequest);
        Object result = tongdunService.processCollectTask(taskId, appId,tongdunRequest);
        logger.info("同盾信息采集,返回结果:result={},taskId={},appid={}", JSON.toJSONString(result), taskId, appId
        );
        return result;
    }



    /**
     * 同盾详细信息采集
     *
     * @return
     */
    @RequestMapping(value = "/query/detail", method = {RequestMethod.POST})
    public Object collectDetail(@RequestParam("appid") String appId, @RequestParam("name") String name,
        @RequestParam("idcard") String idcard, @RequestParam("mobile") String mobile,
        @RequestParam(value = "email", required = false) String email) {
        if (StringUtils.isBlank(name) || StringUtils.isBlank(idcard) || StringUtils.isBlank(mobile)) {
            throw new BizException("姓名，身份证号，手机号必填信息不能为空");
        }
        logger.info("同盾详细信息采集,输入参数:appid={},name={},idcard={},mobile={}", appId, name, idcard, mobile);
        TongdunRequest tongdunRequest = new TongdunRequest();
        tongdunRequest.setUserName(name);
        tongdunRequest.setIdCard(idcard);
        tongdunRequest.setTelNum(mobile);
        if (StringUtils.isNotBlank(email)) {
            tongdunRequest.setAccountEmail(email);
            logger.info("同盾详细信息采集,输入参数:email={}", email);
        }
        Long taskId = tongdunService.startCollectDetailTask(appId,tongdunRequest);
        Object result = tongdunService.processCollectDetailTask(taskId, appId,tongdunRequest);
        logger.info("同盾详细信息采集,返回结果:result={},taskId={},appid={}", JSON.toJSONString(result), taskId, appId
        );
        return result;
    }
}
