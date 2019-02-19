package com.treefinance.saas.grapserver.biz.service.impl;

import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.fastjson.JSON;
import com.datatrees.spider.extra.api.EnterpriseApi;
import com.google.gson.reflect.TypeToken;
import com.treefinance.saas.grapserver.biz.mq.DirectiveResult;
import com.treefinance.saas.grapserver.biz.mq.MQConfig;
import com.treefinance.saas.grapserver.biz.mq.MessageProducer;
import com.treefinance.saas.grapserver.biz.mq.SuccessRemark;
import com.treefinance.saas.grapserver.biz.service.AcquisitionService;
import com.treefinance.saas.grapserver.biz.service.EnterpriseInformationService;
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

import java.time.Clock;
import java.util.*;

/**
 * @author guimeichao
 * @date 2018/12/29
 */
@Service("enterpriseInformationService")
public class EnterpriseInformationServiceImpl extends AbstractService implements EnterpriseInformationService {

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

    @Autowired
    private MessageProducer messageProducer;

    @Autowired
    private MQConfig mqConfig;

    @Override
    public Object startCrawler(Long taskid, String extra) {
        Map platToWebsite = GsonUtils.fromJson(config.getOpinionDetectPlatformToWebsite(), new TypeToken<Map>() {}.getType());
        String website = (String)platToWebsite.get("enterprise");
        if (StringUtils.isBlank(website)) {
            return SaasResult.failResult("当前平台不支持!");
        }
        Map param = GsonUtils.fromJson(extra, new TypeToken<Map>() {}.getType());
        String keyword = (String)param.get("business");
        List<Map<String, String>> enterpriseList = enterpriseApi.queryEnterprise(keyword, website, taskid);
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
            logger.warn("调用EnterpriseApi返回空列表，taskId={}", taskid);
            return SaasResult.successResult(taskid);
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
            String name = enterpriseName;
            if (name.contains("（")) {
                name = name.replaceAll("(\\（)", "(");
            }
            if (name.contains("）")) {
                name = name.replaceAll("(\\）)", ")");
            }
            result = enterpriseService.getEnterpriseDate(name);
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
        List<Map<String, String>> enterpriseList = enterpriseApi.queryEnterprise(keyword, website, taskid);
        if (enterpriseList == null) {
            return SaasResult.failure(-2, "可能由于代理问题未查询企业失败");
        }
        boolean flag = false;
        StringBuilder extraValue = new StringBuilder();
        logger.info("");
        for (Map<String, String> enterprise : enterpriseList) {
            // 修改公司中带括号的情况
            String name = enterprise.get("name");
            logger.info("企查查返回列表中公司name={},unique={}", name, enterprise.get("unique"));
            if (name.contains("(")) {
                name = name.replaceAll("(\\()", "（");
            }
            if (name.contains(")")) {
                name = name.replaceAll("(\\))", "）");
            }

            if (keyword.equals(name)) {
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
        TaskBO task = taskManager.getTaskById(taskid);
        if (ETaskStatus.RUNNING.getStatus().equals(task.getStatus())) {
            return SaasResult.failResult(null, "任务还在进行中...", 1);
        }
        if (enterpriseName.contains("（")) {
            enterpriseName = enterpriseName.replaceAll("(\\（)", "(");
        }
        if (enterpriseName.contains("）")) {
            enterpriseName = enterpriseName.replaceAll("(\\）)", ")");
        }
        EnterpriseDataResultDTO resultDTO = getResult(enterpriseName);
        if (enterpriseName.contains("(")) {
            enterpriseName = enterpriseName.replaceAll("(\\()", "（");
        }
        if (enterpriseName.contains(")")) {
            enterpriseName = enterpriseName.replaceAll("(\\))", "）");
        }
        resultDTO.setEnterpriseName(enterpriseName);
        return SaasResult.successResult(resultDTO);
    }

    @Override
    public EnterpriseDataResultDTO getResult(String enterpriseName) {
        try {
            return enterpriseService.getEnterpriseDate(enterpriseName);
        } catch (RpcException e) {
            logger.error("调用dubbo服务失败", e);
            throw new RuntimeException("获取企业信息数据失败");
        }
    }

}
