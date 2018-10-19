package com.treefinance.saas.grapserver.biz.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.treefinance.saas.grapserver.biz.config.DiamondConfig;
import com.treefinance.saas.grapserver.common.enums.EBizType;
import com.treefinance.saas.grapserver.common.enums.ETaskStatus;
import com.treefinance.saas.grapserver.common.enums.ETongdunData;
import com.treefinance.saas.grapserver.common.request.TongdunRequest;
import com.treefinance.saas.grapserver.common.result.TongdunData;
import com.treefinance.saas.grapserver.common.utils.HttpClientUtils;
import com.treefinance.saas.grapserver.common.utils.TongdunDataResolver;
import com.treefinance.saas.grapserver.dao.entity.AppLicense;
import com.treefinance.saas.knife.result.SimpleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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
            taskLogService.insert(taskId, "调用功夫贷同盾采集任务异常", new Date(), "调用功夫贷同盾采集任务异常");
            taskService.updateTaskStatus(taskId, ETaskStatus.FAIL.getStatus());
            return SimpleResult.failResult("Unexpected exception!");
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
            taskLogService.insert(taskId, "调用功夫贷同盾采集任务返回值中任务日志信息存在问题", new Date(), "调用功夫贷同盾采集任务返回值中任务日志信息存在问题");
            taskService.updateTaskStatus(taskId, ETaskStatus.FAIL.getStatus());
            // 错误日志中
            return SimpleResult.failResult("Unexpected exception!");
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
            tongdunData.setValue(item.getText());
            if (saasRuleScoreDTOMap.get(item.getText()) == null) {
                tongdunData.setScore(TongdunDataResolver.encode(0));
            } else {

                tongdunData.setScore(TongdunDataResolver.encode(Integer.valueOf(saasRuleScoreDTOMap.get(item.getText()))));
            }
            tongdunDataList.add(tongdunData);
        }

        AppLicense license = appLicenseService.getAppLicense(appId);
        taskLogService.insert(taskId, "任务成功", new Date(), "");
        taskService.updateTaskStatus(taskId, ETaskStatus.SUCCESS.getStatus());
        return SimpleResult.successEncryptByRSAResult(tongdunDataList, license.getServerPublicKey());
    }

}