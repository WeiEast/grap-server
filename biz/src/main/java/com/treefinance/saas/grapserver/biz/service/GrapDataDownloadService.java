package com.treefinance.saas.grapserver.biz.service;


import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.treefinance.saas.grapserver.biz.domain.AppLicense;
import com.treefinance.saas.grapserver.biz.domain.CallbackConfig;
import com.treefinance.saas.grapserver.biz.dto.TaskAttribute;
import com.treefinance.saas.grapserver.biz.dto.TaskCallbackLog;
import com.treefinance.saas.grapserver.biz.dto.TaskLog;
import com.treefinance.saas.grapserver.common.enums.EBizType;
import com.treefinance.saas.grapserver.common.enums.ETaskStatus;
import com.treefinance.saas.grapserver.common.exception.ParamsCheckException;
import com.treefinance.saas.grapserver.common.model.dto.TaskDTO;
import com.treefinance.saas.grapserver.facade.enums.EDataType;
import com.treefinance.saas.grapserver.facade.enums.EGrapStatus;
import com.treefinance.saas.grapserver.facade.enums.ETaskAttribute;
import com.treefinance.saas.grapserver.util.CallbackDataUtils;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private TaskService taskService;
    @Autowired
    protected TaskLogService taskLogService;
    @Autowired
    protected TaskAttributeService taskAttributeService;
    @Autowired
    protected LicenseService applicenseservice;
    @Autowired
    private TaskLicenseService taskLicenseService;
    @Autowired
    private AppCallbackConfigService callbackConfigService;

    /**
     * 获取加密的爬取数据
     */
    public String getEncryptGrapData(String appId, String bizType, Long taskId) {
        TaskDTO taskDTO = taskService.getById(taskId);
        if (taskDTO == null) {
            throw new ParamsCheckException("taskId不存在");
        }
        if (!taskDTO.getAppId().equals(appId)) {
            throw new ParamsCheckException("非授权用户taskId");
        }
        if (!taskService.isTaskCompleted(taskDTO)) {
            throw new ParamsCheckException("任务进行中");
        }
        // 校验范围权限
        EBizType ebizType = EBizType.of(bizType);
        if (ebizType == null) {
            throw new ParamsCheckException("未开通相关业务");
        }
        if (!ebizType.getCode().equals(taskDTO.getBizType())) {
            throw new ParamsCheckException("非所属业务taskId");
        }
        taskLicenseService.verifyCreateTask(appId, taskDTO.getUniqueId(), EBizType.of(bizType));
        // 1.获取回调日志中的数据
        Map<String, Object> dataMap = getGrapDataMap(taskDTO);
        // 2.默认数据的填充
        fillDataMap(taskDTO, dataMap);
        // 3.加密参数
        AppLicense appLicense = applicenseservice.getAppLicense(appId);
        String params = "";
        try {
            params = CallbackDataUtils.encrypt(dataMap, appLicense.getServerPublicKey());
            params = URLEncoder.encode(params, "utf-8");
        } catch (Exception e) {
            logger.error("encrypt data error: taskid={},dataMap={}", taskId, JSON.toJSONString(dataMap), e);
        }
        logger.info("grap data : appId={},taskId={},params={}....", appId, taskId, params);
        return params;
    }


    /**
     * 获取抓取数据
     */
    protected Map<String, Object> getGrapDataMap(TaskDTO taskDTO) {
        Map<String, Object> dataMap = Maps.newHashMap();
        // 根据回调日志获取数据
        List<TaskCallbackLog> taskCallbackLogList = taskCallbackLogService.getTaskCallbackLogs(taskDTO.getId(), null);
        if (CollectionUtils.isNotEmpty(taskCallbackLogList)) {
            String appId = taskDTO.getAppId();
            Byte bizType = taskDTO.getBizType();
            List<CallbackConfig> configs = callbackConfigService.queryCallbackConfigsByAppIdAndBizType(appId, bizType, EDataType.MAIN_STREAM);
            logger.info("根据业务类型匹配回调配置结果:taskId={},configList={}", taskDTO.getId(), JSON.toJSONString(configs));

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
    protected Map<String, Object> fillDataMap(TaskDTO task, Map<String, Object> dataMap) {
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
        return dataMap;
    }

}
