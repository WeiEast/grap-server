package com.treefinance.saas.grapserver.biz.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.treefinance.saas.grapserver.biz.config.DiamondConfig;
import com.treefinance.saas.grapserver.common.enums.EBizType;
import com.treefinance.saas.grapserver.common.enums.ETaskStatus;
import com.treefinance.saas.grapserver.common.enums.ETongdunData;
import com.treefinance.saas.grapserver.common.enums.ETongdunDetailData;
import com.treefinance.saas.grapserver.common.enums.ETongdunType;
import com.treefinance.saas.grapserver.common.request.TongdunRequest;
import com.treefinance.saas.grapserver.common.result.SaasResult;
import com.treefinance.saas.grapserver.common.result.TongdunData;
import com.treefinance.saas.grapserver.common.result.TongdunDetailData;
import com.treefinance.saas.grapserver.common.result.TongdunDetailResult;
import com.treefinance.saas.grapserver.common.utils.HttpClientUtils;
import com.treefinance.saas.grapserver.common.utils.TongdunDataResolver;
import com.treefinance.saas.grapserver.dao.entity.AppLicense;
import com.treefinance.saas.taskcenter.facade.service.TaskFacade;
import com.treefinance.saas.taskcenter.facade.service.TaskLogFacade;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author:guoguoyun
 * @date:Created in 2018/10/17下午2:22
 */
@Service
public class TongdunService {

    private static final Logger logger = LoggerFactory.getLogger(TongdunService.class);
    @Autowired
    private TaskService taskService;
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

    public Long startCollectTask(String appId, TongdunRequest tongdunRequest) {
        // 使用身份证号当作uniqueId
        taskLicenseService.verifyCreateTask(appId, tongdunRequest.getIdCard(), EBizType.TONGDUN);
        return taskService.createTask(tongdunRequest.getIdCard(), appId, EBizType.TONGDUN.getCode(), null, null, null);
    }

    public Long startCollectDetailTask(String appId, TongdunRequest tongdunRequest) {
        // 使用身份证号当作uniqueId
        taskLicenseService.verifyCreateTask(appId, tongdunRequest.getIdCard(), EBizType.TONGDUN_KANIU);
        return taskService.createTask(tongdunRequest.getIdCard(), appId, EBizType.TONGDUN_KANIU.getCode(), null, null, null);
    }

    public Object processCollectTask(Long taskId, String appId, TongdunRequest tongdunRequest) {
        String url = diamondConfig.getTongdunUrlCollect();
        Map<String, Object> map = new HashMap<>(1);
        map.put("data", JSON.toJSONString(tongdunRequest));
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
            logger.error("调用功夫贷同盾采集任务返回值中任务日志信息存在问题:taskId={},tongdunRequest={},httpResult={}", taskId, tongdunRequest, httpResult);
            taskLogFacade.insert(taskId, "调用功夫贷同盾采集任务返回值中任务日志信息存在问题", new Date(), "调用功夫贷同盾采集任务返回值中任务日志信息存在问题");
            taskFacade.updateTaskStatusWithStep(taskId, ETaskStatus.FAIL.getStatus());
            // 错误日志中
            return SaasResult.failResult("Unexpected exception!");
        }
        // 获取规则评分
        Map<String, Integer> saasRuleScoreDTOMap = getSaasRuleScoreMapFromJson(result);

        // 获取关联手机 身份证 邮箱信息
        JSONObject associated3MDTO = result.getJSONObject("saasAssociated3MDTO");
        // 获取详细数值
        JSONObject summary = result.getJSONObject("saasSummaryDTO");
        ETongdunData[] values = ETongdunData.values();
        List<TongdunData> tongdunDataList = new ArrayList<>(values.length);
        try {
            for (ETongdunData item : values) {
                TongdunData tongdunData = new TongdunData();
                tongdunData.setId(item.getName());
                tongdunData.setValue(TongdunDataResolver.to(summary.getInteger(item.getText())));
                tongdunData.setScore(TongdunDataResolver.to(saasRuleScoreDTOMap.get(item.getText())));
                tongdunData.setDetail(getTongdunDetailData(item, associated3MDTO));

                tongdunDataList.add(tongdunData);
            }
        } catch (Exception e) {
            logger.error("调用功夫贷同盾采集任务返回值中结果存在问题{}", httpResult, e);
            return SaasResult.failResult("查询不到数据!");
        }

        AppLicense license = appLicenseService.getAppLicense(appId);
        taskLogService.insert(taskId, "任务成功", new Date(), "");
        taskFacade.updateTaskStatusWithStep(taskId, ETaskStatus.SUCCESS.getStatus());
        return SaasResult.successEncryptByRSAResult(tongdunDataList, license.getServerPublicKey());
    }

    private TongdunDetailData getTongdunDetailData(ETongdunData item, JSONObject associated3MDTO) {
        TongdunDetailData detail = null;
        if (ETongdunData.MI_MOBILE_3W.equals(item)) {
            List<String> list = getList(associated3MDTO, "phoneAssociatedIdentity3M");
            if (!list.isEmpty()) {
                detail = new TongdunDetailData();
                detail.setIdcardNos(list);
            }

        } else if (ETongdunData.ME_IDCARD_3M.equals(item)) {
            List<String> list = getList(associated3MDTO, "identityAssociatedMail3M");
            if (!list.isEmpty()) {
                detail = new TongdunDetailData();
                detail.setMails(list);
            }
        } else if (ETongdunData.MM_IDCARD_3M.equals(item)) {
            List<String> list = getList(associated3MDTO, "identityAssociatedPhone3M");
            if (!list.isEmpty()) {
                detail = new TongdunDetailData();
                detail.setMobiles(list);
            }
        }
        return detail;
    }

    private List<String> getList(JSONObject associated3MDTO, String identityAssociatedMail3M) {
        String value = associated3MDTO.getString(identityAssociatedMail3M);
        if (StringUtils.isEmpty(value)) {
            return Collections.emptyList();
        }
        return Arrays.asList(value.split(","));
    }

    public Object processCollectDetailTask(Long taskId, String appId, TongdunRequest tongdunRequest) {
        String url = diamondConfig.getTongdunDetailUrlCollect();
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
            logger.error("调用功夫贷同盾采集详细任务返回值中任务日志信息存在问题:taskId={},tongdunRequest={},httpResult={}", taskId, tongdunRequest, httpResult);
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
        try {
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
                        } else if (!Objects.isNull(item.getJSONObject(eTongdunType.getSecondtext()))) {
                            jsonType = item.getJSONObject(eTongdunType.getSecondtext());
                        } else {
                            continue;
                        }
                        for (ETongdunDetailData eTongdunDetailData : ETongdunDetailData.values()) {

                            if (!Objects.isNull(jsonType.get(eTongdunDetailData.getText()))) {
                                secondmap.put(eTongdunDetailData.getName(), TongdunDataResolver.to(jsonType.getInteger(eTongdunDetailData.getText())));
                            } else {
                                secondmap.put(ETongdunDetailData.LevelZ.getName(), TongdunDataResolver.to(jsonType.getInteger(eTongdunDetailData.getText())));
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

        } catch (Exception e) {
            return SaasResult.failResult("查询不到数据!");
        }

        AppLicense license = appLicenseService.getAppLicense(appId);
        taskLogService.insert(taskId, "任务成功", new Date(), "");
        taskFacade.updateTaskStatusWithStep(taskId, ETaskStatus.SUCCESS.getStatus());
        return SaasResult.successEncryptByRSAResult(resultList, license.getServerPublicKey());

    }

    private Map<String, Integer> getSaasRuleScoreMapFromJson(JSONObject result) {
        Map<String, Integer> saasRuleScoreDTOMap = new HashMap<>(8);
        JSONArray scores = result.getJSONArray("saasRuleScoreDTO");
        for (int i = 0, size = scores.size(); i < size; i++) {
            JSONObject item = scores.getJSONObject(i);
            saasRuleScoreDTOMap.put(item.getString("ruleName"), item.getInteger("policyScore"));
        }
        return saasRuleScoreDTOMap;
    }
}