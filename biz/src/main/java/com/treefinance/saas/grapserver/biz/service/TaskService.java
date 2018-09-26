package com.treefinance.saas.grapserver.biz.service;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.treefinance.basicservice.security.crypto.facade.EncryptionIntensityEnum;
import com.treefinance.basicservice.security.crypto.facade.ISecurityCryptoService;
import com.treefinance.saas.assistant.model.Constants;
import com.treefinance.saas.grapserver.biz.config.DiamondConfig;
import com.treefinance.saas.grapserver.common.enums.ETaskStatus;
import com.treefinance.saas.grapserver.common.enums.ETaskStep;
import com.treefinance.saas.grapserver.common.exception.AppIdUncheckException;
import com.treefinance.saas.grapserver.common.exception.BizException;
import com.treefinance.saas.grapserver.common.exception.ForbiddenException;
import com.treefinance.saas.grapserver.common.exception.base.MarkBaseException;
import com.treefinance.saas.grapserver.common.model.dto.TaskDTO;
import com.treefinance.saas.grapserver.common.utils.DataConverterUtils;
import com.treefinance.saas.grapserver.dao.entity.Task;
import com.treefinance.saas.grapserver.dao.entity.TaskCriteria;
import com.treefinance.saas.grapserver.dao.entity.TaskLog;
import com.treefinance.saas.grapserver.dao.mapper.TaskMapper;
import com.treefinance.saas.taskcenter.facade.request.TaskCreateRequest;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;
import com.treefinance.saas.taskcenter.facade.service.TaskFacade;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private TaskMapper taskMapper;
    @Autowired
    private ISecurityCryptoService securityCryptoService;
    @Autowired
    private TaskLogService taskLogService;
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
        TaskCriteria taskCriteria = new TaskCriteria();
        taskCriteria.createCriteria().andIdEqualTo(task.getId())
                .andStatusNotIn(Lists.newArrayList(ETaskStatus.CANCEL.getStatus(),
                        ETaskStatus.SUCCESS.getStatus(), ETaskStatus.FAIL.getStatus()));
        return taskMapper.updateByExampleSelective(task, taskCriteria);
    }

    public int updateWebSite(Long taskId, String webSite) {
        Task task = new Task();
        task.setId(taskId);
        task.setWebSite(webSite);

        return updateUnfinishedTask(task);
    }

    public int setAccountNo(Long taskId, String accountNo) {
        Task existTask = taskMapper.selectByPrimaryKey(taskId);
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
        Task task = taskMapper.selectByPrimaryKey(taskId);
        if (task == null) {
            return null;
        }
        TaskDTO result = DataConverterUtils.convert(task, TaskDTO.class);
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
        if (taskId == null || StringUtils.isEmpty(accountNo)) {
            return;
        }
        String _accountNo = securityCryptoService.encrypt(accountNo, EncryptionIntensityEnum.NORMAL);
        Task task = new Task();
        task.setId(taskId);
        task.setAccountNo(_accountNo);
        task.setWebSite(webSite);
        updateUnfinishedTask(task);
    }

    @Transactional
    public String cancelTaskWithStep(Long taskId) {

        Task task = new Task();
        task.setId(taskId);
        task.setStatus(ETaskStatus.CANCEL.getStatus());
        TaskLog taskLog = taskLogService.queryLastestErrorLog(taskId);
        if (taskLog != null) {
            task.setStepCode(taskLog.getStepCode());
        } else {
            logger.error("更新任务状态为取消时,未查询到取消任务日志信息 taskId={}", taskId);
        }
        updateUnfinishedTask(task);
        // 取消任务
        taskLogService.logCancleTask(taskId);
        return task.getStepCode();

    }


    @Transactional
    public String failTaskWithStep(Long taskId) {
        TaskCriteria taskCriteria = new TaskCriteria();
        taskCriteria.createCriteria().andIdEqualTo(taskId).
                andStatusNotIn(Lists.newArrayList(ETaskStatus.CANCEL.getStatus(),
                        ETaskStatus.SUCCESS.getStatus(), ETaskStatus.FAIL.getStatus()));
        ;
        Task task = new Task();
        task.setId(taskId);
        task.setStatus(ETaskStatus.FAIL.getStatus());
        TaskLog taskLog = taskLogService.queryLastestErrorLog(taskId);
        if (taskLog != null) {
            task.setStepCode(taskLog.getStepCode());
        } else {
            logger.error("更新任务状态为失败时,未查询到失败任务日志信息 taskId={}", taskId);
        }
        taskMapper.updateByExampleSelective(task, taskCriteria);
        //如果任务是超时导致的失败,则不记录任务失败日志了
        if (!ETaskStep.TASK_TIMEOUT.getStepCode().equals(task.getStepCode())) {
            taskLogService.logFailTask(taskId);
        }
        return task.getStepCode();
    }


    @Transactional
    public String updateTaskStatusWithStep(Long taskId, Byte status) {
        if (ETaskStatus.SUCCESS.getStatus().equals(status)) {
            Task task = new Task();
            task.setId(taskId);
            task.setStatus(status);
            updateUnfinishedTask(task);
            taskLogService.logSuccessTask(taskId);
            return null;
        }
        if (ETaskStatus.FAIL.getStatus().equals(status)) {
            return this.failTaskWithStep(taskId);
        }
        return null;
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
            throw new BizException("调用taskcenter异常");
        }
    }

}
