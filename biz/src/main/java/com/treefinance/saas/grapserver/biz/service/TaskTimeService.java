package com.treefinance.saas.grapserver.biz.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.treefinance.saas.assistant.model.Constants;
import com.treefinance.saas.grapserver.biz.cache.RedisDao;
import com.treefinance.saas.grapserver.biz.service.task.TaskTimeoutHandler;
import com.treefinance.saas.grapserver.biz.service.thread.TaskActiveTimeoutThread;
import com.treefinance.saas.grapserver.biz.service.thread.TaskCrawlerTimeoutThread;
import com.treefinance.saas.grapserver.common.enums.ETaskStatus;
import com.treefinance.saas.grapserver.common.utils.CommonUtils;
import com.treefinance.saas.grapserver.common.utils.GrapDateUtils;
import com.treefinance.saas.grapserver.common.utils.RedisKeyUtils;
import com.treefinance.saas.grapserver.dao.entity.AppBizType;
import com.treefinance.saas.grapserver.dao.entity.Task;
import com.treefinance.saas.grapserver.dao.entity.TaskAttribute;
import com.treefinance.saas.grapserver.dao.entity.TaskCriteria;
import com.treefinance.saas.grapserver.dao.mapper.TaskMapper;
import com.treefinance.saas.grapserver.facade.enums.ETaskAttribute;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private AppBizTypeService appBizTypeService;
    @Autowired
    private List<TaskTimeoutHandler> taskTimeoutHandlers;
    @Autowired
    private RedisDao redisDao;
    @Autowired
    private TaskAttributeService taskAttributeService;
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
        taskAttributeService.insertOrUpdateSelective(taskId, ETaskAttribute.LOGIN_TIME.getAttribute(), GrapDateUtils.getDateStrByDate(date));

        String key = RedisKeyUtils.genTaskLoginTimeKey(taskId);
        redisDao.setEx(key, String.valueOf(date.getTime()), 1, TimeUnit.HOURS);
        logger.info("记录任务登录时间:taskId={},key={},value={}", taskId, key, GrapDateUtils.getDateStrByDate(date));
    }

    /**
     * 获取登录时间
     *
     * @param taskId
     * @return
     */
    public Date getLoginTime(Long taskId) {
        String key = RedisKeyUtils.genTaskLoginTimeKey(taskId);
        String dateTime = redisDao.get(key);
        if (StringUtils.isNotBlank(dateTime)) {
            return new Date(Long.valueOf(dateTime));
        } else {
            TaskAttribute taskAttribute = taskAttributeService.findByName(taskId, ETaskAttribute.LOGIN_TIME.getAttribute(), false);
            if (taskAttribute == null) {
                logger.info("获取登录时间时,未查询到任务登录时间,任务未登录.taskId={}", taskId);
                return null;
            }
            Date date = GrapDateUtils.getDateByStr(taskAttribute.getValue());
            this.updateLoginTime(taskId, date);
            return date;
        }
    }

    /**
     * 获取任务抓取超时时间
     *
     * @param taskId
     * @return
     */
    public Date getCrawlerTimeoutTime(Long taskId) {
        Date loginTime = this.getLoginTime(taskId);
        if (loginTime == null) {
            return null;
        }
        Integer timeoutSeconds = this.getCrawlerTimeoutSeconds(taskId);
        if (timeoutSeconds == null) {
            return null;
        }
        Date timeoutDate = DateUtils.addSeconds(loginTime, timeoutSeconds);
        return timeoutDate;
    }

    /**
     * 获取设置的任务抓取超时时长
     *
     * @param taskId
     * @return
     */
    public Integer getCrawlerTimeoutSeconds(Long taskId) {
        Task task = null;
        try {
            task = cache.get(taskId);
        } catch (ExecutionException e) {
            logger.error("获取设置的任务抓取超时时长时,未查询到任务信息taskId={}", taskId);
        }
        if (task == null) {
            return null;
        }
        AppBizType bizType = appBizTypeService.getAppBizType(task.getBizType());
        if (bizType == null || bizType.getTimeout() == null) {
            logger.error("获取设置的任务抓取超时时长时,未查询到任务相关的bizType信息,taskId={}", taskId);
            return null;
        }
        return bizType.getTimeout();
    }


    /**
     * 任务抓取是否超时
     *
     * @param taskId
     * @return
     */
    public boolean isTaskTimeout(Long taskId) {
        Date current = new Date();
        Date timeoutTime = this.getCrawlerTimeoutTime(taskId);
        if (timeoutTime == null) {
            return false;
        }
        if (timeoutTime.after(current)) {
            return false;
        }
        logger.info("任务抓取超时:taskId={},currentTime={},timeoutTime={}",
                taskId, CommonUtils.date2Str(current), CommonUtils.date2Str(timeoutTime));
        return true;
    }

    /**
     * 处理任务抓取超时
     *
     * @param taskId
     */
    public void handleTaskTimeout(Long taskId) {
        logger.info("任务抓取超时异步处理:taskId={}", taskId);
        threadPoolExecutor.execute(new TaskCrawlerTimeoutThread(taskId, taskTimeoutHandlers));
    }

    /**
     * 处理登录后抓取任务超时(注意区分环境)
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void scheduleTaskTimeout() {
        Map<String, Object> lockMap = Maps.newHashMap();
        String lockKey = RedisKeyUtils.genRedisLockKey("task-crawler-time-job", Constants.SAAS_ENV_VALUE);
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
            List<Task> tasks = taskMapper.selectByExample(criteria);
            for (Task task : tasks) {
                if (this.isTaskTimeout(task.getId())) {
                    //处理抓取超时任务
                    this.handleTaskTimeout(task.getId());
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
        } finally {
            redisDao.releaseLock(lockKey, lockMap, 60 * 1000L);
        }
    }


}
