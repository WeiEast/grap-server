package com.treefinance.saas.grapserver.biz.service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.datatrees.spider.extra.api.EnterpriseApi;
import com.google.gson.reflect.TypeToken;
import com.treefinance.saas.grapserver.biz.config.DiamondConfig;
import com.treefinance.saas.grapserver.common.enums.EBizType;
import com.treefinance.saas.grapserver.common.enums.ESpiderTopic;
import com.treefinance.saas.grapserver.common.result.SaasResult;
import com.treefinance.toolkit.util.json.GsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author guimeichao
 * @date 2018/12/29
 */
@Service
public class EnterpriseInformationService {

    @Autowired
    private TaskLicenseService taskLicenseService;

    @Autowired
    private DiamondConfig      config;

    @Autowired
    private TaskService        taskService;

    @Autowired
    private AcquisitionService acquisitionService;

    @Resource
    private EnterpriseApi      enterpriseApi;

    private static final Logger logger = LoggerFactory.getLogger(EnterpriseInformationService.class);

    public Long creatTask(String appId, String uniqueId) {
        taskLicenseService.verifyCreateTask(appId, uniqueId, EBizType.ENTERPRISE);
        return taskService.createTask(uniqueId, appId, EBizType.ENTERPRISE.getCode(), null, null, null);
    }

    public Object startCrawler(Long taskid, String extra) {
        Map platToWebsite = GsonUtils.fromJson(config.getOpinionDetectPlatformToWebsite(), new TypeToken<Map>() {}.getType());
        String website = (String) platToWebsite.get("enterprise");
        if (StringUtils.isBlank(website)) {
            return SaasResult.failResult("当前平台不支持!");
        }
        Map param = GsonUtils.fromJson(extra, new TypeToken<Map>() {}.getType());
        String keyword = (String) param.get("business");
        List<Map<String, String>> enterpriseList = enterpriseApi.queryEnterprise(keyword, website);
        StringBuilder extraValue = new StringBuilder();
        enterpriseList.stream().forEach(enterprise -> extraValue.append(enterprise.get("name")).append(":").append(enterprise.get("unique")).append(":")
                .append(enterprise.get("index")).append(";"));
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

}
