package com.treefinance.saas.grapserver.biz.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.treefinance.saas.grapserver.biz.config.DiamondConfig;
import com.treefinance.saas.grapserver.common.enums.*;
import com.treefinance.saas.grapserver.common.request.TongdunRequest;
import com.treefinance.saas.grapserver.common.result.SaasResult;
import com.treefinance.saas.grapserver.common.result.TongdunData;
import com.treefinance.saas.grapserver.common.result.TongdunDetailResult;
import com.treefinance.saas.grapserver.common.utils.HttpClientUtils;
import com.treefinance.saas.grapserver.common.utils.TongdunDataResolver;
import com.treefinance.saas.grapserver.dao.entity.AppLicense;
import com.treefinance.saas.taskcenter.facade.service.TaskFacade;
import com.treefinance.saas.taskcenter.facade.service.TaskLogFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.ValidationException;
import java.util.*;

/**
 * @author guoguoyun
 * @date Created in 2018/10/17下午2:22
 */
@Service
public class TongdunService {

    private static final Logger logger = LoggerFactory.getLogger(TongdunService.class);

    @Autowired
    private SaasTaskService saasTaskService;
    @Autowired
    private TaskFacade taskFacade;
    @Autowired
    private TaskLogFacade taskLogFacade;
    @Autowired
    private TaskLicenseService taskLicenseService;
    @Autowired
    private TaskLogService taskLogService;
    @Autowired
    private AppLicenseService appLicenseService;
    @Autowired
    private DiamondConfig diamondConfig;

    public Long startCollectTask(String appId, TongdunRequest tongdunRequest) throws ValidationException {
        // 使用身份证号当作uniqueId
        taskLicenseService.verifyCreateSaasTask(appId, tongdunRequest.getIdCard(), EBizType.TONGDUN);
        return saasTaskService.createTask(tongdunRequest.getIdCard(), appId, EBizType.TONGDUN.getCode(), null, null, null);
    }

    public Long startCollectDetailTask(String appId, TongdunRequest tongdunRequest) throws ValidationException {
        // 使用身份证号当作uniqueId
        taskLicenseService.verifyCreateSaasTask(appId, tongdunRequest.getIdCard(), EBizType.TONGDUN_KANIU);
        return saasTaskService.createTask(tongdunRequest.getIdCard(), appId, EBizType.TONGDUN_KANIU.getCode(), null, null,
            null);
    }

    public Object processCollectTask(Long taskId, String appId, TongdunRequest tongdunRequest) {
        String url = diamondConfig.getTongdunUrlCollect();
        JSONObject data = JSON.parseObject(JSON.toJSONString(tongdunRequest));
        Map<String, Object> map = new HashMap<>(1);
        map.put("data", data);
        String httpResult;
        try {
            httpResult = HttpClientUtils.doPost(url, map);
        } catch (Exception e) {
            logger.error("调用功夫贷同盾采集任务异常:taskId={},tongdunRequset={}", taskId, tongdunRequest, e);
            taskLogFacade.insert(taskId, "调用功夫贷同盾采集任务异常", new Date(), "调用功夫贷同盾采集任务异常");
            taskFacade.updateTaskStatusWithStep(taskId, ETaskStatus.FAIL.getStatus());
            return SaasResult.failResult("Unexpected exception!");
        }

        JSONObject result = null;
        try {
            result = JSON.parseObject(httpResult);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        if (result == null) {
            logger.error("调用功夫贷同盾采集任务返回值中任务日志信息存在问题:taskId={},tongdunRequest={},httpResult={}", taskId, tongdunRequest,
                httpResult);
            taskLogFacade.insert(taskId, "调用功夫贷同盾采集任务返回值中任务日志信息存在问题", new Date(), "调用功夫贷同盾采集任务返回值中任务日志信息存在问题");
            taskFacade.updateTaskStatusWithStep(taskId, ETaskStatus.FAIL.getStatus());
            // 错误日志中
            return SaasResult.failResult("Unexpected exception!");
        }

        Map<String, Integer> saasRuleScoreDTOMap = new HashMap<>(8);
        // 获取规则评分
        JSONArray scores = result.getJSONArray("saasRuleScoreDTO");
        for (int i = 0, size = scores.size(); i < size; i++) {
            JSONObject item = scores.getJSONObject(i);
            saasRuleScoreDTOMap.put(item.getString("ruleName"), item.getInteger("policyScore"));
        }

        // 获取详细数值
        JSONObject summary = result.getJSONObject("saasSummaryDTO");
        ETongdunData[] values = ETongdunData.values();
        List<TongdunData> tongdunDataList = new ArrayList<>(values.length);

        for (ETongdunData item : values) {
            TongdunData tongdunData = new TongdunData();
            tongdunData.setId(item.getName());
            tongdunData.setValue(TongdunDataResolver.to(summary.getInteger(item.getText())));
            tongdunData.setScore(TongdunDataResolver.to(saasRuleScoreDTOMap.get(item.getText())));
            tongdunDataList.add(tongdunData);
        }

        AppLicense license = appLicenseService.getAppLicense(appId);
        taskLogService.insert(taskId, "任务成功", new Date(), "");
        taskFacade.updateTaskStatusWithStep(taskId, ETaskStatus.SUCCESS.getStatus());
        return SaasResult.successEncryptByRSAResult(tongdunDataList, license.getServerPublicKey());
    }

    public Object processCollectDetailTask(Long taskId, String appId, TongdunRequest tongdunRequest) {
        String url = diamondConfig.getTongdunUrlCollect();
        JSONObject data = JSON.parseObject(JSON.toJSONString(tongdunRequest));
        Map<String, Object> map = new HashMap<>(1);
        map.put("data", data);
        String httpResult;
        try {
            httpResult = HttpClientUtils.doPost(url, map);
        } catch (Exception e) {
            logger.error("调用功夫贷同盾采集详细任务异常:taskId={},tongdunRequset={}", taskId, tongdunRequest, e);
            taskLogFacade.insert(taskId, "调用功夫贷同盾采集任务异常", new Date(), "调用功夫贷同盾采集任务异常");
            taskFacade.updateTaskStatusWithStep(taskId, ETaskStatus.FAIL.getStatus());
            return SaasResult.failResult("Unexpected exception!");
        }

        JSONObject result = null;
        try {
            result = JSON.parseObject(httpResult);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        if (result == null) {
            logger.error("调用功夫贷同盾采集详细任务返回值中任务日志信息存在问题:taskId={},tongdunRequest={},httpResult={}", taskId,
                tongdunRequest, httpResult);
            taskLogFacade.insert(taskId, "调用功夫贷同盾采集详细任务返回值中任务日志信息存在问题", new Date(), "调用功夫贷同盾采集任务返回值中任务日志信息存在问题");
            taskFacade.updateTaskStatusWithStep(taskId, ETaskStatus.FAIL.getStatus());
            // 错误日志中
            return SaasResult.failResult("Unexpected exception!");
        }

        List<Object> resultList = new ArrayList<>(6);
        JSONObject detail = result.getJSONObject("details");

        // 获取详细数值
        JSONObject summary = result.getJSONObject("saasSummaryDTO");
        ETongdunType[] types = ETongdunType.values();
        List<TongdunDetailResult> tongdunDataList = new ArrayList<>(5);

           for (int i = 1; i < 6; i++) {
               if (summary.getInteger(ETongdunData.getText((byte)i)) != 0) {
                   TongdunDetailResult tongdunDetailResult = new TongdunDetailResult();
                   JSONObject item = detail.getJSONObject(ETongdunData.getText((byte)i));
                   tongdunDetailResult.setId(ETongdunData.getName((byte)i));
                   tongdunDetailResult.setValue(TongdunDataResolver.to(summary.getInteger(ETongdunData.getText((byte)i))));
                   Map<String, Map> firstmap = new HashMap<>();
                   for (ETongdunType eTongdunType : types) {

                       Map<String, String> secondmap = new HashMap<>();
                       JSONObject jsonType;
                       if (!Objects.isNull(item.getJSONObject(eTongdunType.getText()))) {
                           jsonType = item.getJSONObject(eTongdunType.getText());
                       } else {
                           jsonType = item.getJSONObject(eTongdunType.getSecondtext());
                       }
                       for (ETongdunDetailData eTongdunDetailData : ETongdunDetailData.values()) {

                           if (!Objects.isNull(jsonType.get(eTongdunDetailData.getText()))) {
                               secondmap.put(eTongdunDetailData.getName(), TongdunDataResolver.to(jsonType.getInteger(eTongdunDetailData.getText())));
                           }
                       }
                       firstmap.put(eTongdunType.getName(), secondmap);

                   }

                   tongdunDetailResult.setDetails(firstmap);
                   tongdunDataList.add(tongdunDetailResult);
               }
           }

           // 获取黑名单
           Map blackMap = new HashMap(2);
           blackMap.put("id", "IS_BLACK");
           blackMap.put("value", summary.get("isHitDiscreditPolicy"));
           resultList.addAll(tongdunDataList);
           resultList.add(blackMap);


        AppLicense license = appLicenseService.getAppLicense(appId);
        taskLogService.insert(taskId, "任务成功", new Date(), "");
        taskFacade.updateTaskStatusWithStep(taskId, ETaskStatus.SUCCESS.getStatus());
        return SaasResult.successEncryptByRSAResult(resultList, license.getServerPublicKey());

    }
}