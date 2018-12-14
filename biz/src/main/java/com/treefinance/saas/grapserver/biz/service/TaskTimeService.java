package com.treefinance.saas.grapserver.biz.service;

import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.treefinance.saas.assistant.model.Constants;
import com.treefinance.saas.grapserver.biz.domain.AppBizType;
import com.treefinance.saas.grapserver.common.exception.UnknownException;
import com.treefinance.saas.grapserver.context.component.AbstractService;
import com.treefinance.saas.grapserver.manager.TaskManager;
import com.treefinance.saas.grapserver.manager.domain.TaskBO;
import com.treefinance.saas.grapserver.share.cache.redis.RedisDao;
import com.treefinance.saas.grapserver.share.cache.redis.RedisKeyUtils;
import com.treefinance.saas.grapserver.util.SystemUtils;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;
import com.treefinance.saas.taskcenter.facade.service.TaskFacade;
import com.treefinance.saas.taskcenter.facade.service.TaskTimeFacade;
import com.treefinance.toolkit.util.DateUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 任务时间Service
 * 
 * @author yh-treefinance on 2017/8/3.
 */
@Service
public class TaskTimeService extends AbstractService {

    @Autowired
    private AppBizTypeService appBizTypeService;
    @Autowired
    private TaskTimeFacade taskTimeFacade;
    @Autowired
    private TaskManager taskManager;
    @Autowired
    private RedisDao redisDao;

    /**
     * 本地任务缓存
     */
    private final LoadingCache<Long, TaskBO> cache =
        CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).maximumSize(20000).build(CacheLoader.from(new Function<Long, TaskBO>() {
            @Override
            public TaskBO apply(Long taskId) {
                return taskManager.getTaskById(taskId);
            }
        }));

    /**
     * 更新登录时间
     */
    public void updateLoginTime(Long taskId, Date date) {
        if (taskId == null || date == null) {
            return;
        }
        taskTimeFacade.updateLoginTime(taskId, date);
    }

    /**
     * 获取登录时间
     */
    public Date getLoginTime(Long taskId) {
        TaskResult<Date> rpcResult = taskTimeFacade.getLoginTime(taskId);
        if (!rpcResult.isSuccess()) {
            throw new UnknownException("调用taskcenter失败");
        }
        return rpcResult.getData();
    }

    /**
     * 获取任务抓取超时时间
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
        return DateUtils.plusSeconds(loginTime, timeoutSeconds);
    }

    /**
     * 获取设置的任务抓取超时时长
     */
    public Integer getCrawlerTimeoutSeconds(Long taskId) {
        TaskBO task = cache.getUnchecked(taskId);

        AppBizType bizType = appBizTypeService.getAppBizType(task.getBizType());

        return bizType.getTimeout();
    }

    /**
     * 任务抓取是否超时
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
        logger.info("任务抓取超时:taskId={},currentTime={},timeoutTime={}", taskId, DateUtils.format(current), DateUtils.format(timeoutTime));
        return true;
    }

    /**
     * 处理任务抓取超时
     */
    public void handleTaskTimeout(Long taskId) {
        logger.info("任务抓取超时异步处理:taskId={}", taskId);
        taskTimeFacade.handleTaskTimeout(taskId);
    }

    /**
     * 处理任务抓取超时
     */
    public void handleTaskAliveTimeout(Long taskId, Date startTime) {
        logger.info("任务活跃超时异步处理:taskId={}", taskId);
        taskTimeFacade.handleTaskAliveTimeout(taskId, startTime);
    }

    /**
     * 处理登录后抓取任务超时(注意区分环境)
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void scheduleTaskTimeout() {
        String lockKey = RedisKeyUtils.genRedisLockKey("task-crawler-time-job", Constants.SAAS_ENV_VALUE);
        Map<String, Object> lockMap = null;
        try {
            lockMap = redisDao.acquireLock(lockKey, 60 * 1000L);
            if (MapUtils.isEmpty(lockMap)) {
                return;
            }

            Date now = SystemUtils.now();
            Date before = DateUtils.minusMinutes(now, 60);
            List<TaskBO> tasks = taskManager.listRunningTasks(Byte.valueOf(Constants.SAAS_ENV_VALUE), before, now);
            for (TaskBO task : tasks) {
                if (this.isTaskTimeout(task.getId())) {
                    // 处理抓取超时任务
                    this.handleTaskTimeout(task.getId());
                }
            }

        } finally {
            redisDao.releaseLock(lockKey, lockMap, 60 * 1000L);
        }
    }

    /**
     * 处理任务活跃时间超时(任务10分钟不活跃则取消任务) (注意区分环境)
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void scheduleTaskActiveTimeout() {
        String lockKey = RedisKeyUtils.genRedisLockKey("task-alive-time-job", Constants.SAAS_ENV_VALUE);
        Map<String, Object> lockMap = null;
        try {
            lockMap = redisDao.acquireLock(lockKey, 60 * 1000L);
            if (MapUtils.isEmpty(lockMap)) {
                return;
            }

            Date now = new Date();
            Date before = DateUtils.minusMinutes(now, 60);
            List<TaskBO> tasks = taskManager.listRunningTasks(Byte.valueOf(Constants.SAAS_ENV_VALUE), before, now);
            for (TaskBO task : tasks) {
                this.handleTaskAliveTimeout(task.getId(), now);
            }
        } finally {
            redisDao.releaseLock(lockKey, lockMap, 60 * 1000L);
        }
    }

}
