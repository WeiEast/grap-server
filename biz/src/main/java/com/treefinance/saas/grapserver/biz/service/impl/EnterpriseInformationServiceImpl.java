package com.treefinance.saas.grapserver.biz.service.impl;

import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.datatrees.spider.extra.api.EnterpriseApi;
import com.datatrees.spider.operator.domain.OperatorLoginConfig;
import com.datatrees.spider.share.api.WebsiteApi;
import com.datatrees.spider.share.domain.CommonPluginParam;
import com.datatrees.spider.share.domain.FormType;
import com.datatrees.spider.share.domain.WebLoginConfig;
import com.datatrees.spider.share.domain.http.HttpResult;
import com.google.gson.reflect.TypeToken;
import com.treefinance.saas.grapserver.biz.mq.DirectiveResult;
import com.treefinance.saas.grapserver.biz.mq.MQConfig;
import com.treefinance.saas.grapserver.biz.mq.MessageProducer;
import com.treefinance.saas.grapserver.biz.mq.SuccessRemark;
import com.treefinance.saas.grapserver.biz.service.AcquisitionService;
import com.treefinance.saas.grapserver.biz.service.EnterpriseInformationService;
import com.treefinance.saas.grapserver.biz.service.TaskAttributeService;
import com.treefinance.saas.grapserver.common.enums.ESpiderTopic;
import com.treefinance.saas.grapserver.common.enums.ETaskStatus;
import com.treefinance.saas.grapserver.common.result.SaasResult;
import com.treefinance.saas.grapserver.context.component.AbstractService;
import com.treefinance.saas.grapserver.context.config.DiamondConfig;
import com.treefinance.saas.grapserver.exception.CrawlerBizException;
import com.treefinance.saas.grapserver.facade.enums.ETaskAttribute;
import com.treefinance.saas.grapserver.manager.TaskManager;
import com.treefinance.saas.grapserver.manager.domain.TaskBO;
import com.treefinance.saas.knife.result.SimpleResult;
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
    private DiamondConfig        config;

    @Autowired
    private AcquisitionService   acquisitionService;

    @Resource
    private EnterpriseApi        enterpriseApi;

    @Resource
    private WebsiteApi           websiteApi;

    @Autowired
    private EnterpriseService    enterpriseService;

    @Autowired
    private TaskAttributeService taskAttributeService;

    @Autowired
    private TaskManager          taskManager;

    @Autowired
    private MessageProducer      messageProducer;

    @Autowired
    private MQConfig             mqConfig;

    @Override
    public Object prepare(Long taskId, String extra) {
        Map param = GsonUtils.fromJson(extra, new TypeToken<Map>() {}.getType());
        String keyword = (String) param.get("business");
        CommonPluginParam commonPluginParam = new CommonPluginParam();
        commonPluginParam.setTaskId(taskId);
        commonPluginParam.setGroupCode("ENTERPRISE_INFORMATION");
        commonPluginParam.setGroupName("企业工商信息");
        commonPluginParam.setUsername(keyword);
        HttpResult<WebLoginConfig> result;
        try {
            result = websiteApi.preLogin(commonPluginParam);
        } catch (Exception e) {
            logger.error("工商信息:调用爬数获取工商信息配置信息异常,commonPluginParam={}", JSON.toJSONString(commonPluginParam), e);
            throw e;
        }
        if (!result.getStatus()) {
            logger.info("工商信息:调用爬数获取工商信息配置信息失败,commonPluginParam={},result={}", JSON.toJSONString(commonPluginParam), JSON.toJSONString(result));
            throw new CrawlerBizException(result.getResponseCode(), result.getMessage());
        }
        if (result.getData() != null && StringUtils.isNotBlank(result.getData().getUsername()) &&
                StringUtils.isNotBlank(result.getData().getWebsiteName())) {
            String accountNo = result.getData().getUsername();
            String websiteName = result.getData().getWebsiteName();
            taskManager.setAccountNoAndWebsite(taskId, accountNo, websiteName);
        } else {
            logger.info("工商信息:调用爬数获取工商信息accountNo,websiteName为空,taskId={},commonPluginParam={},result={}", taskId,
                    JSON.toJSONString(commonPluginParam), JSON.toJSONString(result));
        }
        String groupCode = commonPluginParam.getGroupCode();
        String groupName = commonPluginParam.getGroupName();
        taskAttributeService.insertOrUpdateSelective(taskId, ETaskAttribute.OPERATOR_GROUP_CODE.getAttribute(), groupCode);
        taskAttributeService.insertOrUpdateSelective(taskId, ETaskAttribute.OPERATOR_GROUP_NAME.getAttribute(), groupName);
        commonPluginParam.setWebsiteName(result.getData().getWebsiteName());
        commonPluginParam.setFormType(FormType.LOGIN);
        return startCrawler(commonPluginParam);
    }

    @Override
    public Object startCrawler(CommonPluginParam commonPluginParam) {
        HttpResult<Object> result;
        Long taskId = commonPluginParam.getTaskId();
        try {
            result = websiteApi.submit(commonPluginParam);
        } catch (Exception e) {
            logger.error("工商信息:调用爬数获取工商信息爬取异常,commonPluginParam={}", JSON.toJSONString(commonPluginParam), e);
            throw e;
        }
        if (!result.getStatus()) {
            messageProducer.send(JSON.toJSONString(new DirectiveResult(taskId, DirectiveResult.Directive.task_fail, null)),
                    mqConfig.getProduceDirectiveTopic(), mqConfig.getProduceDirectiveTag(), null);
            logger.warn("工商信息:调用爬数获取工商信息爬取失败,taskId={},result={}", taskId, JSON.toJSONString(result));
            return SaasResult.successResult(taskId);
        }
        if (result.getData() == null) {
            // 回调操作
            messageProducer.send(JSON.toJSONString(new DirectiveResult(taskId, DirectiveResult.Directive.task_success,
                            JSON.toJSONString(new SuccessRemark(taskId, "enterprise", 0, null, 0, Clock.systemUTC().millis(), (byte) 1)))),
                    mqConfig.getProduceDirectiveTopic(), mqConfig.getProduceDirectiveTag(), null);
            logger.info("mq发送成功，taskId={},topic={},tag={}", taskId, mqConfig.getProduceDirectiveTopic(), mqConfig.getProduceDirectiveTag());
            return SaasResult.successResult(taskId);
        }
        logger.info("工商信息:调用爬数获取工商信息爬取成功,commonPluginParam={},result={}", JSON.toJSONString(commonPluginParam), JSON.toJSONString(result));
        return SimpleResult.successResult(taskId);
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
        String website = (String) platToWebsite.get(platform);
        if (StringUtils.isBlank(website)) {
            return SaasResult.failResult("当前平台不支持!");
        }
        Map param = GsonUtils.fromJson(extra, new TypeToken<Map>() {}.getType());
        String keyword = (String) param.get("business");
        List<Map<String, String>> enterpriseList = enterpriseApi.queryEnterprise(keyword, website, taskid);
        if (enterpriseList == null) {
            return SaasResult.failure(-2, "可能由于代理问题未查询企业失败");
        }
        boolean flag = false;
        StringBuilder extraValue = new StringBuilder();
        logger.info("企查查返回列表 enterpriseList={},size={}", JSONObject.toJSONString(enterpriseList), enterpriseList.size());
        if (enterpriseList.size() == 0) {
            SaasResult<Object> saasResult = SaasResult.successResult("没有对应的企业");
            saasResult.setCode(4);
            return saasResult;
        }
        Map<String, String> enterprise = enterpriseList.get(0);
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
            extraValue.append(enterprise.get("name")).append(":").append(enterprise.get("unique")).append(":").append(enterprise.get("index"))
                    .append(";");
            flag = true;
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
