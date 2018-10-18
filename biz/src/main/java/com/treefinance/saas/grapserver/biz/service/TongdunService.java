package com.treefinance.saas.grapserver.biz.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONArray;
import com.treefinance.saas.grapserver.biz.config.DiamondConfig;
import com.treefinance.saas.grapserver.biz.service.monitor.MonitorService;
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
    @Autowired private TaskService taskService;
    @Autowired private TaskLicenseService taskLicenseService;
    @Autowired private TaskLogService taskLogService;
    @Autowired private MonitorService monitorService;
    @Autowired private AppLicenseService appLicenseService;
    @Autowired private DiamondConfig diamondConfig;

    public Long startCollectTask(String appId, TongdunRequest tongdunRequest) {
        // 使用身份证号当作uniqueId
        taskLicenseService.verifyCreateTask(appId, tongdunRequest.getIdCard(), EBizType.TONGDUN);
        Long taskId =
            taskService.createTask(tongdunRequest.getIdCard(), appId, EBizType.TONGDUN.getCode(), null, null, null);
        return taskId;
    }

    public Object processCollectTask(Long taskId, String appId, TongdunRequest tongdunRequest) {
        String url = "http://risk-third-party.test.91gfd.cn/td/saas/query";
        JSONObject data = JSON.parseObject(JSON.toJSONString(tongdunRequest));
        Map map = new HashMap();
        map.put("data", data);
        String httpResult;
        try {
            httpResult = HttpClientUtils.doPost(url, map);

        } catch (Exception e) {
            logger.error("调用功夫贷同盾采集任务异常:taskId={},tongdunRequset={}", taskId, tongdunRequest, e);
            taskLogService.insert(taskId, "调用功夫贷同盾采集任务异常", new Date(), "调用功夫贷同盾采集任务异常");
            taskService.updateTaskStatus(taskId, ETaskStatus.FAIL.getStatus());
            return SimpleResult.failResult("功夫贷同盾采集任务采集失败");
        }
        JSONObject result = JSON.parseObject(httpResult);
        JSONArray saasRuleScoreDTOArrays = JSONArray.parseArray(result.get("saasRuleScoreDTO").toString());
        Map<String, String> saasRuleScoreDTOMap = new HashMap();
        for (int i = 0; i < saasRuleScoreDTOArrays.size(); i++) {

            JSONObject saasRuleScoreDTO = JSONObject.parseObject(saasRuleScoreDTOArrays.get(i).toString());
            saasRuleScoreDTOMap.put(saasRuleScoreDTO.get("ruleName").toString(), saasRuleScoreDTO.get("policyScore").toString());

        }

        List<TongdunData> tongdunDataList = new ArrayList<>();

        for (ETongdunData item : ETongdunData.values()) {
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

        if (result != null) {

            AppLicense license = appLicenseService.getAppLicense(appId);
            taskLogService.insert(taskId, "任务成功", new Date(), "");
            taskService.updateTaskStatus(taskId, ETaskStatus.SUCCESS.getStatus());
            return SimpleResult.successEncryptByRSAResult(tongdunDataList, license.getServerPublicKey());
        } else {
            logger.error("调用功夫贷同盾采集任务返回值中任务日志信息存在问题:taskId={},tongdunRequest={},httpResult={},result={}", taskId,
                tongdunRequest, httpResult, JSON.toJSONString(result));
            taskLogService.insert(taskId, "调用功夫贷同盾采集任务返回值中任务日志信息存在问题", new Date(), "调用功夫贷同盾采集任务返回值中任务日志信息存在问题");
            taskService.updateTaskStatus(taskId, ETaskStatus.FAIL.getStatus());
            return SimpleResult.failResult("功夫贷同盾采集任务采集失败");
        }
    }

}