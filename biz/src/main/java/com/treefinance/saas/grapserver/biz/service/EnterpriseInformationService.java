package com.treefinance.saas.grapserver.biz.service;

import java.time.Clock;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.datatrees.spider.extra.api.EnterpriseApi;
import com.google.gson.reflect.TypeToken;
import com.treefinance.saas.grapserver.biz.config.DiamondConfig;
import com.treefinance.saas.grapserver.biz.mq.DirectiveResult;
import com.treefinance.saas.grapserver.biz.mq.MQConfig;
import com.treefinance.saas.grapserver.biz.mq.MessageProducer;
import com.treefinance.saas.grapserver.biz.mq.SuccessRemark;
import com.treefinance.saas.grapserver.common.enums.EBizType;
import com.treefinance.saas.grapserver.common.enums.ESpiderTopic;
import com.treefinance.saas.grapserver.common.exception.FailureInSendingToMQException;
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

    @Autowired
    private MessageProducer messageProducer;

    @Autowired
    private MQConfig mqConfig;

    @Autowired
    private EnterpriseApi enterpriseApi;

    private static final Logger logger = LoggerFactory.getLogger(EnterpriseInformationService.class);

    public Long creatTask(String appId, String uniqueId) {
        taskLicenseService.verifyCreateTask(appId, uniqueId, EBizType.ENTERPRISE);
        return taskService.createTask(uniqueId, appId, EBizType.ENTERPRISE.getCode(), null, null, null);
    }

    public Object startCrawler(Long taskid, String extra) throws FailureInSendingToMQException {
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
            logger.info("mq发送成功，taskId={},topic={},tag={}",taskid,mqConfig.getProduceDirectiveTopic(),mqConfig.getProduceDirectiveTag());
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
