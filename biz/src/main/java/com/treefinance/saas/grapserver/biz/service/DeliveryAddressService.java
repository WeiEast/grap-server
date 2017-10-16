package com.treefinance.saas.grapserver.biz.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.treefinance.saas.grapserver.biz.common.CallbackSecureHandler;
import com.treefinance.saas.grapserver.biz.mq.model.DeliveryAddressMessage;
import com.treefinance.saas.grapserver.common.enums.EDataType;
import com.treefinance.saas.grapserver.common.exception.CallbackEncryptException;
import com.treefinance.saas.grapserver.common.exception.RequestFailedException;
import com.treefinance.saas.grapserver.common.model.dto.AppCallbackConfigDTO;
import com.treefinance.saas.grapserver.common.model.dto.TaskDTO;
import com.treefinance.saas.grapserver.common.utils.HttpClientUtils;
import com.treefinance.saas.grapserver.common.utils.RemoteDataDownloadUtils;
import com.treefinance.saas.grapserver.dao.entity.AppLicense;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by yh-treefinance on 2017/9/28.
 */
@Service
public class DeliveryAddressService {
    private static final Logger logger = LoggerFactory.getLogger(DeliveryAddressService.class);

    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskLogService taskLogService;
    @Autowired
    private AppCallbackConfigService appCallbackConfigService;
    @Autowired
    private CallbackSecureHandler callbackSecureHandler;
    @Autowired
    private AppLicenseService appLicenseService;
    @Autowired
    protected TaskCallbackLogService taskCallbackLogService;


    public void callback(DeliveryAddressMessage message) {
        if (message == null || message.getTaskId() == null) {
            return;
        }
        Long taskId = message.getTaskId();
        // 1. 记录日志
        taskLogService.insert(taskId, "收货地址爬取完成", new Date(), "");
        // 2.获取任务
        TaskDTO taskDTO = taskService.getById(taskId);
        if (taskDTO == null) {
            logger.info("delivery address callback failed : task {} not exists, message={}...", taskId, JSON.toJSONString(message));
            return;
        }
        String appId = taskDTO.getAppId();
        // 3.获取商户密钥
        AppLicense appLicense = appLicenseService.getAppLicense(appId);
        if (appLicense == null) {
            logger.info("delivery address callback failed : appLicense of {} is null, message={}...", appId, JSON.toJSONString(message));
            return;
        }

        List<AppCallbackConfigDTO> callbackConfigs = getCallbackConfigs(taskDTO);
        if (CollectionUtils.isEmpty(callbackConfigs)) {
            logger.info("delivery address callback failed : callbackConfigs of {} is null, message={}...", appId, JSON.toJSONString(message));
            return;
        }
        Map<String, Object> dataMap = JSON.parseObject(message.getData());
        Map<String, Object> originalDataMap = Maps.newHashMap(dataMap);
        // 填充uniqueId、taskId、taskStatus
        dataMap.put("uniqueId", taskDTO.getUniqueId());
        dataMap.put("taskId", taskDTO.getId());
        // 4.爬取成功，下载数据
        boolean isSuccess = Integer.valueOf(1).equals(message.getStatus());
        if (isSuccess) {
            dataMap.put("taskStatus", "001");
            dataMap.put("taskErrorMsg", "");
            if (dataMap.get("dataUrl") != null) {
                String dataUrl = dataMap.get("dataUrl").toString();
                try {
                    String appDataKey = appLicense.getDataSecretKey();
                    // oss 下载数据
                    byte[] result = RemoteDataDownloadUtils.download(dataUrl, byte[].class);
                    // 数据体默认使用商户密钥加密
                    String data = callbackSecureHandler.decryptByAES(result, appDataKey);
                    Map<String, Object> downloadDataMap = JSON.parseObject(data);
                    dataMap.put("data", downloadDataMap);
                    if (MapUtils.isEmpty(downloadDataMap)) {
                        dataMap.put("taskErrorMsg", "抓取结果为空");
                        dataMap.put("taskStatus", "003");
                    }
                    if (logger.isDebugEnabled()) {
                        logger.debug("download data success : {}  >>>>>>> {}", JSON.toJSONString(dataMap), data);
                    }
                } catch (Exception e) {
                    logger.error("download data failed : data={}", JSON.toJSONString(dataMap));
                    dataMap.put("taskErrorMsg", "下载数据失败");
                    dataMap.put("taskStatus", "004");
                }
            }
            dataMap.remove("dataUrl");
        } else {
            dataMap.put("taskStatus", "002");
            dataMap.put("taskErrorMsg", "抓取失败");
        }
        // 5.回调 aes加密
        String aesKey = appLicense.getDataSecretKey();
        for (AppCallbackConfigDTO configDTO : callbackConfigs) {
            // 成功是否通知
            if (!Byte.valueOf("1").equals(configDTO.getIsNotifySuccess()) && !isSuccess) {
                continue;
            }
            // 失败是否通知
            if (!Byte.valueOf("1").equals(configDTO.getIsNotifyFailure()) && isSuccess) {
                continue;
            }
            String result = "";
            Map<String, Object> paramMap = Maps.newHashMap();
            String callbackUrl = configDTO.getUrl();
            Long startTime = System.currentTimeMillis();
            try {
                String params = callbackSecureHandler.encryptByAES(dataMap, aesKey);
                paramMap.put("params", params);
                // 超时时间（秒）
                Byte timeOut = configDTO.getTimeOut();
                // 重试次数，3次
                Byte retryTimes = configDTO.getRetryTimes();
                result = HttpClientUtils.doPostWithTimoutAndRetryTimes(callbackUrl, timeOut, retryTimes, paramMap);
                taskLogService.insert(taskId, "收货地址回调通知成功", new Date(), result);
            } catch (CallbackEncryptException e) {
                logger.error("encry data failed : data={},key={}", dataMap, aesKey, e);
                result = e.getMessage();
            } catch (RequestFailedException e) {
                logger.error("callback failed : data={}", dataMap, e);
                result = e.getResult();
                taskLogService.insert(taskId, "收货地址回调通知失败", new Date(), result);
            } finally {
                long consumeTime = System.currentTimeMillis() - startTime;
                taskCallbackLogService.insert(callbackUrl, configDTO, taskId, JSON.toJSONString(originalDataMap), result, consumeTime);
                logger.info("回调收货地址通知：config={},data={},paramMap={},result={}", JSON.toJSONString(configDTO), dataMap, paramMap, result);
            }
        }

    }


    /**
     * 获取回调配置
     *
     * @return
     */
    protected List<AppCallbackConfigDTO> getCallbackConfigs(TaskDTO taskDTO) {
        String appId = taskDTO.getAppId();
        Byte bizType = taskDTO.getBizType();
        List<AppCallbackConfigDTO> configList = appCallbackConfigService.getByAppIdAndBizType(appId, bizType);
        logger.info("根据业务类型匹配回调配置结果:configList={}", JSON.toJSONString(configList));
        if (CollectionUtils.isEmpty(configList)) {
            return Lists.newArrayList();
        }
        // 剔除非主流程数据
        return configList.stream()
                .filter(config -> config != null && EDataType.DELIVERY_ADDRESS.getType().equals(config.getDataType()))
                .collect(Collectors.toList());
    }
}
