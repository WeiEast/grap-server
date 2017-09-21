package com.treefinance.saas.grapserver.biz.service.moxie.directive.process;

import com.alibaba.fastjson.JSON;
import com.datatrees.toolkits.util.crypto.RSA;
import com.datatrees.toolkits.util.crypto.core.Decryptor;
import com.datatrees.toolkits.util.crypto.core.Encryptor;
import com.datatrees.toolkits.util.json.Jackson;
import com.google.common.collect.Maps;
import com.treefinance.saas.grapserver.biz.service.*;
import com.treefinance.saas.grapserver.common.enums.ETaskStatus;
import com.treefinance.saas.grapserver.common.enums.moxie.EMoxieDirective;
import com.treefinance.saas.grapserver.common.exception.CallbackEncryptException;
import com.treefinance.saas.grapserver.common.exception.CryptorException;
import com.treefinance.saas.grapserver.common.model.dto.TaskDTO;
import com.treefinance.saas.grapserver.common.model.dto.moxie.MoxieDirectiveDTO;
import com.treefinance.saas.grapserver.common.utils.JsonUtils;
import com.treefinance.saas.grapserver.dao.entity.AppLicense;
import com.treefinance.saas.grapserver.dao.entity.TaskAttribute;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

public abstract class MoxieAbstractDirectiveProcessor implements MoxieDirectiveProcessor {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected TaskService taskService;
    @Autowired
    protected TaskLogService taskLogService;
    @Autowired
    protected TaskNextDirectiveService taskNextDirectiveService;
    @Autowired
    protected TaskAttributeService taskAttributeService;
    @Autowired
    protected AppLicenseService appLicenseService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void process(MoxieDirectiveDTO directiveDTO) {
        long start = System.currentTimeMillis();
        if (directiveDTO == null) {
            logger.error("handle moxie directive error : directive is null");
            return;
        }
        if (directiveDTO.getTaskId() == null && StringUtils.isBlank(directiveDTO.getMoxieTaskId())) {
            logger.error("handle moxie directive error : taskId and moxieTaskId are null");
            return;
        }
        // 1.转化为指令
        String directiveName = directiveDTO.getDirective();
        EMoxieDirective directive = EMoxieDirective.directiveOf(directiveName);
        if (directive == null) {
            logger.error("handle moxie directive error : no support the directive of {}, directive={}", directiveName, JsonUtils.toJsonString(directiveDTO));
            return;
        }
        // 2.初始化任务详细
        Long taskId = directiveDTO.getTaskId();
        if (taskId == null) {
            String moxieTaskId = directiveDTO.getMoxieTaskId();
            TaskAttribute taskAttribute = taskAttributeService.findByNameAndValue("moxie-taskId", moxieTaskId, false);
            if (taskAttribute == null) {
                logger.error("handle moxie directive error : moxieTaskId={} doesn't have taskId matched in task_attribute", moxieTaskId);
                return;
            }
            taskId = taskAttribute.getTaskId();
        }
        TaskDTO taskDTO = directiveDTO.getTask();
        if (taskDTO == null) {
            taskDTO = taskService.getById(taskId);
            if (taskDTO == null) {
                logger.error("handle moxie directive error : taskId={} is not exists, directive={}", taskId, JsonUtils.toJsonString(directiveDTO));
                return;
            }
            directiveDTO.setTask(taskDTO);
        }
        // 3.任务是否是已经完成
        Byte taskStatus = taskDTO.getStatus();
        if (ETaskStatus.CANCEL.getStatus().equals(taskStatus)
                || ETaskStatus.SUCCESS.getStatus().equals(taskStatus)
                || ETaskStatus.FAIL.getStatus().equals(taskStatus)) {
            logger.info("handle moxie directive error : the task id={} is completed: directive={}", taskId, JsonUtils.toJsonString(directiveDTO));
            return;
        }
        // 4.处理指令
        try {
            this.doProcess(directive, directiveDTO);
        } finally {
            taskNextDirectiveService.insert(taskId, directiveDTO.getDirective());
            taskNextDirectiveService.putNextDirectiveToRedis(taskId, JsonUtils.toJsonString(directiveDTO, "task"));
            logger.info("handle moxie directive completed  cost {} ms : directive={}", System.currentTimeMillis() - start, JSON.toJSONString(directiveDTO));
        }
    }

    protected void generateData(MoxieDirectiveDTO directiveDTO, AppLicense appLicense) {
        TaskDTO task = directiveDTO.getTask();
        Map<String, Object> dataMap = ifNull(JSON.parseObject(directiveDTO.getRemark()), Maps.newHashMap());
        dataMap.put("uniqueId", ifNull(dataMap.get("uniqueId"), directiveDTO.getTask().getUniqueId()));
        dataMap.put("taskId", ifNull(dataMap.get("taskId"), directiveDTO.getTask().getId()));
        dataMap.put("taskErrorMsg", ifNull(dataMap.get("taskErrorMsg"), ""));

        dataMap.put("taskStatus", "001");
        // 此次任务状态：001-抓取成功，002-抓取失败，003-抓取结果为空,004-任务取消
        if (ETaskStatus.SUCCESS.getStatus().equals(task.getStatus())) {
            dataMap.put("taskStatus", "001");
        } else if (ETaskStatus.FAIL.getStatus().equals(task.getStatus())) {
            dataMap.put("taskStatus", "002");
        } else if (ETaskStatus.CANCEL.getStatus().equals(task.getStatus())) {
            dataMap.put("taskStatus", "004");

        }
        logger.info("handle moxie directive generateData :data={}", JSON.toJSONString(dataMap));
        try {
            String params = encryptByRSA(dataMap, appLicense);
            Map<String, Object> paramMap = Maps.newHashMap();
            paramMap.put("params", params);
            directiveDTO.setRemark(JSON.toJSONString(paramMap));
        } catch (Exception e) {
            logger.error("handle moxie directive error :encryptByRSA error dataMap={},key={}", dataMap, appLicense.getServerPublicKey(), e);
            directiveDTO.setRemark("指令信息处理失败");
        }
    }

    protected <T> T ifNull(T value, T defaultValue) {
        return value == null ? defaultValue : value;
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
        String rsaPublicKey = appLicense.getServerPublicKey();
        String encryptedData = Helper.encryptResult(dataMap, rsaPublicKey);
        String params = URLEncoder.encode(encryptedData, "utf-8");
        return params;
    }

    static class Helper {


        public static Encryptor getEncryptor(String publicKey) {
            try {
                if (StringUtils.isEmpty(publicKey)) {
                    throw new IllegalArgumentException("Can not find commercial tenant's public key.");
                }

                return RSA.createEncryptor(publicKey);
            } catch (Exception e) {
                throw new CryptorException(
                        "Error creating Encryptor with publicKey '" + publicKey + " to encrypt callback.", e);
            }
        }

        public static String encryptResult(Object data, String publicKey) throws CallbackEncryptException {
            Encryptor encryptor = getEncryptor(publicKey);
            try {
                byte[] json = Jackson.toJSONByteArray(data);
                return encryptor.encryptAsBase64String(json);
            } catch (Exception e) {
                throw new CallbackEncryptException("Error encrypting callback data", e);
            }
        }

        public static Decryptor getDecryptor(String privateKey) {
            try {
                if (StringUtils.isEmpty(privateKey)) {
                    throw new IllegalArgumentException("Can not find commercial tenant's private key.");
                }
                return RSA.createDecryptor(privateKey);
            } catch (Exception e) {
                throw new CryptorException(
                        "Error creating Decryptor with privateKey '" + privateKey + " to encrypt callback.", e);
            }
        }

        public static String decryptResult(Object data, String privateKey) throws CallbackEncryptException {
            Decryptor decryptor = getDecryptor(privateKey);
            try {
                byte[] json = Jackson.toJSONByteArray(data);
                return decryptor.decryptWithBase64AsString(json);
            } catch (Exception e) {
                throw new CallbackEncryptException("Error decrypting callback data", e);
            }
        }
    }

    /**
     * 处理指令
     *
     * @param directive
     * @param directiveDTO
     */
    protected abstract void doProcess(EMoxieDirective directive, MoxieDirectiveDTO directiveDTO);


}
