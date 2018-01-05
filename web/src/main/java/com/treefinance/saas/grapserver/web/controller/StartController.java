package com.treefinance.saas.grapserver.web.controller;

import com.google.common.collect.Maps;
import com.treefinance.saas.grapserver.biz.config.DiamondConfig;
import com.treefinance.saas.grapserver.biz.service.MerchantConfigService;
import com.treefinance.saas.grapserver.biz.service.TaskDeviceService;
import com.treefinance.saas.grapserver.biz.service.TaskLicenseService;
import com.treefinance.saas.grapserver.biz.service.TaskService;
import com.treefinance.saas.grapserver.common.enums.EBizType;
import com.treefinance.saas.grapserver.common.exception.ForbiddenException;
import com.treefinance.saas.grapserver.common.utils.IpUtils;
import com.treefinance.saas.knife.result.SimpleResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

/**
 * start接口
 * Created by yh-treefinance on 2017/10/9.
 */
@RestController
@RequestMapping(value = {"/", "/grap", "/h5", "/grap/h5"})
public class StartController {
    private static final Logger logger = LoggerFactory.getLogger(EcommerceController.class);
    @Autowired
    private TaskService taskServiceImpl;
    @Autowired
    private TaskDeviceService taskDeviceService;
    @Autowired
    private MerchantConfigService merchantConfigService;
    @Autowired
    private TaskLicenseService taskLicenseService;
    @Autowired
    private DiamondConfig diamondConfig;

    /**
     * 创建任务接口
     *
     * @param appid
     * @param uniqueId
     * @param coorType
     * @param deviceInfo
     * @param extra
     * @param source
     * @param bizType
     * @param request
     * @return
     * @throws ForbiddenException
     */
    @RequestMapping(value = "/start", method = {RequestMethod.POST})
    public Object start(@RequestParam("appid") String appid,
                        @RequestParam("uniqueId") String uniqueId,
                        @RequestParam(name = "coorType", required = false) String coorType,
                        @RequestParam("deviceInfo") String deviceInfo,
                        @RequestParam(name = "extra", required = false) String extra,
                        @RequestParam(name = "source", required = false) String source,
                        @RequestParam(name = "bizType", required = false) String bizType,
                        @RequestParam(name = "style", required = false) String style,
                        HttpServletRequest request) throws ForbiddenException, IOException {
        if (StringUtils.isEmpty(bizType)) {
            bizType = (String) request.getAttribute("bizType");
        }
        logger.info("task start : appid={},uniqueId={},coorType={},deviceInfo={},extra={},bizType={},source={}",
                appid, uniqueId, coorType, deviceInfo, extra, bizType, source);
        EBizType eBizType = EBizType.of(bizType);
        taskLicenseService.verifyCreateTask(appid, uniqueId, eBizType);
        Long taskId = taskServiceImpl.createTask(uniqueId, appid, eBizType.getCode(), extra);
        Map<String, Object> map = Maps.newHashMap();
        map.put("taskid", String.valueOf(taskId));
        map.put("color", merchantConfigService.getColorConfig(appid, style));
        map.put("title", diamondConfig.getSdkTitle(eBizType));
        String ipAddress = IpUtils.getIpAddress(request);
        taskDeviceService.create(deviceInfo, ipAddress, coorType, taskId);
        return new SimpleResult<>(map);
    }
}
