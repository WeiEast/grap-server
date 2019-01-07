package com.treefinance.saas.grapserver.biz.service.impl;

import com.alibaba.dubbo.rpc.RpcException;
import com.datatrees.spider.extra.api.EnterpriseApi;
import com.google.gson.reflect.TypeToken;
import com.treefinance.saas.grapserver.biz.service.AcquisitionService;
import com.treefinance.saas.grapserver.biz.service.EnterpriseInformationService;
import com.treefinance.saas.grapserver.biz.service.TaskLicenseService;
import com.treefinance.saas.grapserver.biz.service.TaskService;
import com.treefinance.saas.grapserver.common.enums.EBizType;
import com.treefinance.saas.grapserver.common.enums.ESpiderTopic;
import com.treefinance.saas.grapserver.common.enums.ETaskStatus;
import com.treefinance.saas.grapserver.common.result.SaasResult;
import com.treefinance.saas.grapserver.context.component.AbstractService;
import com.treefinance.saas.grapserver.context.config.DiamondConfig;
import com.treefinance.saas.grapserver.manager.TaskManager;
import com.treefinance.saas.grapserver.manager.domain.TaskBO;
import com.treefinance.saas.processor.thirdparty.facade.enterprise.EnterpriseService;
import com.treefinance.saas.processor.thirdparty.facade.enterprise.model.EnterpriseDataResultDTO;
import com.treefinance.toolkit.util.json.GsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author guimeichao
 * @date 2018/12/29
 */
@Service("enterpriseInformationService")
public class EnterpriseInformationServiceImpl extends AbstractService implements EnterpriseInformationService {

    @Autowired
    private TaskLicenseService taskLicenseService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private DiamondConfig config;

    @Autowired
    private AcquisitionService acquisitionService;

    @Resource
    private EnterpriseApi enterpriseApi;

    @Autowired
    private EnterpriseService enterpriseService;

    @Autowired
    private TaskManager taskManager;

    @Override
    public Long creatTask(String appId, String uniqueId) {
        taskLicenseService.verifyCreateTask(appId, uniqueId, EBizType.ENTERPRISE);
        return taskService.createTask(uniqueId, appId, EBizType.ENTERPRISE.getCode(), null, null, null);
    }

    @Override
    public Object startCrawler(Long taskid, String extra) {
        Map platToWebsite = GsonUtils.fromJson(config.getOpinionDetectPlatformToWebsite(), new TypeToken<Map>() {}.getType());
        String website = (String)platToWebsite.get("enterprise");
        if (StringUtils.isBlank(website)) {
            return SaasResult.failResult("当前平台不支持!");
        }
        Map param = GsonUtils.fromJson(extra, new TypeToken<Map>() {}.getType());
        String keyword = (String)param.get("business");
        List<Map<String, String>> enterpriseList = enterpriseApi.queryEnterprise(keyword, website);
        StringBuilder extraValue = new StringBuilder();
        enterpriseList.stream()
            .forEach(enterprise -> extraValue.append(enterprise.get("name")).append(":").append(enterprise.get("unique")).append(":").append(enterprise.get("index")).append(";"));
        if (StringUtils.isBlank(extraValue)) {
            return SaasResult.failResult("企业列表为空");
        }
        Map<String, String> extraMap = new HashMap<>();
        extraMap.put("business", extraValue.toString());
        extra = GsonUtils.toJson(extraMap);
        logger.info("工商信息-发消息：acquisition，taskid={},extra={}", taskid, extra);
        acquisitionService.acquisition(taskid, null, null, null, website, null, ESpiderTopic.SPIDER_EXTRA.name().toLowerCase(), extra);
        return SaasResult.successResult(taskid);
    }

    @Override
    public boolean isStartCrawler(String enterpriseName) {
        EnterpriseDataResultDTO result;
        try {
            result = enterpriseService.getEnterpriseDate(enterpriseName);
            if (result == null) {
                return true;
            }
        } catch (RpcException e) {
            logger.error("调用dubbo服务失败", e);
            return true;
        }
        Date date = result.getCrawlerDate();
        long second = Long.parseLong(config.getWebdetectSecond());
        return date.getTime() < (System.currentTimeMillis() - second * 1000);
    }

    @Override
    public Object startPynerCrawler(Long taskid, String platform, String extra) {
        Map platToWebsite = GsonUtils.fromJson(config.getOpinionDetectPlatformToWebsite(), new TypeToken<Map>() {}.getType());
        String website = (String)platToWebsite.get(platform);
        if (StringUtils.isBlank(website)) {
            return SaasResult.failResult("当前平台不支持!");
        }
        Map param = GsonUtils.fromJson(extra, new TypeToken<Map>() {}.getType());
        String keyword = (String)param.get("business");
        List<Map<String, String>> enterpriseList = enterpriseApi.queryEnterprise(keyword, website);
        boolean flag = false;
        StringBuilder extraValue = new StringBuilder();
        for (Map<String, String> enterprise : enterpriseList) {
            if (keyword.equals(enterprise.get("name"))) {
                extraValue.append(enterprise.get("name")).append(":").append(enterprise.get("unique")).append(":").append(enterprise.get("index")).append(";");
                flag = true;
                break;
            }
        }
        if (!flag) {
            SaasResult<Object> saasResult = SaasResult.successResult("没有对应的企业");
            saasResult.setCode(4);
            return saasResult;
        }
        Map<String, String> extraMap = new HashMap<>();
        extraMap.put("business", extraValue.toString());
        extra = GsonUtils.toJson(extraMap);
        logger.info("工商信息-发消息：acquisition，taskid={},extra={}", taskid, extra);
        acquisitionService.acquisition(taskid, null, null, null, website, null, ESpiderTopic.SPIDER_EXTRA.name().toLowerCase(), extra);
        return SaasResult.successResult(taskid);
    }

    @Override
    public Object getEnterpriseData(String appId, String uniqueId, Long taskid, String enterpriseName) {
        taskLicenseService.verifyCreateSaasTask(appId, uniqueId, EBizType.ENTERPRISE);
        TaskBO task = taskManager.getTaskById(taskid);
        if (ETaskStatus.RUNNING.getStatus().equals(task.getStatus())) {
            return SaasResult.failResult(null, "任务还在进行中...", 1);
        }
        return getResult(enterpriseName);
    }

    @Override
    public Object getResult(String enterpriseName) {
        try {
            EnterpriseDataResultDTO result = enterpriseService.getEnterpriseDate(enterpriseName);
            return SaasResult.successResult(result);
        } catch (RpcException e) {
            logger.error("调用dubbo服务失败", e);
            return SaasResult.failResult("获取企业信息数据失败");
        }
    }

}
