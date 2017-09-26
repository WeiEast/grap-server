package com.treefinance.saas.grapserver.biz.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Splitter;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.treefinance.basicservice.security.crypto.facade.EncryptionIntensityEnum;
import com.treefinance.basicservice.security.crypto.facade.ISecurityCryptoService;
import com.treefinance.commonservice.uid.UidGenerator;
import com.treefinance.saas.grapserver.biz.config.DiamondConfig;
import com.treefinance.saas.grapserver.common.enums.EBizType;
import com.treefinance.saas.grapserver.common.enums.ETaskStatus;
import com.treefinance.saas.grapserver.common.enums.ETaskStep;
import com.treefinance.saas.grapserver.common.exception.base.MarkBaseException;
import com.treefinance.saas.grapserver.common.model.dto.TaskDTO;
import com.treefinance.saas.grapserver.common.utils.CommonUtils;
import com.treefinance.saas.grapserver.common.utils.DataConverterUtils;
import com.treefinance.saas.grapserver.dao.entity.AppBizType;
import com.treefinance.saas.grapserver.dao.entity.Task;
import com.treefinance.saas.grapserver.dao.entity.TaskCriteria;
import com.treefinance.saas.grapserver.dao.entity.TaskLog;
import com.treefinance.saas.grapserver.dao.mapper.TaskMapper;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ValidationException;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
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
    private TaskDeviceService taskDeviceService;
    @Autowired
    private TaskAttributeService taskAttributeService;
    @Autowired
    private AppBizTypeService appBizTypeService;
    @Autowired
    private TaskTimeService taskTimeService;
    @Autowired
    private DiamondConfig diamondConfig;

    /**
     * 本地任务缓存
     */
    private final LoadingCache<Long, Task> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .maximumSize(20000)
            .build(CacheLoader.from(taskid -> taskMapper.selectByPrimaryKey(taskid)));

    /**
     * 创建任务
     *
     * @param uniqueId
     * @param appId
     * @param bizType
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Long createTask(String uniqueId, String appId, Byte bizType) {
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
        task.setId(id);
        taskMapper.insertSelective(task);

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

    @Transactional
    public int cancleTask(Long taskId) {
        Task task = new Task();
        task.setId(taskId);
        task.setStatus((byte) 1);
        int result = taskMapper.updateByPrimaryKeySelective(task);
        // 取消任务
        taskLogService.logCancleTask(taskId);
        return result;
    }

    public boolean isDoingTask(Long taskid) {
        Task existTask = taskMapper.selectByPrimaryKey(taskid);
        if (existTask != null && existTask.getStatus() == 0) {
            return true;
        }
        return false;
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
     * 任务是否超时
     *
     * @param taskid
     * @return
     */
    public boolean isTaskTimeout(Long taskid) {
        try {
            Task task = cache.get(taskid);
            AppBizType bizType = appBizTypeService.getAppBizType(task.getBizType());
            if (bizType == null || bizType.getTimeout() == null) {
                return false;
            }
            // 超时时间秒
            int timeout = bizType.getTimeout();
            Date loginTime = taskTimeService.getLoginTime(taskid);
            if (loginTime == null) {
                return false;
            }
            // 未超时: 登录时间+超时时间 < 当前时间
            Date timeoutDate = DateUtils.addSeconds(loginTime, timeout);
            Date current = new Date();
            logger.info("isTaskTimeout: taskid={}，loginTime={},current={},timeout={}",
                    taskid, CommonUtils.date2Str(loginTime), CommonUtils.date2Str(current), timeout);
            if (timeoutDate.after(current)) {
                return false;
            }
        } catch (ExecutionException e) {
            logger.error("task id=" + taskid + "is not exists...", e);
        }
        return true;
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

    /**
     * 开始任务
     *
     * @param uniqueId
     * @param appid
     * @param type
     * @param deviceInfo
     * @param ipAddress
     * @param coorType
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Long startTask(String uniqueId, String appid, EBizType type, String deviceInfo, String ipAddress, String coorType, String extra) throws IOException {
        Long taskId = this.createTask(uniqueId, appid, type.getCode());
        if (StringUtils.isNotBlank(extra)) {
            ObjectMapper objectMapper = new ObjectMapper();
            Map map = objectMapper.readValue(extra, Map.class);
            if (MapUtils.isNotEmpty(map)) {
                setMobileAttribute(taskId, map);
            }
        }
        taskDeviceService.create(deviceInfo, ipAddress, coorType, taskId);
        return taskId;
    }

    private void setMobileAttribute(Long taskId, Map map) {
        String attribute = "mobile";
        String _value = map.get(attribute) == null ? "" : String.valueOf(map.get(attribute));
        if (StringUtils.isNotBlank(_value)) {
            boolean b = CommonUtils.regexMatch(_value, "^1(3|4|5|7|8)[0-9]\\d{8}$");
            if (!b) {
                throw new ValidationException(String.format("the mobile number is illegal! mobile=%s", _value));
            }
            String value = securityCryptoService.encrypt(_value, EncryptionIntensityEnum.NORMAL);
            taskAttributeService.insert(taskId, attribute, value);
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

}
