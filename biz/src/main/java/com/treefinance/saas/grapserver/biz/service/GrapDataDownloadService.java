package com.treefinance.saas.grapserver.biz.service;


import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.treefinance.saas.grapserver.biz.domain.AppLicense;
import com.treefinance.saas.grapserver.biz.domain.CallbackConfig;
import com.treefinance.saas.grapserver.biz.domain.TaskLog;
import com.treefinance.saas.grapserver.biz.dto.TaskAttribute;
import com.treefinance.saas.grapserver.biz.dto.TaskCallbackLog;
import com.treefinance.saas.grapserver.biz.validation.AccessValidationHandler;
import com.treefinance.saas.grapserver.common.enums.EBizType;
import com.treefinance.saas.grapserver.common.enums.ETaskStatus;
import com.treefinance.saas.grapserver.exception.ForbiddenException;
import com.treefinance.saas.grapserver.exception.InternalServerException;
import com.treefinance.saas.grapserver.exception.TaskStateException;
import com.treefinance.saas.grapserver.facade.enums.EDataType;
import com.treefinance.saas.grapserver.facade.enums.EGrapStatus;
import com.treefinance.saas.grapserver.facade.enums.ETaskAttribute;
import com.treefinance.saas.grapserver.manager.TaskManager;
import com.treefinance.saas.grapserver.manager.domain.TaskBO;
import com.treefinance.saas.grapserver.util.CallbackDataUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author yh-treefinance on 2018/2/7.
 */
@Service
public class GrapDataDownloadService {

    /**
     * logger
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private TaskCallbackLogService taskCallbackLogService;
    @Autowired
    private TaskManager taskManager;
    @Autowired
    protected TaskLogService taskLogService;
    @Autowired
    protected TaskAttributeService taskAttributeService;
    @Autowired
    protected LicenseService licenseService;
    @Autowired
    private AppCallbackConfigService callbackConfigService;
    @Autowired
    private AccessValidationHandler validationHandler;

    /**
     * 获取加密的爬取数据
     */
    public String getEncryptGrapData(String appId, Long taskId, EBizType ebizType) {
        TaskBO task = taskManager.queryCompletedTaskById(taskId);
        if(task == null) {
            throw new TaskStateException("任务进行中");
        }

        if (!task.getAppId().equals(appId)) {
            throw new ForbiddenException("非法访问！appId: " + appId + ", taskId: " + taskId);
        }

        // 校验范围权限
        if (!ebizType.getCode().equals(task.getBizType())) {
            throw new ForbiddenException("非法访问！请检查所需业务类型是否正确！");
        }

        validationHandler.validateTask(appId, task.getUniqueId(), ebizType);

        // 1.获取回调日志中的数据
        Map<String, Object> dataMap = getGrapDataMap(task);
        // 2.默认数据的填充
        fillDataMap(task, dataMap);
        // 3.加密参数
        AppLicense appLicense = licenseService.getAppLicense(appId);

        try {
            String params = CallbackDataUtils.encrypt(dataMap, appLicense.getServerPublicKey());
            params = URLEncoder.encode(params, "utf-8");
            logger.info("grap data : appId={},taskId={},params={}....", appId, taskId, params);
            return params;
        } catch (Exception e) {
            throw new InternalServerException("回调数据加密失败！appId: " + appId + ", taskId:" + taskId + ", bizType: " + ebizType, e);
        }
    }


    /**
     * 获取抓取数据
     */
    private Map<String, Object> getGrapDataMap(TaskBO task) {
        Map<String, Object> dataMap = Maps.newHashMap();
        // 根据回调日志获取数据
        List<TaskCallbackLog> taskCallbackLogList = taskCallbackLogService.getTaskCallbackLogs(task.getId(), null);
        if (CollectionUtils.isNotEmpty(taskCallbackLogList)) {
            String appId = task.getAppId();
            Byte bizType = task.getBizType();
            List<CallbackConfig> configs = callbackConfigService.queryCallbackConfigsByAppIdAndBizType(appId, bizType, EDataType.MAIN_STREAM);
            logger.info("根据业务类型匹配回调配置结果:taskId={},configList={}", task.getId(), JSON.toJSONString(configs));

            if (CollectionUtils.isEmpty(configs)) {
                // 前端回调: 直接取数据结果
                taskCallbackLogList.stream().filter(taskCallbackLog -> Byte.valueOf("2").equals(taskCallbackLog.getType()))
                        .forEach(taskCallbackLog -> {
                            if (StringUtils.isNotEmpty(taskCallbackLog.getRequestParam())) {
                                Map<String, Object> aDataMap = JSON.parseObject(taskCallbackLog.getRequestParam());
                                dataMap.putAll(MapUtils.isNotEmpty(aDataMap) ? aDataMap : Maps.newHashMap());
                            }
                        });
            } else {
                // 后端回调:取主流程数据
                Long configId = configs.get(0).getId().longValue();
                taskCallbackLogList.stream().filter(taskCallbackLog ->
                        Byte.valueOf("1").equals(taskCallbackLog.getType()) && configId.equals(taskCallbackLog.getConfigId()))
                        .forEach(taskCallbackLog -> {
                            if (StringUtils.isNotEmpty(taskCallbackLog.getRequestParam())) {
                                Map<String, Object> aDataMap = JSON.parseObject(taskCallbackLog.getRequestParam());
                                dataMap.putAll(MapUtils.isNotEmpty(aDataMap) ? aDataMap : Maps.newHashMap());
                            }
                        });
            }
        }
        return dataMap;
    }

    /**
     * 生成数据Map
     */
    private void fillDataMap(TaskBO task, Map<String, Object> dataMap) {
        Long taskId = task.getId();
        // 1. 初始化回调数据 并填充uniqueId、taskId、taskStatus
        if (!dataMap.containsKey("taskId")) {
            dataMap.put("taskId", task.getId());
        }
        if (!dataMap.containsKey("uniqueId")) {
            dataMap.put("uniqueId", task.getId());
        }

        // 此次任务状态：001-抓取成功，002-抓取失败，003-抓取结果为空,004-任务取消
        if (!dataMap.containsKey("taskStatus")) {
            if (ETaskStatus.SUCCESS.getStatus().equals(task.getStatus())) {
                dataMap.put("taskStatus", EGrapStatus.SUCCESS.getCode());
            } else if (ETaskStatus.FAIL.getStatus().equals(task.getStatus())) {
                dataMap.put("taskStatus", EGrapStatus.FAIL.getCode());
            } else if (ETaskStatus.CANCEL.getStatus().equals(task.getStatus())) {
                dataMap.put("taskStatus", EGrapStatus.CANCEL.getCode());
            }
        }

        if (!dataMap.containsKey("taskErrorMsg")) {
            if (ETaskStatus.SUCCESS.getStatus().equals(task.getStatus())) {
                dataMap.put("taskErrorMsg", "");
            } else if (ETaskStatus.FAIL.getStatus().equals(task.getStatus())) {
                // 任务失败消息
                TaskLog log = taskLogService.queryLatestErrorLog(task.getId());
                if (log != null) {
                    dataMap.put("taskErrorMsg", log.getMsg());
                } else {
                    dataMap.put("taskErrorMsg", EGrapStatus.FAIL.getName());
                }
            } else if (ETaskStatus.CANCEL.getStatus().equals(task.getStatus())) {
                dataMap.put("taskErrorMsg", "用户取消");
            }
        }

        if (!dataMap.containsKey("dataUrl")) {
            dataMap.put("dataUrl", "");
        }

        if (!dataMap.containsKey("expirationTime")) {
            dataMap.put("expirationTime", "");
        }

        if (!dataMap.containsKey("dataSize")) {
            dataMap.put("dataSize", "0");
        }
        if (!dataMap.containsKey("type")) {
            if (EBizType.EMAIL.getCode().equals(task.getBizType())) {
                dataMap.put("type", EBizType.EMAIL.getText().toLowerCase());
            } else {
                dataMap.put("type", Objects.requireNonNull(EBizType.getName(task.getBizType())).toLowerCase());
            }
        }
        if (!dataMap.containsKey("timestamp")) {
            dataMap.put("timestamp", System.currentTimeMillis());
        }

        // 如果是运营商数据
        if (EBizType.OPERATOR.getCode().equals(task.getBizType())) {
            String groupCodeAttribute = ETaskAttribute.OPERATOR_GROUP_CODE.getAttribute();
            String groupNameAttribute = ETaskAttribute.OPERATOR_GROUP_NAME.getAttribute();
            if (!dataMap.containsKey(groupCodeAttribute) || !dataMap.containsKey(groupNameAttribute)) {
                Map<String, TaskAttribute> attributeMap = taskAttributeService.findByNames(taskId, false, groupCodeAttribute, groupNameAttribute);
                dataMap.put(groupCodeAttribute, attributeMap.get(groupCodeAttribute) == null ? "" : attributeMap.get(groupCodeAttribute).getValue());
                dataMap.put(groupNameAttribute, attributeMap.get(groupNameAttribute) == null ? "" : attributeMap.get(groupNameAttribute).getValue());
            }
        }
        logger.info("fillDataMap: data={}, task={}", JSON.toJSONString(dataMap), JSON.toJSONString(task));
    }

}
