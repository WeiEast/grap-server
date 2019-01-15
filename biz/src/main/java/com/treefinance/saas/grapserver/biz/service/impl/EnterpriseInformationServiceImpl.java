package com.treefinance.saas.grapserver.biz.service.impl;

import java.time.Clock;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.datatrees.spider.extra.api.EnterpriseApi;
import com.google.gson.reflect.TypeToken;
import com.treefinance.saas.grapserver.biz.mq.DirectiveResult;
import com.treefinance.saas.grapserver.biz.mq.MQConfig;
import com.treefinance.saas.grapserver.biz.mq.MessageProducer;
import com.treefinance.saas.grapserver.biz.mq.SuccessRemark;
import com.treefinance.saas.grapserver.biz.service.AcquisitionService;
import com.treefinance.saas.grapserver.biz.service.EnterpriseInformationService;
import com.treefinance.saas.grapserver.biz.service.TaskLicenseService;
import com.treefinance.saas.grapserver.biz.service.TaskService;
import com.treefinance.saas.grapserver.common.enums.EBizType;
import com.treefinance.saas.grapserver.common.enums.ESpiderTopic;
import com.treefinance.saas.grapserver.common.result.SaasResult;
import com.treefinance.saas.grapserver.context.component.AbstractService;
import com.treefinance.saas.grapserver.context.config.DiamondConfig;
import com.treefinance.toolkit.util.json.GsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Autowired
    private MessageProducer messageProducer;

    @Autowired
    private MQConfig mqConfig;

    @Autowired
    private EnterpriseApi enterpriseApi;

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
        if (enterpriseList == null) {
            // 回调操作
            messageProducer.send(
                JSON.toJSONString(new DirectiveResult(taskid, DirectiveResult.Directive.task_success,
                    JSON.toJSONString(new SuccessRemark(taskid, "enterprise", 0, null, 0, Clock.systemUTC().millis(), (byte)1)))),
                mqConfig.getProduceDirectiveTopic(), mqConfig.getProduceDirectiveTag(), null);
            logger.info("mq发送成功，taskId={},topic={},tag={}", taskid, mqConfig.getProduceDirectiveTopic(), mqConfig.getProduceDirectiveTag());
            return SaasResult.successResult(taskid);
        }
        StringBuilder extraValue = new StringBuilder();
        enterpriseList.stream()
            .forEach(enterprise -> extraValue.append(enterprise.get("name")).append(":").append(enterprise.get("unique")).append(":").append(enterprise.get("index")).append(";"));
        if (StringUtils.isBlank(extraValue)) {
            messageProducer.send(JSON.toJSONString(new DirectiveResult(taskid, DirectiveResult.Directive.task_fail, null)), mqConfig.getProduceDirectiveTopic(),
                mqConfig.getProduceDirectiveTag(), null);
            logger.warn("调用EnterpriseApi返回空列表，taskId={}",taskid);
            return SaasResult.successResult(taskid);
        }
        Map<String, String> extraMap = new HashMap<>();
        extraMap.put("business", extraValue.toString());
        extra = GsonUtils.toJson(extraMap);
        logger.info("工商信息-发消息：acquisition，taskid={},extra={}", taskid, extra);
        acquisitionService.acquisition(taskid, null, null, null, website, null, ESpiderTopic.SPIDER_EXTRA.name().toLowerCase(), extra);
        return SaasResult.successResult(taskid);
    }
}
