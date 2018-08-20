package com.treefinance.saas.grapserver.biz.service.directive.process;

import com.alibaba.fastjson.JSON;
import com.alibaba.rocketmq.shade.io.netty.util.internal.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.treefinance.saas.grapserver.biz.common.CallbackSecureHandler;
import com.treefinance.saas.grapserver.biz.service.*;
import com.treefinance.saas.grapserver.biz.service.monitor.MonitorService;
import com.treefinance.saas.grapserver.common.enums.EBizType;
import com.treefinance.saas.grapserver.common.enums.EDirective;
import com.treefinance.saas.grapserver.common.enums.ETaskStatus;
import com.treefinance.saas.grapserver.common.exception.CallbackEncryptException;
import com.treefinance.saas.grapserver.common.exception.RequestFailedException;
import com.treefinance.saas.grapserver.common.model.Constants;
import com.treefinance.saas.grapserver.common.model.dto.AppCallbackConfigDTO;
import com.treefinance.saas.grapserver.common.model.dto.CallBackLicenseDTO;
import com.treefinance.saas.grapserver.common.model.dto.DirectiveDTO;
import com.treefinance.saas.grapserver.common.model.dto.TaskDTO;
import com.treefinance.saas.grapserver.common.utils.HttpClientUtils;
import com.treefinance.saas.grapserver.common.utils.RemoteDataDownloadUtils;
import com.treefinance.saas.grapserver.dao.entity.AppLicense;
import com.treefinance.saas.grapserver.dao.entity.TaskAttribute;
import com.treefinance.saas.grapserver.dao.entity.TaskLog;
import com.treefinance.saas.grapserver.facade.enums.EDataType;
import com.treefinance.saas.grapserver.facade.enums.EGrapStatus;
import com.treefinance.saas.grapserver.facade.enums.ETaskAttribute;
import com.treefinance.saas.knife.result.SimpleResult;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 支持回调的指令处理
 * Created by yh-treefinance on 2017/7/11.
 */
public abstract class CallbackableDirectiveProcessor {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected AppCallbackConfigService appCallbackConfigService;
    @Autowired
    protected CallbackSecureHandler callbackSecureHandler;
    @Autowired
    protected AppLicenseService appLicenseService;
    @Autowired
    protected TaskCallbackLogService taskCallbackLogService;
    @Autowired
    protected TaskLogService taskLogService;
    @Autowired
    protected TaskAttributeService taskAttributeService;
    @Autowired
    protected CallbackResultService callbackResultService;
    @Autowired
    protected GrapDataCallbackService grapDataCallbackService;
    @Autowired
    protected MonitorService monitorService;


    /**
     * 回调前处理
     *
     * @param directiveDTO
     */
    protected boolean precallback(Map<String, Object> dataMap, AppLicense appLicense, DirectiveDTO directiveDTO) {
        // 使用商户密钥加密数据，返回给前端
        Map<String, Object> paramMap = Maps.newHashMap();
        String remark = directiveDTO.getRemark();
        if (StringUtils.isNotEmpty(remark)) {
            try {
                Map<String, Object> jsonObject = JSON.parseObject(remark);
                if (!CollectionUtils.isEmpty(jsonObject)) {
                    paramMap.put(Constants.ERROR_MSG_NAME, jsonObject.get(Constants.ERROR_MSG_NAME));

                }
            } catch (Exception e) {
            }
        }
        try {
            logger.info("回调数据生成： {}", JSON.toJSONString(dataMap));
            String params = encryptByRSA(dataMap, appLicense);
            paramMap.put("params", params);
            directiveDTO.setRemark(JSON.toJSONString(paramMap));
        } catch (Exception e) {
            logger.error("encryptByRSA error : " + dataMap + ",key=" + appLicense.getServerPublicKey(), e);
            directiveDTO.setRemark(JSON.toJSONString("指令信息处理失败"));
            return false;
        }
        return true;
    }

    /**
     * 刷新数据
     *
     * @param dataMap
     * @param appLicense
     * @param directiveDTO
     */
    private void flushData(Map<String, Object> dataMap, AppLicense appLicense, DirectiveDTO directiveDTO) {
        this.precallback(dataMap, appLicense, directiveDTO);
    }

    /**
     * 执行回调(无需预处理)
     *
     * @param directiveDTO
     * @return
     */
    protected int callback(DirectiveDTO directiveDTO) {
        TaskDTO taskDTO = directiveDTO.getTask();
        String appId = taskDTO.getAppId();
        // 1.获取商户密钥
        AppLicense appLicense = appLicenseService.getAppLicense(appId);
        // 2.成数据map
        Map<String, Object> dataMap = generateDataMap(directiveDTO);
        // 3.回调
        return callback(dataMap, appLicense, directiveDTO);
    }

    /**
     * 执行回调
     *
     * @param dataMap
     * @param appLicense
     * @param directiveDTO
     * @return 0-无需回调，1-回调成功，-1-回调失败
     */
    protected int callback(Map<String, Object> dataMap, AppLicense appLicense, DirectiveDTO directiveDTO) {
        TaskDTO taskDTO = directiveDTO.getTask();
        Long taskId = directiveDTO.getTaskId();

        // 4.查询回调配置
        List<AppCallbackConfigDTO> configList = getCallbackConfigs(taskDTO);
        if (CollectionUtils.isEmpty(configList)) {
            logger.info("callback exit: callbackconfig is empty, directive={}", JSON.toJSONString(directiveDTO));
            monitorService.sendTaskCallbackMsgMonitorMessage(taskId, null, null, false);
            return 0;
        }

        // 5.校验是否需要回调
        configList = configList.stream().filter(config -> checkCallbackable(config, directiveDTO)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(configList)) {
            logger.info("callback exit: the task no callback required, directive={}", JSON.toJSONString(directiveDTO));
            monitorService.sendTaskCallbackMsgMonitorMessage(taskId, null, null, false);
            return 0;
        }
        // 6.执行回调，支持一个任务回调多方
        List<Boolean> callbackFlags = Lists.newArrayList();
        for (AppCallbackConfigDTO config : configList) {
            Boolean callbackSuccess = Boolean.TRUE;
            try {
                // 执行回调
                callbackSuccess = doCallBack(dataMap, appLicense, config, directiveDTO);
            } catch (Exception e) {
                dataMap.put("taskStatus", EGrapStatus.FAIL.getCode());
                dataMap.put("taskErrorMsg", "回调通知失败");
                flushData(dataMap, appLicense, directiveDTO);
                callbackSuccess = Boolean.FALSE;
                String errorMsg = "回调通知失败：" + e.getMessage();
                logger.error(errorMsg + "，config=" + JSON.toJSONString(config), e);
                taskLogService.insert(taskId, "回调通知失败", new Date(), StringUtils.substring(errorMsg, 0, 1000));
            }
            callbackFlags.add(callbackSuccess);

        }
        // 7.有回调失败的整个任务算失败
        if (callbackFlags.contains(Boolean.FALSE)) {
            return -1;
        }
        dataMap.put("taskStatus", EGrapStatus.SUCCESS.getCode());
        dataMap.put("taskErrorMsg", "");
        taskLogService.insert(taskId, "回调通知成功", new Date(), null);
        return 1;

    }


    /**
     * 获取回调配置
     *
     * @return
     */
    protected List<AppCallbackConfigDTO> getCallbackConfigs(TaskDTO taskDTO) {
        return grapDataCallbackService.getCallbackConfigs(taskDTO, EDataType.MAIN_STREAM);
    }

    /**
     * 校验是否能够回调
     *
     * @param config
     * @param directiveDTO
     * @return
     */
    protected boolean checkCallbackable(AppCallbackConfigDTO config, DirectiveDTO directiveDTO) {
        Long taskId = directiveDTO.getTask().getId();
        String directive = directiveDTO.getDirective();
        if (config == null) {
            return false;
        }
        // 当前任务成功，但成功不通知
        if (EDirective.TASK_SUCCESS.getText().equals(directive) && !Byte.valueOf("1").equals(config.getIsNotifySuccess())) {
            if (logger.isDebugEnabled()) {
                logger.debug("the task of {} success, but no need for callback : task={},config={}",
                        taskId, JSON.toJSONString(directiveDTO), JSON.toJSONString(config));
            }
            return false;
        }
        // 当前任务失败，但失败不通知
        else if (EDirective.TASK_FAIL.getText().equals(directive) && !Byte.valueOf("1").equals(config.getIsNotifyFailure())) {
            if (logger.isDebugEnabled()) {
                logger.debug("the task of {} failed, but no need for callback : task={},config={}",
                        taskId, JSON.toJSONString(directiveDTO), JSON.toJSONString(config));
            }
            return false;
        }
        // 当前任务取消，但取消不通知
        else if (EDirective.TASK_CANCEL.getText().equals(directive) && !Byte.valueOf("1").equals(config.getIsNotifyCancel())) {
            if (logger.isDebugEnabled()) {
                logger.debug("the task of {} cancel, but no need for callback : task={},config={}",
                        taskId, JSON.toJSONString(directiveDTO), JSON.toJSONString(config));
            }
            return false;
        }
        return true;
    }


    /**
     * 生成数据Map
     *
     * @param directiveDTO
     * @return
     */
    protected Map<String, Object> generateDataMap(DirectiveDTO directiveDTO) {
        TaskDTO task = directiveDTO.getTask();
        // 1. 初始化回调数据 并填充uniqueId、taskId、taskStatus
        Map<String, Object> dataMap = ifNull(JSON.parseObject(directiveDTO.getRemark()), Maps.newHashMap());
        dataMap.put("uniqueId", ifNull(dataMap.get("uniqueId"), directiveDTO.getTask().getUniqueId()));
        dataMap.put("taskId", ifNull(dataMap.get("taskId"), directiveDTO.getTask().getId()));

        dataMap.put("taskStatus", EGrapStatus.SUCCESS.getCode());
        dataMap.put("taskErrorMsg", "");
        // 此次任务状态：001-抓取成功，002-抓取失败，003-抓取结果为空,004-任务取消
        if (ETaskStatus.SUCCESS.getStatus().equals(task.getStatus())) {
            dataMap.put("taskStatus", EGrapStatus.SUCCESS.getCode());
            dataMap.put("taskErrorMsg", "");
        } else if (ETaskStatus.FAIL.getStatus().equals(task.getStatus())) {
            dataMap.put("taskStatus", EGrapStatus.FAIL.getCode());
            // 任务失败消息
            TaskLog log = taskLogService.queryLastestErrorLog(task.getId());
            if (log != null) {
                dataMap.put("taskErrorMsg", log.getMsg());
            } else {
                dataMap.put("taskErrorMsg", EGrapStatus.FAIL.getName());
            }
        } else if (ETaskStatus.CANCEL.getStatus().equals(task.getStatus())) {
            dataMap.put("taskStatus", EGrapStatus.CANCEL.getCode());
            dataMap.put("taskErrorMsg", "用户取消");

        }
        logger.info("generateDataMap: data={}, directive={}", JSON.toJSONString(dataMap), JSON.toJSONString(directiveDTO));
        return dataMap;
    }

    /**
     * 初始化回调参数
     *
     * @param dataMap
     * @param appLicense
     * @param config
     * @return
     * @throws Exception
     */
    protected void initDataMap(Map<String, Object> dataMap, AppLicense appLicense, AppCallbackConfigDTO config, DirectiveDTO directiveDTO) throws Exception {
        // 如果是数据传输，则需先下载数据
        Byte notifyModel = config.getNotifyModel();
        if (Byte.valueOf("1").equals(notifyModel)) {
            dataMap.put("data", "");
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
                        dataMap.put("taskErrorMsg", EGrapStatus.RESULT_EMPTY.getName());
                        dataMap.put("taskStatus", EGrapStatus.RESULT_EMPTY.getCode());
                        flushData(dataMap, appLicense, directiveDTO);
                    }
//                    if (logger.isDebugEnabled()) {
//                        logger.debug("download data success : {}  >>>>>>> {}", JSON.toJSONString(dataMap), data);
//                    }
                } catch (IOException e) {
                    logger.error("download data failed : data={}", JSON.toJSONString(dataMap));
                    dataMap.put("taskErrorMsg", "下载数据失败");
                    dataMap.put("taskStatus", EGrapStatus.FAIL.getCode());
                    flushData(dataMap, appLicense, directiveDTO);
                }
            }
            dataMap.remove("dataUrl");
        }
        // 如果是运营商数据
        if (directiveDTO.getTask() != null && EBizType.OPERATOR.getCode().equals(directiveDTO.getTask().getBizType())) {
            Long taskId = directiveDTO.getTask().getId();
            String groupCodeAttribute = ETaskAttribute.OPERATOR_GROUP_CODE.getAttribute();
            String groupNameAttribute = ETaskAttribute.OPERATOR_GROUP_NAME.getAttribute();

            Map<String, TaskAttribute> attributeMap = taskAttributeService.findByNames(taskId, false, groupCodeAttribute, groupNameAttribute);
            dataMap.put(groupCodeAttribute, attributeMap.get(groupCodeAttribute) == null ? "" : attributeMap.get(groupCodeAttribute).getValue());
            dataMap.put(groupNameAttribute, attributeMap.get(groupNameAttribute) == null ? "" : attributeMap.get(groupNameAttribute).getValue());
        }
    }

    /**
     * 初始化回调参数
     *
     * @param dataMap
     * @param appLicense
     * @param config
     * @return
     * @throws Exception
     */
    protected String encryptParams(Map<String, Object> dataMap, AppLicense appLicense, AppCallbackConfigDTO config) throws Exception {
        String params = null;
        String appId = config.getAppId();

        // 2.获取商户密钥、回调密钥
        CallBackLicenseDTO callbackLicense = appLicenseService.getCallbackLicense(config.getId());
        // 是否使用新密钥，0-否，1-是
        Byte isNewKey = config.getIsNewKey();
        String aesDataKey = "";
        if (Byte.valueOf("0").equals(isNewKey)) {
            if (appLicense == null) {
                throw new CallbackEncryptException("appLicense is null");
            }
            aesDataKey = appLicense.getDataSecretKey();
        } else if (Byte.valueOf("1").equals(isNewKey)) {
            if (callbackLicense == null) {
                throw new CallbackEncryptException("callbackLicense is null");
            }
            aesDataKey = callbackLicense.getDataSecretKey();
        }
        byte version = config.getVersion();
        if (version > 0) {
            // 默认使用AES方式
            params = encryptByAES(dataMap, aesDataKey);
        } else {
            params = encryptByRSA(dataMap, appLicense);
        }

        return params;
    }

    /**
     * AES 加密
     *
     * @param dataMap
     * @param aesDataKey
     * @return
     * @throws CallbackEncryptException
     */
    private String encryptByAES(Map<String, Object> dataMap, String aesDataKey) throws CallbackEncryptException {
        String params = callbackSecureHandler.encryptByAES(dataMap, aesDataKey);
        return params;
    }

    /**
     * RSA 加密
     *
     * @param dataMap
     * @param appLicense
     * @return
     * @throws CallbackEncryptException
     * @throws UnsupportedEncodingException
     */
    private String encryptByRSA(Map<String, Object> dataMap, AppLicense appLicense) throws CallbackEncryptException, UnsupportedEncodingException {
        String params;
        String rsaPublicKey = appLicense.getServerPublicKey();
        // 兼容老版本，使用RSA
        params = callbackSecureHandler.encrypt(dataMap, rsaPublicKey);
        params = URLEncoder.encode(params, "utf-8");
        return params;
    }


    /**
     * 执行回调
     *
     * @param dataMap
     * @param appLicense
     * @param config
     * @return
     */
    protected boolean doCallBack(Map<String, Object> dataMap, AppLicense appLicense, AppCallbackConfigDTO config, DirectiveDTO directiveDTO) throws Exception {
        // 1.备份数据
        Map<String, Object> originalDataMap = Maps.newHashMap(dataMap);

        // 2.初始化数据
        this.initDataMap(dataMap, appLicense, config, directiveDTO);
        String params = this.encryptParams(dataMap, appLicense, config);
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("params", params);
        paramMap.put("appId", appLicense.getAppId());
        String callbackUrl = config.getUrl();
        // 超时时间（秒）
        Byte timeOut = config.getTimeOut();
        // 重试次数，3次
        Byte retryTimes = config.getRetryTimes();
        logger.info("回调执行：taskId={},dataMap={}, params={}", directiveDTO.getTaskId(), JSON.toJSONString(dataMap), JSON.toJSONString(paramMap));
        String result = "";
        int httpCode = 200;
        Long startTime = System.currentTimeMillis();
        try {
            if (Byte.valueOf("0").equals(config.getNotifyModel())) {
                result = HttpClientUtils.doGetWithTimoutAndRetryTimes(callbackUrl, timeOut, retryTimes, paramMap);
            } else {
                result = HttpClientUtils.doPostWithTimoutAndRetryTimes(callbackUrl, timeOut, retryTimes, paramMap);
            }
        } catch (RequestFailedException e) {
            logger.error("doCallBack exception: callbackUrl={},dataMap={}", callbackUrl, JSON.toJSONString(dataMap), e);
            result = e.getResult();
            httpCode = e.getStatusCode();
            throw e;
        } finally {
            long consumeTime = System.currentTimeMillis() - startTime;
            // 保存的参数（含dataUrl）
//            String paramsForLog = this.encryptParams(originalDataMap, appLicense, config);
            // 记录回调日志
            taskCallbackLogService.insert(config, directiveDTO.getTaskId(), (byte) 1, JSON.toJSONString(originalDataMap),
                    result, consumeTime, httpCode);
            //主流程回调做监控
            if (config.getDataType() != null && config.getDataType() == 0) {
                monitorService.sendTaskCallbackMsgMonitorMessage(directiveDTO.getTaskId(), httpCode, result, true);
            }
            // 处理返回结果
            handleRequestResult(directiveDTO, result);
            // 回调处理
            callbackResultService.handleResult(directiveDTO.getTask(), result, config, httpCode);
        }
        return true;
    }


    /**
     * 处理请求失败异常
     *
     * @param directiveDTO
     * @param result
     */
    private void handleRequestResult(DirectiveDTO directiveDTO, String result) {
        try {
            result = result.trim();
            if (!StringUtil.isNullOrEmpty(result) && result.startsWith("{") && result.endsWith("}")) {
                SimpleResult simpleResult = JSON.parseObject(result, SimpleResult.class);
                Map<String, Object> remarkMap = Maps.newHashMap();
                if (StringUtils.isNotBlank(directiveDTO.getRemark())) {
                    remarkMap = JSON.parseObject(directiveDTO.getRemark());
                }
                if (simpleResult != null && StringUtils.isNotEmpty(simpleResult.getErrorMsg())) {
                    remarkMap.put(Constants.ERROR_MSG_NAME, simpleResult.getErrorMsg());
                }
                directiveDTO.setRemark(JSON.toJSONString(remarkMap));
                logger.info("handle callback result : result={},directiveDTO={}", result, JSON.toJSONString(directiveDTO));
            }
        } catch (Exception e) {
            logger.info("handle result failed : directiveDTO={},   result={}", JSON.toJSONString(directiveDTO), result, e);
        }
    }

    /**
     * null 值判断
     *
     * @param value
     * @param defaultValue
     * @param <T>
     * @return
     */
    protected <T> T ifNull(T value, T defaultValue) {
        return value == null ? defaultValue : value;
    }
}
