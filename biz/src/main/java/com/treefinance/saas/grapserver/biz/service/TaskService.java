package com.treefinance.saas.grapserver.biz.service;

import com.google.common.base.Splitter;
import com.treefinance.basicservice.security.crypto.facade.EncryptionIntensityEnum;
import com.treefinance.basicservice.security.crypto.facade.ISecurityCryptoService;
import com.treefinance.saas.assistant.model.Constants;
import com.treefinance.saas.grapserver.biz.config.DiamondConfig;
import com.treefinance.saas.grapserver.common.enums.ETaskStatus;
import com.treefinance.saas.grapserver.common.exception.AppIdUncheckException;
import com.treefinance.saas.grapserver.common.exception.BizException;
import com.treefinance.saas.grapserver.common.exception.ForbiddenException;
import com.treefinance.saas.grapserver.common.exception.UnknownException;
import com.treefinance.saas.grapserver.common.exception.base.MarkBaseException;
import com.treefinance.saas.grapserver.common.model.dto.TaskDTO;
import com.treefinance.saas.grapserver.common.utils.DataConverterUtils;
import com.treefinance.saas.grapserver.dao.entity.Task;
import com.treefinance.saas.taskcenter.facade.request.TaskCreateRequest;
import com.treefinance.saas.taskcenter.facade.request.TaskRequest;
import com.treefinance.saas.taskcenter.facade.result.TaskRO;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;
import com.treefinance.saas.taskcenter.facade.service.TaskFacade;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 *
 */
@Service("taskService")
public class TaskService {
    // logger
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * prefix
     */
    private final String prefix = "saas_gateway_task_account_uniqueid:";
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
     *
     * @param uniqueId
     * @param appId
     * @param bizType
     * @param extra
     * @param website
     * @param source
     * @return
     * @throws IOException
     */
    public Long createTask(String uniqueId, String appId, Byte bizType, String extra, String website, String source) {
        if (StringUtils.isBlank(appId)) {
            throw new AppIdUncheckException("appId非法");
        }
        if (bizType == null) {
            logger.error("创建任务时传入的bizType为null");
            throw new ForbiddenException("无操作权限");
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
        if (StringUtils.isNotBlank(website)) {
            rpcRequest.setWebsite(website);
        }
        rpcRequest.setSaasEnv(Byte.valueOf(Constants.SAAS_ENV_VALUE));
        rpcRequest.setExtra(extra);
        TaskResult<Long> rpcResult;
        try {
            rpcResult = taskFacade.createTask(rpcRequest);
        } catch (Exception e) {
            throw new BizException("调用taskcenter异常");
        }
        if (!rpcResult.isSuccess()) {
            throw new BizException("调用taskcenter失败");
        }
        return rpcResult.getData();
    }

    /**
     * uniqueId校验
     *
     * @param uniqueId
     * @param appId
     * @param bizType
     */
    private void checkUniqueId(String uniqueId, String appId, Byte bizType) {
        Integer maxCount = diamondConfig.getMaxCount();
        if (maxCount != null) {
            String key = keyOfUniqueId(uniqueId, appId, bizType);
            Long count = redisTemplate.opsForSet().size(key);
            if (count != null && count >= maxCount) {
                throw new MarkBaseException("0", "uniqueId访问超阈值");
            }
        }
    }

    private String keyOfUniqueId(String uniqueId, String appId, Byte bizType) {
        return prefix + ":" + appId + ":" + bizType + ":" + uniqueId;
    }


    /**
     * 更新未完成任务
     *
     * @param task
     * @return
     */
    private int updateUnfinishedTask(Task task) {
        TaskCreateRequest taskCreateRequest = DataConverterUtils.convert(task, TaskCreateRequest.class);
        TaskResult<Integer> rpcResult = taskFacade.updateUnfinishedTask(taskCreateRequest);
        if (!rpcResult.isSuccess()) {
            throw new UnknownException("调用taskcenter失败");
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
            throw new UnknownException("调用taskcenter失败");
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
     *
     * @param task
     * @return
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
            throw new UnknownException("调用taskcenter失败");
        }
        TaskDTO result = DataConverterUtils.convert(rpcResult.getData(), TaskDTO.class);
        return result;
    }


    /**
     * 更新AccountNo
     *
     * @param taskId
     * @param accountNo
     * @param webSite
     */
    public void updateTask(Long taskId, String accountNo, String webSite) {
        taskFacade.updateTask(taskId, accountNo, webSite);
    }

    public String cancelTaskWithStep(Long taskId) {

        TaskResult<String> rpcResult = taskFacade.cancelTaskWithStep(taskId);
        if (!rpcResult.isSuccess()) {
            throw new UnknownException("调用taskcenter失败");
        }
        return rpcResult.getData();

    }


    public String failTaskWithStep(Long taskId) {

        TaskResult<String> rpcResult = taskFacade.failTaskWithStep(taskId);
        if (!rpcResult.isSuccess()) {
            throw new UnknownException("调用taskcenter失败");
        }
        return rpcResult.getData();
    }


    public String updateTaskStatusWithStep(Long taskId, Byte status) {

        TaskResult<String> rpcResult = taskFacade.updateTaskStatusWithStep(taskId, status);
        if (!rpcResult.isSuccess()) {
            throw new UnknownException("调用taskcenter失败");
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
            throw new UnknownException("调用taskcenter异常");
        }
    }

}
