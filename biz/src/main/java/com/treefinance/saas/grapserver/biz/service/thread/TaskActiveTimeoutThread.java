package com.treefinance.saas.grapserver.biz.service.thread;

import com.google.common.collect.Maps;
import com.treefinance.saas.assistant.model.Constants;
import com.treefinance.saas.grapserver.biz.cache.RedisDao;
import com.treefinance.saas.grapserver.biz.common.SpringUtils;
import com.treefinance.saas.grapserver.biz.config.DiamondConfig;
import com.treefinance.saas.grapserver.biz.service.TaskAliveService;
import com.treefinance.saas.grapserver.biz.service.TaskService;
import com.treefinance.saas.grapserver.common.utils.RedisKeyUtils;
import com.treefinance.saas.grapserver.dao.entity.Task;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;

/**
 * Good Luck Bro , No Bug !
 *
 * @author haojiahong
 * @date 2018/5/28
 */
public class TaskActiveTimeoutThread implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(TaskActiveTimeoutThread.class);

    private TaskAliveService taskAliveService;
    private TaskService taskService;
    private DiamondConfig diamondConfig;
    private RedisDao redisDao;
    private Task task;
    private Date startTime;

    public TaskActiveTimeoutThread(Task task, Date startTime) {
        this.taskAliveService = (TaskAliveService) SpringUtils.getBean("taskAliveService");
        this.taskService = (TaskService) SpringUtils.getBean("taskService");
        this.diamondConfig = (DiamondConfig) SpringUtils.getBean("diamondConfig");
        this.redisDao = (RedisDao) SpringUtils.getBean("redisDao");
        this.task = task;
        this.startTime = startTime;
    }

    @Override
    public void run() {
        //保证取消任务只会执行一次
        Map<String, Object> lockMap = Maps.newHashMap();
        String lockKey = RedisKeyUtils.genRedisLockKey("task-alive-time-job-task", Constants.SAAS_ENV_VALUE, String.valueOf(task.getId()));
        try {
            lockMap = redisDao.acquireLock(lockKey, 3 * 60 * 1000L);
            if (MapUtils.isEmpty(lockMap)) {
                return;
            }
            String valueStr = taskAliveService.getTaskAliveTime(task.getId());
            if (StringUtils.isBlank(valueStr)) {
                logger.info("任务已经被取消了taskId={}", task.getId());
                return;
            }

            Long lastActiveTime = Long.parseLong(valueStr);
            long diff = diamondConfig.getTaskMaxAliveTime();
            if (startTime.getTime() - lastActiveTime > diff) {
                logger.info("任务活跃时间超时,取消任务,taskId={}", task.getId());
                taskService.cancelTask(task.getId());
            }
            //删除记录任务活跃时间redisKey
            taskAliveService.deleteTaskAliveRedisKey(task.getId());
        } finally {
            redisDao.releaseLock(lockKey, lockMap, 3 * 60 * 1000L);
        }
    }
}
