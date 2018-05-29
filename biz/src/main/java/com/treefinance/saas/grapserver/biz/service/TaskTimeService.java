package com.treefinance.saas.grapserver.biz.service;

import com.alibaba.fastjson.JSON;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.treefinance.saas.assistant.model.Constants;
import com.treefinance.saas.grapserver.biz.cache.RedisDao;
import com.treefinance.saas.grapserver.biz.common.AsycExcutor;
import com.treefinance.saas.grapserver.biz.config.DiamondConfig;
import com.treefinance.saas.grapserver.biz.service.task.TaskTimeoutHandler;
import com.treefinance.saas.grapserver.biz.service.thread.TaskActiveTimeoutThread;
import com.treefinance.saas.grapserver.common.enums.ETaskStatus;
import com.treefinance.saas.grapserver.common.model.dto.TaskDTO;
import com.treefinance.saas.grapserver.common.utils.CommonUtils;
import com.treefinance.saas.grapserver.common.utils.DataConverterUtils;
import com.treefinance.saas.grapserver.common.utils.JsonUtils;
import com.treefinance.saas.grapserver.common.utils.RedisKeyUtils;
import com.treefinance.saas.grapserver.dao.entity.AppBizType;
import com.treefinance.saas.grapserver.dao.entity.Task;
import com.treefinance.saas.grapserver.dao.entity.TaskCriteria;
import com.treefinance.saas.grapserver.dao.mapper.TaskMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 任务时间Service
 * Created by yh-treefinance on 2017/8/3.
 */
@Service
public class TaskTimeService {
    /**
     * logger
     */
    private static final Logger logger = LoggerFactory.getLogger(TaskTimeService.class);
    /**
     * prefix
     */
    private final String prefix = "saas_gateway_task_time:";
    /**
     * taskIds
     */
    private final String taskSetKey = prefix + "login-taskids";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private AppBizTypeService appBizTypeService;

    @Autowired
    private List<TaskTimeoutHandler> taskTimeoutHandlers;

    @Autowired
    private AsycExcutor asycExcutor;
    @Autowired
    private TaskService taskService;
    @Autowired
    private DiamondConfig diamondConfig;
    @Autowired
    private RedisDao redisDao;
    @Autowired
    private TaskAliveService taskAliveService;
    @Autowired
    private ThreadPoolTaskExecutor threadPoolExecutor;

    /**
     * 本地任务缓存
     */
    private final LoadingCache<Long, Task> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .maximumSize(20000)
            .build(CacheLoader.from(taskid -> taskMapper.selectByPrimaryKey(taskid)));


    /**
     * 更新登录时间
     *
     * @param taskId
     * @param date
     */
    public void updateLoginTime(Long taskId, Date date) {
        if (taskId == null || date == null) {
            return;
        }
        String key = prefix + taskId;
        redisTemplate.opsForValue().set(key, String.valueOf(date.getTime()), 1, TimeUnit.HOURS);

        redisTemplate.opsForSet().add(taskSetKey, taskId + "");
        logger.info("updateLoginTime : taskId={}  key={}, value={}", taskId, key, DateFormatUtils.format(date, DateFormatUtils.ISO_DATETIME_FORMAT.getPattern()));
    }

    /**
     * 获取登录时间
     *
     * @param taskId
     * @return
     */
    public Date getLoginTime(Long taskId) {
        String key = prefix + taskId;
        String dateTime = redisTemplate.opsForValue().get(key);
        if (StringUtils.isEmpty(dateTime)) {
            return null;
        }
        return new Date(Long.valueOf(dateTime));
    }

    /**
     * 处理登录后抓取任务超时(注意区分环境)
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void scheduleTaskTimeout() {
        Map<String, Object> lockMap = Maps.newHashMap();
        String lockKey = RedisKeyUtils.genRedisLockKey("task-crawler-time-job", Constants.SAAS_ENV_VALUE);
        try {
            lockMap = redisDao.acquireLock(lockKey, 60 * 1000L);
            if (MapUtils.isEmpty(lockMap)) {
                return;
            }
            Set<String> taskIdSet = redisTemplate.opsForSet().members(taskSetKey);
            logger.info("scheduleTaskTimeout：running ：lock-key={}, taskid-key={}，taskIds={}", lockKey, taskSetKey, JSON.toJSONString(taskIdSet));
            if (CollectionUtils.isEmpty(taskIdSet)) {
                logger.info("scheduleTaskTimeout：taskIds is empty, key={}", taskSetKey);
                return;
            }
            // 处理超时任务
            List<Long> taskIds = taskIdSet.stream().map(id -> Long.valueOf(id)).collect(Collectors.toList());
            for (List<Long> _taskIds : Lists.partition(taskIds, 100)) {
                TaskCriteria criteria = new TaskCriteria();
                criteria.createCriteria().andIdIn(_taskIds).andSaasEnvEqualTo(Byte.parseByte(Constants.SAAS_ENV_VALUE));
                List<Task> tasks = taskMapper.selectByExample(criteria);
                if (CollectionUtils.isEmpty(tasks)) {
                    continue;
                }
                // 清除已经完成任务
                List<String> completedTaskIds = tasks.stream().filter(task -> !ETaskStatus.RUNNING.getStatus().equals(task.getStatus()))
                        .map(task -> task.getId().toString()).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(completedTaskIds)) {
                    redisTemplate.opsForSet().remove(taskSetKey, completedTaskIds.toArray(new String[completedTaskIds.size()]));
                    logger.info("scheduleTaskTimeout : task is completed : completedTaskIds={}", JSON.toJSONString(completedTaskIds));
                }

                List<String> timeoutTaskIds = Lists.newArrayList();
                // 计算正在运行任务是否有超时
                tasks.stream().filter(task -> ETaskStatus.RUNNING.getStatus().equals(task.getStatus()))
                        .forEach(task -> {
                            Long taskId = task.getId();
                            if (handleTaskTimeout(task)) {
                                timeoutTaskIds.add(taskId.toString());
                            }
                        });
                // 清除已经处理的超时任务
                if (CollectionUtils.isNotEmpty(timeoutTaskIds)) {
                    redisTemplate.opsForSet().remove(taskSetKey, timeoutTaskIds.toArray(new String[timeoutTaskIds.size()]));
                    logger.info("scheduleTaskTimeout : task is timeout : timeoutTaskIds={}", JSON.toJSONString(timeoutTaskIds));
                }
            }
        } finally {
            redisDao.releaseLock(lockKey, lockMap, 60 * 1000L);
        }
    }


    /**
     * 处理任务活跃时间超时(任务10分钟不活跃则取消任务)
     * (注意区分环境)
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void scheduleTaskActiveTimeout() {
        Map<String, Object> lockMap = Maps.newHashMap();
        String lockKey = RedisKeyUtils.genRedisLockKey("task-alive-time-job", Constants.SAAS_ENV_VALUE);
        long start = System.currentTimeMillis();
        try {
            Date startTime = new Date();
            lockMap = redisDao.acquireLock(lockKey, 60 * 1000L);
            if (MapUtils.isEmpty(lockMap)) {
                return;
            }
            Date endTime = DateUtils.addMinutes(startTime, -60);
            TaskCriteria criteria = new TaskCriteria();
            criteria.createCriteria().andStatusEqualTo(ETaskStatus.RUNNING.getStatus())
                    .andSaasEnvEqualTo(Byte.parseByte(Constants.SAAS_ENV_VALUE))
                    .andCreateTimeGreaterThanOrEqualTo(endTime)
                    .andCreateTimeLessThan(startTime);

            List<Task> taskList = taskMapper.selectByExample(criteria);
            for (Task task : taskList) {
                threadPoolExecutor.execute(new TaskActiveTimeoutThread(task, startTime));
            }
            logger.info("处理任务活跃时间超时,taskList={}", JSON.toJSONString(taskList));
        } finally {
            redisDao.releaseLock(lockKey, lockMap, 60 * 1000L);
            logger.info("处理任务活跃时间超时,耗时{}ms", System.currentTimeMillis() - start);
        }
    }


    /**
     * 获取超时时间
     *
     * @param bizType
     * @return
     */
    private Integer getTimeout(Byte bizType) {
        AppBizType appBizType = appBizTypeService.getAppBizType(bizType);
        return appBizType.getTimeout();
    }

    /**
     * 处理超时任务
     *
     * @param taskId
     */
    public void handleTaskTimeout(Long taskId) {
        Task task = taskMapper.selectByPrimaryKey(taskId);
        logger.info("handleTaskTimeout async : taskId={}, task={}", taskId, JsonUtils.toJsonString(task));
        if (task != null && ETaskStatus.RUNNING.getStatus().equals(task.getStatus())) {
            asycExcutor.runAsyc(task, _task -> {
                this.handleTaskTimeout(task);
            });
        }
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
            Date loginTime = this.getLoginTime(taskid);
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


    /**
     * 处理任务超时
     *
     * @param task
     * @return
     */
    public boolean handleTaskTimeout(Task task) {
        Long taskId = task.getId();
        Date loginTime = getLoginTime(taskId);
        Integer timeout = getTimeout(task.getBizType());
        TaskDTO taskDTO = DataConverterUtils.convert(task, TaskDTO.class);

        // 任务超时: 当前时间-登录时间>超时时间
        Date currentTime = new Date();
        Date timeoutDate = DateUtils.addSeconds(loginTime, timeout);
        if (timeoutDate.after(currentTime)) {
            return false;
        }
        logger.info("handleTaskTimeout: taskid={}，loginTime={},current={},timeout={}",
                taskId, CommonUtils.date2Str(loginTime), CommonUtils.date2Str(currentTime), timeout);
        taskTimeoutHandlers.forEach(handler -> {
            try {
                handler.handleTaskTimeout(taskDTO, timeout, loginTime);
                logger.info("handle timeout task: handler={},task={},loginTime={},timeout={}",
                        handler.getClass(), taskId, DateFormatUtils.format(loginTime, "yyyyMMdd HH:mm:ss"), timeout);
            } catch (Exception e) {
                logger.error("handle timeout task error: handler={},task={},loginTime={},timeout={}",
                        handler.getClass(), taskId, DateFormatUtils.format(loginTime, "yyyyMMdd HH:mm:ss"), timeout, e);
            }
        });
        return true;
    }

}
