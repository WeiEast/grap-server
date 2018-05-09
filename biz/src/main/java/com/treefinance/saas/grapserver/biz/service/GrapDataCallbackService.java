package com.treefinance.saas.grapserver.biz.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.treefinance.saas.grapserver.common.exception.RequestFailedException;
import com.treefinance.saas.grapserver.common.model.dto.AppCallbackConfigDTO;
import com.treefinance.saas.grapserver.common.model.dto.AsycGrapDTO;
import com.treefinance.saas.grapserver.common.model.dto.TaskDTO;
import com.treefinance.saas.grapserver.common.utils.HttpClientUtils;
import com.treefinance.saas.grapserver.common.utils.RSAUtils;
import com.treefinance.saas.grapserver.dao.entity.AppLicense;
import com.treefinance.saas.grapserver.facade.enums.EDataType;
import com.treefinance.saas.grapserver.facade.enums.EGrapStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 爬取数据回调Service
 * Created by yh-treefinance on 2017/12/25.
 */
@Service
public class GrapDataCallbackService {
    /**
     * logger
     */
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskTimeService taskTimeService;
    @Autowired
    private TaskLogService taskLogService;
    @Autowired
    private AppCallbackConfigService appCallbackConfigService;
    @Autowired
    private AppLicenseService appLicenseService;
    @Autowired
    protected TaskCallbackLogService taskCallbackLogService;

    /**
     * 异步爬取数据处理
     */
    public void handleAyscData(AsycGrapDTO asycGrapDTO) {
        Assert.notNull(asycGrapDTO, "taskId can't be null : json=" + JSON.toJSONString(asycGrapDTO));
        Assert.notNull(asycGrapDTO.getTaskId(), "taskId can't be null : json=" + JSON.toJSONString(asycGrapDTO));
        Assert.notNull(asycGrapDTO.getDataType(), "dataType can't be null : json=" + JSON.toJSONString(asycGrapDTO));

        EDataType dataType = EDataType.typeOf(asycGrapDTO.getDataType().byteValue());
        Assert.notNull(dataType, "dataType is illegal : json=" + JSON.toJSONString(asycGrapDTO));

        Long taskId = asycGrapDTO.getTaskId();
        // 1.获取任务
        TaskDTO taskDTO = taskService.getById(taskId);
        if (taskDTO == null) {
            logger.info("{} callback failed : task {} not exists, message={}...", dataType.name(), taskId, JSON.toJSONString(asycGrapDTO));
            return;
        }
        String appId = taskDTO.getAppId();
        // 3.获取商户密钥
        AppLicense appLicense = appLicenseService.getAppLicense(appId);
        if (appLicense == null) {
            logger.info("{} callback failed : taskId={} appLicense of {} is null, message={}...", dataType.name(), taskId, appId, JSON.toJSONString(asycGrapDTO));
            return;
        }

        List<AppCallbackConfigDTO> callbackConfigs = getCallbackConfigs(taskDTO, dataType);
        if (CollectionUtils.isEmpty(callbackConfigs)) {
            logger.info("{} callback failed :taskId={}, callbackConfigs of {} is null, message={}...", dataType.name(), taskId, appId, JSON.toJSONString(asycGrapDTO));
            return;
        }
        Map<String, Object> dataMap = Maps.newHashMap();
        // 填充uniqueId、taskId、taskStatus
        dataMap.put("taskId", taskDTO.getId());
        dataMap.put("taskStatus", EGrapStatus.SUCCESS.getCode());
        dataMap.put("taskErrorMsg", "");
        dataMap.put("uniqueId", taskDTO.getUniqueId());
        dataMap.put("dataUrl", asycGrapDTO.getDataUrl());
        dataMap.put("dataType", asycGrapDTO.getDataType());
        dataMap.put("dataSize", asycGrapDTO.getDataSize());
        dataMap.put("expiredTime", asycGrapDTO.getExpiredTime());
        dataMap.put("timestamp", asycGrapDTO.getTimestamp());

        // 4.爬取成功，下载数据
        boolean isSuccess = Integer.valueOf(1).equals(asycGrapDTO.getStatus());
        if (isSuccess) {
            if (StringUtils.isEmpty(asycGrapDTO.getDataUrl())) {
                dataMap.put("taskStatus", EGrapStatus.RESULT_EMPTY.getCode());
                dataMap.put("taskErrorMsg", EGrapStatus.RESULT_EMPTY.getName());
            }
        } else {
            dataMap.put("taskStatus", EGrapStatus.FAIL.getCode());
            dataMap.put("taskErrorMsg", EGrapStatus.FAIL.getName());
        }
        if (logger.isDebugEnabled()) {
            logger.debug("{} callback : generate dataMap  {} ", dataType.name(), JSON.toJSONString(dataMap));
        }
        // 5.回调 aes加密
        String key = appLicense.getServerPublicKey();
        for (AppCallbackConfigDTO configDTO : callbackConfigs) {
            // 任务成功但是成功不通知
            if (isSuccess && !Byte.valueOf("1").equals(configDTO.getIsNotifySuccess())) {
                logger.info("{} callback : 任务成功，但是成功不通知...taskId={}, config={}", dataType.name(), taskId, JSON.toJSONString(configDTO));
                continue;
            }
            // 任务失败但是失败不通知
            else if (!isSuccess && !Byte.valueOf("1").equals(configDTO.getIsNotifyFailure())) {
                logger.info("{} callback : 任务失败，但是成功不通知...taskId={}, config={}", dataType.name(), taskId, JSON.toJSONString(configDTO));
                continue;
            }

            String result = "";
            Map<String, Object> paramMap = Maps.newHashMap();
            String callbackUrl = configDTO.getUrl();
            Long startTime = System.currentTimeMillis();
            try {
                String dataJson = JSON.toJSONString(dataMap);
                String params = RSAUtils.encryptData(dataJson, key);
                paramMap.put("params", params);
                // 超时时间（秒）
                Byte timeOut = configDTO.getTimeOut();
                // 重试次数，3次
                Byte retryTimes = configDTO.getRetryTimes();
                result = HttpClientUtils.doPostWithTimoutAndRetryTimes(callbackUrl, timeOut, retryTimes, paramMap);
                taskLogService.insert(taskId, dataType.getName() + "回调通知成功", new Date(), "");
                logger.info("{} callback : callback success : taskId={},callbackUrl={}, params={}", dataType.name(), taskId, callbackUrl, params);
            } catch (RequestFailedException e) {
                logger.error("{} callback : 回调通知失败 : data={}", dataType.name(), dataMap, e);
                result = e.getResult();
                taskLogService.insert(taskId, dataType.getName() + "回调通知失败", new Date(), result);
            } catch (Exception e) {
                logger.error("{} callback : encry data failed : data={},key={}", dataType.name(), dataMap, key, e);
                result = e.getMessage();
            } finally {
                long consumeTime = System.currentTimeMillis() - startTime;
                taskCallbackLogService.insert(configDTO, taskId, (byte) 1, JSON.toJSONString(dataMap), result, consumeTime, 0);
                logger.info("{} callback ：{}回调通知完成：config={},data={},paramMap={},result={}", dataType.name(),
                        dataType.getName(), JSON.toJSONString(configDTO), dataMap, paramMap, result);
            }
        }
    }

    /**
     * 获取回调配置
     *
     * @return
     */
    public List<AppCallbackConfigDTO> getCallbackConfigs(TaskDTO taskDTO, EDataType dataType) {
        String appId = taskDTO.getAppId();
        Byte bizType = taskDTO.getBizType();
        List<AppCallbackConfigDTO> configList = appCallbackConfigService.getByAppIdAndBizType(appId, bizType, dataType);
        logger.info("根据业务类型匹配回调配置结果:taskId={},configList={}", taskDTO.getId(), JSON.toJSONString(configList));
        if (CollectionUtils.isEmpty(configList)) {
            return Lists.newArrayList();
        }
        return configList.stream()
                .filter(config -> config != null && dataType.getType().equals(config.getDataType()))
                .collect(Collectors.toList());
    }

}
