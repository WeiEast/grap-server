package com.treefinance.saas.grapserver.biz.service;

import com.google.common.base.Splitter;
import com.treefinance.basicservice.security.crypto.facade.EncryptionIntensityEnum;
import com.treefinance.basicservice.security.crypto.facade.ISecurityCryptoService;
import com.treefinance.saas.assistant.model.Constants;
import com.treefinance.saas.grapserver.biz.config.DiamondConfig;
import com.treefinance.saas.grapserver.common.enums.ETaskStatus;
import com.treefinance.saas.grapserver.common.exception.*;
import com.treefinance.saas.grapserver.common.exception.base.MarkBaseException;
import com.treefinance.saas.grapserver.common.model.dto.TaskDTO;
import com.treefinance.saas.grapserver.common.utils.DataConverterUtils;
import com.treefinance.saas.grapserver.dao.entity.Task;
import com.treefinance.saas.taskcenter.facade.request.TaskCreateRequest;
import com.treefinance.saas.taskcenter.facade.request.TaskRequest;
import com.treefinance.saas.taskcenter.facade.request.TaskUpdateRequest;
import com.treefinance.saas.taskcenter.facade.result.TaskRO;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;
import com.treefinance.saas.taskcenter.facade.service.TaskFacade;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.xml.bind.ValidationException;
import java.io.IOException;
import java.util.List;

/**
 * @author guoguoyun
 * @date Created in 2018/11/1下午3:32
 */
@Service("saasTaskService")
public class SaasTaskService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private ISecurityCryptoService securityCryptoService;
    @Autowired
    private DiamondConfig diamondConfig;
    @Autowired
    private TaskFacade taskFacade;

    /**
     * 创建任务
     */
    public Long createTask(String uniqueId, String appId, Byte bizType, String extra, String website, String source)
        throws ValidationException {
        if (StringUtils.isBlank(appId)) {
            throw new ValidationException("appid为空");
        }
        if (bizType == null) {
            logger.error("创建任务时传入的bizType为null");
            throw new ParamsCheckException("bizType为null");
        }
        // 校验uniqueId
        String excludeAppId = diamondConfig.getExcludeAppId();
        if (StringUtils.isNotEmpty(excludeAppId)) {
            List<String> excludeAppIdList = Splitter.on(",").trimResults().splitToList(excludeAppId);
            if (!excludeAppIdList.contains(appId)) {
                checkUniqueId(uniqueId, appId, bizType);
            }
        } else {
            checkUniqueId(uniqueId, appId, bizType);
        }

        TaskCreateRequest rpcRequest = new TaskCreateRequest();
        rpcRequest.setUniqueId(uniqueId);
        rpcRequest.setAppId(appId);
        rpcRequest.setBizType(bizType);
        rpcRequest.setStatus((byte) 0);
        rpcRequest.setSource(source);
        if (StringUtils.isNotBlank(website)) {
            rpcRequest.setWebsite(website);
        }
        rpcRequest.setSaasEnv(Byte.valueOf(Constants.SAAS_ENV_VALUE));
        rpcRequest.setExtra(extra);
        TaskResult<Long> rpcResult;
        try {
            rpcResult = taskFacade.createTask(rpcRequest);
        } catch (Exception e) {
            logger.error("调用taskcenter异常", e);
            throw new UnknownException();
        }
        if (!rpcResult.isSuccess()) {
            throw new UnknownException();
        }
        return rpcResult.getData();
    }

    /**
     * uniqueId校验
     */
    private void checkUniqueId(String uniqueId, String appId, Byte bizType) {
        Integer maxCount = diamondConfig.getMaxCount();
        if (maxCount != null) {
            String key = keyOfUniqueId(uniqueId, appId, bizType);
            Long count = redisTemplate.opsForSet().size(key);
            if (count != null && count >= maxCount) {
                throw new UniqueidMaxException();
            }
        }
    }

    private String keyOfUniqueId(String uniqueId, String appId, Byte bizType) {
       // prefix
        String prefix = "saas_gateway_task_account_uniqueid:";
        return prefix + ":" + appId + ":" + bizType + ":" + uniqueId;
    }


    /**
     * 更新未完成任务
     */
    private int updateUnfinishedTask(Task task) {
        TaskUpdateRequest taskUpdateRequest = DataConverterUtils.convert(task, TaskUpdateRequest.class);
        TaskResult<Integer> rpcResult = taskFacade.updateUnfinishedTask(taskUpdateRequest);
        if (!rpcResult.isSuccess()) {
            throw new UnknownException();
        }
        return rpcResult.getData();
    }

    public int updateWebSite(Long taskId, String webSite) {
        Task task = new Task();
        task.setId(taskId);
        task.setWebSite(webSite);

        return updateUnfinishedTask(task);
    }

    public int setAccountNo(Long taskId, String accountNo) {
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setId(taskId);
        TaskResult<TaskRO> rpcResult = taskFacade.getTaskByPrimaryKey(taskRequest);
        if (!rpcResult.isSuccess()) {
            throw new UnknownException();
        }
        Task existTask = DataConverterUtils.convert(rpcResult.getData(), Task.class);
        if (existTask != null && StringUtils.isEmpty(existTask.getAccountNo())) {
            Task task = new Task();
            task.setId(taskId);
            task.setAccountNo(securityCryptoService.encrypt(accountNo, EncryptionIntensityEnum.NORMAL));

            String key = keyOfUniqueId(existTask.getUniqueId(), existTask.getAppId(), existTask.getBizType());
            redisTemplate.opsForSet().add(key, accountNo);
            return updateUnfinishedTask(task);
        } else {
            return -1;
        }
    }


    /**
     * 任务是否完成
     */
    public boolean isTaskCompleted(TaskDTO task) {
        if (task == null) {
            return false;
        }
        Byte status = task.getStatus();
        if (ETaskStatus.CANCEL.getStatus().equals(status)
            || ETaskStatus.FAIL.getStatus().equals(status)
            || ETaskStatus.SUCCESS.getStatus().equals(status)) {
            return true;
        }
        return false;
    }


    public TaskDTO getById(Long taskId) {
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setId(taskId);
        TaskResult<TaskRO> rpcResult = taskFacade.getTaskByPrimaryKey(taskRequest);
        if (!rpcResult.isSuccess()) {
            throw new UnknownException();
        }
        TaskDTO result = DataConverterUtils.convert(rpcResult.getData(), TaskDTO.class);
        return result;
    }


    /**
     * 更新AccountNo
     */
    public void updateTask(Long taskId, String accountNo, String webSite) {
        taskFacade.updateTask(taskId, accountNo, webSite);
    }

    public String cancelTaskWithStep(Long taskId) {

        TaskResult<String> rpcResult = taskFacade.cancelTaskWithStep(taskId);
        if (!rpcResult.isSuccess()) {
            throw new UnknownException();
        }
        return rpcResult.getData();

    }


    public String failTaskWithStep(Long taskId) {

        TaskResult<String> rpcResult = taskFacade.failTaskWithStep(taskId);
        if (!rpcResult.isSuccess()) {
            throw new UnknownException();
        }
        return rpcResult.getData();
    }


    public String updateTaskStatusWithStep(Long taskId, Byte status) {

        TaskResult<String> rpcResult = taskFacade.updateTaskStatusWithStep(taskId, status);
        if (!rpcResult.isSuccess()) {
            throw new UnknownException();
        }
        return rpcResult.getData();
    }


    /**
     * 正常流程下取消任务
     *
     * @param taskId 任务id
     */
    public void cancelTask(Long taskId) {
        logger.info("取消任务 : taskId={} ", taskId);
        try {
            taskFacade.cancelTask(taskId);
        } catch (Exception e) {
            logger.error("调用taskcenter异常", e);
            throw new UnknownException();
        }
    }
}
