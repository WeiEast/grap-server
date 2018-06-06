package com.treefinance.saas.grapserver.biz.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Splitter;
import com.treefinance.basicservice.security.crypto.facade.EncryptionIntensityEnum;
import com.treefinance.basicservice.security.crypto.facade.ISecurityCryptoService;
import com.treefinance.commonservice.uid.UidGenerator;
import com.treefinance.saas.assistant.model.Constants;
import com.treefinance.saas.grapserver.biz.config.DiamondConfig;
import com.treefinance.saas.grapserver.biz.service.directive.DirectiveService;
import com.treefinance.saas.grapserver.common.enums.EDirective;
import com.treefinance.saas.grapserver.common.enums.ETaskStatus;
import com.treefinance.saas.grapserver.common.enums.ETaskStep;
import com.treefinance.saas.grapserver.common.exception.AppIdUncheckException;
import com.treefinance.saas.grapserver.common.exception.ForbiddenException;
import com.treefinance.saas.grapserver.common.exception.base.MarkBaseException;
import com.treefinance.saas.grapserver.common.model.dto.DirectiveDTO;
import com.treefinance.saas.grapserver.common.model.dto.TaskDTO;
import com.treefinance.saas.grapserver.common.utils.CommonUtils;
import com.treefinance.saas.grapserver.common.utils.DataConverterUtils;
import com.treefinance.saas.grapserver.dao.entity.Task;
import com.treefinance.saas.grapserver.dao.entity.TaskCriteria;
import com.treefinance.saas.grapserver.dao.entity.TaskLog;
import com.treefinance.saas.grapserver.dao.mapper.TaskMapper;
import com.treefinance.saas.grapserver.facade.enums.ETaskAttribute;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ValidationException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private TaskAttributeService taskAttributeService;
    @Autowired
    private DiamondConfig diamondConfig;
    @Autowired
    private DirectiveService directiveService;

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
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
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

        long id = UidGenerator.getId();
        Task task = new Task();
        task.setUniqueId(uniqueId);
        task.setAppId(appId);
        task.setBizType(bizType);
        task.setStatus((byte) 0);
        if (StringUtils.isNotBlank(website)) {
            task.setWebSite(website);
        }
        task.setId(id);
        task.setSaasEnv(Byte.valueOf(Constants.SAAS_ENV_VALUE));
        taskMapper.insertSelective(task);
        if (StringUtils.isNotBlank(extra)) {
            JSONObject jsonObject = JSON.parseObject(extra);
            if (MapUtils.isNotEmpty(jsonObject)) {
                setAttribute(id, jsonObject);
            }
        }
        if (StringUtils.isNotEmpty(source)) {
            taskAttributeService.insert(id, ETaskAttribute.SOURCE_TYPE.getAttribute(), source);
        }
        // 记录创建日志
        taskLogService.logCreateTask(id);
        return id;
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
     * 更改任务状态
     *
     * @param taskId
     * @param status
     * @return
     */
    public int updateTaskStatus(Long taskId, Byte status) {
        Task task = new Task();
        task.setId(taskId);
        task.setStatus(status);
        return taskMapper.updateByPrimaryKeySelective(task);
    }

    public int updateWebSite(Long taskId, String webSite) {
        Task task = new Task();
        task.setId(taskId);
        task.setWebSite(webSite);
        return taskMapper.updateByPrimaryKeySelective(task);
    }

    public int updateTaskAction(Long taskId, String webSite) {
        Task task = new Task();
        task.setId(taskId);
        task.setWebSite(webSite);
        return taskMapper.updateByPrimaryKeySelective(task);
    }

    public int setAccountNo(Long taskId, String accountNo) {
        Task existTask = taskMapper.selectByPrimaryKey(taskId);
        if (existTask != null && StringUtils.isEmpty(existTask.getAccountNo())) {
            Task task = new Task();
            task.setId(taskId);
            task.setAccountNo(securityCryptoService.encrypt(accountNo, EncryptionIntensityEnum.NORMAL));

            String key = keyOfUniqueId(existTask.getUniqueId(), existTask.getAppId(), existTask.getBizType());
            redisTemplate.opsForSet().add(key, accountNo);
            return taskMapper.updateByPrimaryKeySelective(task);
        } else {
            return -1;
        }
    }

    /**
     * 任务是否完成
     *
     * @param taskid
     * @return
     */
    public boolean isTaskCompleted(Long taskid) {
        if (taskid == null) {
            return false;
        }
        Task existTask = taskMapper.selectByPrimaryKey(taskid);
        if (existTask == null) {
            return false;
        }
        Byte status = existTask.getStatus();
        if (ETaskStatus.CANCEL.getStatus().equals(status)
                || ETaskStatus.FAIL.getStatus().equals(status)
                || ETaskStatus.SUCCESS.getStatus().equals(status)) {
            return true;
        }
        return false;
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

    public List<Long> getUserTaskIdList(Long taskId) {
        Task task = taskMapper.selectByPrimaryKey(taskId);
        TaskCriteria taskCriteria = new TaskCriteria();
        taskCriteria.createCriteria().andUniqueIdEqualTo(task.getUniqueId()).andAppIdEqualTo(task.getAppId()).andBizTypeEqualTo(task.getBizType());
        List<Long> list = taskMapper.selectByExample(taskCriteria).stream().map(s -> s.getId()).collect(Collectors.toList());
        return list;
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
     * @param taskId
     * @return
     */
    public String getAppIdById(Long taskId) {
        Task task = taskMapper.selectByPrimaryKey(taskId);
        return task == null ? null : task.getAppId();
    }

    private void setAttribute(Long taskId, Map map) {
        String mobileAttribute = ETaskAttribute.MOBILE.getAttribute();
        String nameAttribute = ETaskAttribute.NAME.getAttribute();
        String idCardAttribute = ETaskAttribute.ID_CARD.getAttribute();
        String mobile = map.get(mobileAttribute) == null ? "" : String.valueOf(map.get(mobileAttribute));
        if (StringUtils.isNotBlank(mobile)) {
            boolean b = CommonUtils.regexMatch(mobile, "^1(3|4|5|6|7|8|9)[0-9]\\d{8}$");
            if (!b) {
                throw new ValidationException(String.format("the mobile number is illegal! mobile=%s", mobile));
            }
            String value = securityCryptoService.encrypt(mobile, EncryptionIntensityEnum.NORMAL);
            taskAttributeService.insert(taskId, mobileAttribute, value);
        }
        String name = map.get(nameAttribute) == null ? "" : String.valueOf(map.get(nameAttribute));
        if (StringUtils.isNotBlank(name)) {
            String value = securityCryptoService.encrypt(name, EncryptionIntensityEnum.NORMAL);
            taskAttributeService.insert(taskId, nameAttribute, value);
        }
        String idCard = map.get(idCardAttribute) == null ? "" : String.valueOf(map.get(idCardAttribute));
        if (StringUtils.isNotBlank(idCard)) {
            String value = securityCryptoService.encrypt(idCard, EncryptionIntensityEnum.NORMAL);
            taskAttributeService.insert(taskId, idCardAttribute, value);
        }
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
        taskMapper.updateByPrimaryKeySelective(task);
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
        taskMapper.updateByPrimaryKeySelective(task);
        // 取消任务
        taskLogService.logCancleTask(taskId);
        return task.getStepCode();

    }


    @Transactional
    public String failTaskWithStep(Long taskId) {
        TaskCriteria taskCriteria = new TaskCriteria();
        taskCriteria.createCriteria().andIdEqualTo(taskId).andStatusNotEqualTo(ETaskStatus.FAIL.getStatus());
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
            taskMapper.updateByPrimaryKeySelective(task);
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
        Task existTask = taskMapper.selectByPrimaryKey(taskId);
        if (existTask != null && existTask.getStatus() == 0) {
            logger.info("取消正在执行任务 : taskId={} ", taskId);
            DirectiveDTO cancelDirective = new DirectiveDTO();
            cancelDirective.setTaskId(taskId);
            cancelDirective.setDirective(EDirective.TASK_CANCEL.getText());
            directiveService.process(cancelDirective);
        }
    }

}
