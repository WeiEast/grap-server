package com.treefinance.saas.grapserver.web.controller;

import com.google.common.collect.Maps;
import com.treefinance.saas.grapserver.common.model.Result;
import com.treefinance.saas.grapserver.biz.config.DiamondConfig;
import com.treefinance.saas.grapserver.common.exception.ForbiddenException;
import com.treefinance.saas.grapserver.biz.service.*;
import com.treefinance.saas.grapserver.biz.service.TaskLicenseService;
import com.treefinance.saas.grapserver.common.enums.EBizType;
import com.treefinance.saas.grapserver.common.utils.IpUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by luoyihua on 2017/4/27.
 */
@RestController
@RequestMapping("/ecommerce")
public class EcommerceController {
    private static final Logger logger = LoggerFactory.getLogger(EcommerceController.class);

    @Autowired
    private TaskServiceImpl taskServiceImpl;
    @Autowired
    private MerchantConfigService merchantConfigService;
    @Autowired
    private TaskDeviceService taskDeviceService;
    @Autowired
    private AcquisitionService acquisitionService;
    @Autowired
    private TaskLicenseService taskLicenseService;
    @Autowired
    private DiamondConfig diamondConfig;

    /**
     * 通知服务端生成taskId
     *
     * @param appid
     * @param uniqueId
     * @return
     */
    @RequestMapping(value = "/start", method = {RequestMethod.POST})
    public Object createTask(@RequestParam("appid") String appid, @RequestParam("uniqueId") String uniqueId,
                             @RequestParam(name = "coorType", required = false) String coorType, @RequestParam("deviceInfo") String deviceInfo,
                             @RequestParam(name = "extra", required = false) String extra,
                             HttpServletRequest request) throws ForbiddenException {
        logger.info("createTask : appid={},uniqueId={},coorType={},deviceInfo={},extra={}",appid,uniqueId,coorType,deviceInfo,extra);
        taskLicenseService.verifyCreateTask(appid, EBizType.ECOMMERCE);
        Long taskId = taskServiceImpl.createTask(uniqueId, appid, EBizType.ECOMMERCE.getCode());
        Map<String, Object> map = Maps.newHashMap();
        map.put("taskid", taskId);
        map.put("color", merchantConfigService.getColorConfig(appid));
        map.put("title", diamondConfig.getSdkTitle(EBizType.ECOMMERCE));
        String ipAddress = IpUtils.getIpAddress(request);
        taskDeviceService.create(deviceInfo, ipAddress, coorType, taskId);
        return new Result<>(map);
    }

    /**
     * 发消息到mq
     * 登陆成功之后调用
     * @param taskid
     * @param header
     * @param cookie
     * @param url
     * @param website
     * @return
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/acquisition", method = {RequestMethod.POST})
    public Object sendToMq(@RequestParam("taskid") Long taskid, @RequestParam(value = "header", required = false) String header,
                           @RequestParam("cookie") String cookie, @RequestParam("url") String url,
                           @RequestParam("website") String website,
                           @RequestParam(value = "accountNo", required = false) String accountNo) {
        logger.info("电商-发消息：acquisition，taskid={},header={},cookie={},url={},website={},accountNo={}",taskid, header, cookie, url, website, accountNo);
        acquisitionService.acquisition(taskid, header, cookie, url, website, accountNo);
        return new Result<>();
    }

    @RequestMapping(value = "/acquisition/process", method = {RequestMethod.POST})
    public Object loginProcess(@RequestParam("taskid") Long taskid,
                               @RequestParam("directiveId") String directiveId,
                               @RequestParam(value = "html", required = false) String html,
                               @RequestParam(value = "cookie", required = false) String cookie) throws Exception {
        if (StringUtils.isEmpty(html) && StringUtils.isEmpty(cookie)) {
            throw new Exception("html和cookie不能同时为空");
        }
        logger.info("电商-loginProcess: taskid={},directiveId={},html={},cookie={}",taskid, directiveId, cookie, html, cookie);
        acquisitionService.loginProcess(directiveId,taskid, html, cookie);
        return new Result<>();
    }

}
