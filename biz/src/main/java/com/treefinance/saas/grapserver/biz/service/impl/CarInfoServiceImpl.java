/*
 * Copyright © 2015 - 2017 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.treefinance.saas.grapserver.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.treefinance.saas.grapserver.biz.domain.AppLicense;
import com.treefinance.saas.grapserver.biz.service.CarInfoService;
import com.treefinance.saas.grapserver.biz.service.LicenseService;
import com.treefinance.saas.grapserver.common.enums.ETaskStep;
import com.treefinance.saas.grapserver.common.model.dto.carinfo.CarInfoCollectTaskLogDTO;
import com.treefinance.saas.grapserver.context.component.AbstractService;
import com.treefinance.saas.grapserver.context.config.DiamondConfig;
import com.treefinance.saas.grapserver.util.HttpClientUtils;
import com.treefinance.saas.knife.result.SimpleResult;
import com.treefinance.saas.taskcenter.facade.request.CarInfoCollectTaskLogRequest;
import com.treefinance.saas.taskcenter.facade.service.CarInfoFacade;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Jerry
 * @date 2018/12/12 01:47
 */
@Service("carInfoService")
public class CarInfoServiceImpl extends AbstractService implements CarInfoService {
    @Autowired
    private LicenseService licenseService;
    @Autowired
    private DiamondConfig diamondConfig;
    @Autowired
    private CarInfoFacade carInfoFacade;

    @Override
    public Object processCollectTask(Long taskId, String appId, String modelNum) {
        String url = diamondConfig.getCrawlerUrlCarInfoCollect();
        logger.info("调用爬数处理车辆信息采集任务传入参数:taskId={},modelNum={}", taskId, modelNum);
        Map<String, Object> map = Maps.newHashMap();
        map.put("modelNum", modelNum);
        String httpResult;
        try {
            httpResult = HttpClientUtils.doPostWithTimeoutAndRetryTimes(url, (byte)60, (byte)0, map);
        } catch (Exception e) {
            logger.error("调用爬数处理车辆信息采集任务异常:taskId={},modelNum={}", taskId, modelNum, e);
            processFailCollectTask(taskId, "调用爬数处理车辆信息采集任务异常");
            return SimpleResult.failResult("车辆信息采集失败");
        }
        JSONObject result = JSON.parseObject(httpResult);
        JSONObject resultData = JSON.parseObject(JSON.toJSONString(result.get("data"), SerializerFeature.WriteMapNullValue));
        if (resultData.get("resultLog") != null && StringUtils.isNotBlank(resultData.get("resultLog").toString()) && checkResultLog(resultData.get("resultLog").toString())) {
            processSuccessCollectTask(taskId, resultData.get("resultLog").toString());
            resultData.remove("resultLog");
            AppLicense license = licenseService.getAppLicense(appId);
            //临时不做加密返回，联调需去掉
            //return SimpleResult.successResult(resultData);
            return SimpleResult.successEncryptByRSAResult(resultData, license.getServerPublicKey());
        } else {
            logger.error("调用爬数处理车辆信息采集任务返回值中任务日志信息存在问题:taskId={},modelNum={},httpResult={},result={}", taskId, modelNum, httpResult, JSON.toJSONString(result));
            processFailCollectTask(taskId, "调用爬数处理车辆信息采集任务返回值中任务日志信息存在问题");
            return SimpleResult.failResult("车辆信息采集失败");
        }
    }

    private void updateCollectTaskStatusAndTaskLogAndSendMonitor(Long taskId, List<CarInfoCollectTaskLogDTO> logList) {
        List<CarInfoCollectTaskLogRequest> carInfoCollectTaskLogRequestList = convert(logList, CarInfoCollectTaskLogRequest.class);
        carInfoFacade.updateCollectTaskStatusAndTaskLogAndSendMonitor(taskId, carInfoCollectTaskLogRequestList);
    }

    /**
     * 校验调用爬数处理车辆信息采集任务返回值中任务日志信息是否存在问题 若日志列表中,没有表明任务结束的日志,则存在问题
     */
    private boolean checkResultLog(String resultLog) {
        List<CarInfoCollectTaskLogDTO> logList = JSON.parseArray(resultLog, CarInfoCollectTaskLogDTO.class);
        for (CarInfoCollectTaskLogDTO log : logList) {
            if (StringUtils.equalsIgnoreCase(log.getMsg(), ETaskStep.TASK_SUCCESS.getText()) || StringUtils.equalsIgnoreCase(log.getMsg(), ETaskStep.TASK_FAIL.getText())) {
                return true;
            }
        }
        return false;
    }

    private void processFailCollectTask(Long taskId, String failMsg) {
        List<CarInfoCollectTaskLogDTO> carInfoCollectTaskLogDTOList = Lists.newArrayList();
        carInfoCollectTaskLogDTOList.add(new CarInfoCollectTaskLogDTO(ETaskStep.CRAWL_FAIL.getText(), failMsg, new Date()));
        carInfoCollectTaskLogDTOList.add(new CarInfoCollectTaskLogDTO(ETaskStep.TASK_FAIL.getText(), null, new Date()));
        this.updateCollectTaskStatusAndTaskLogAndSendMonitor(taskId, carInfoCollectTaskLogDTOList);
    }

    private Boolean processSuccessCollectTask(Long taskId, String resultLog) {
        List<CarInfoCollectTaskLogDTO> logList = JSON.parseArray(resultLog, CarInfoCollectTaskLogDTO.class);
        this.updateCollectTaskStatusAndTaskLogAndSendMonitor(taskId, logList);
        for (CarInfoCollectTaskLogDTO log : logList) {
            if (StringUtils.equalsIgnoreCase(log.getMsg(), ETaskStep.TASK_SUCCESS.getText())) {
                return true;
            }
        }
        return false;
    }
}
