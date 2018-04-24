package com.treefinance.saas.grapserver.biz.service;

import com.treefinance.saas.grapserver.common.enums.ETaskStatus;
import com.treefinance.saas.grapserver.common.utils.RedisKeyUtils;
import com.treefinance.saas.grapserver.dao.entity.Task;
import com.treefinance.saas.grapserver.dao.mapper.TaskMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Buddha Bless , No Bug !
 * 任务活跃服务类
 *
 * @author haojiahong
 * @date 2018/4/24
 */
@Service
public class TaskAliveService {
    private static final Logger logger = LoggerFactory.getLogger(TaskAliveService.class);

    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 更新任务最近活跃时间
     *
     * @param taskId 任务id
     */
    public void updateTaskActiveTime(Long taskId) {
        Task task = taskMapper.selectByPrimaryKey(taskId);
        if (task == null) {
            return;
        }
        if (!ETaskStatus.RUNNING.getStatus().equals(task.getStatus())) {
            logger.info("任务已结束,无需更新任务活跃时间,taskId={}", taskId);
            return;
        }
        String key = RedisKeyUtils.genTaskActiveTimeKey(taskId);
        String value = System.currentTimeMillis() + "";
        redisTemplate.opsForValue().set(key, value);
        if (redisTemplate.getExpire(key) == -1) {
            redisTemplate.expire(key, 30, TimeUnit.MINUTES);
        }
    }

    /**
     * 删除记录任务活跃时间redisKey
     *
     * @param taskId
     */
    public void deleteTaskAliveRedisKey(Long taskId) {
        String key = RedisKeyUtils.genTaskActiveTimeKey(taskId);
        redisTemplate.delete(key);
        logger.info("删除记录任务活跃时间redisKey, taskId={}", taskId);
    }

}