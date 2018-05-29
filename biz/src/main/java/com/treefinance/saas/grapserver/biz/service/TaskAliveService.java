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
@Service("taskAliveService")
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
        redisTemplate.expire(key, 30, TimeUnit.MINUTES);
    }

    /**
     * 更新任务最近活跃时间
     *
     * @param taskId 任务id
     * @param time   需要设置的活跃时间
     */
    public void updateTaskActiveTime(Long taskId, Long time) {
        Task task = taskMapper.selectByPrimaryKey(taskId);
        if (task == null) {
            return;
        }
        if (!ETaskStatus.RUNNING.getStatus().equals(task.getStatus())) {
            logger.info("任务已结束,无需更新任务活跃时间,taskId={}", taskId);
            return;
        }
        String key = RedisKeyUtils.genTaskActiveTimeKey(taskId);
        String value = time + "";
        redisTemplate.opsForValue().set(key, value);
        redisTemplate.expire(key, 30, TimeUnit.MINUTES);
    }

    /**
     * 获取任务最近活跃时间
     *
     * @param taskId
     * @return
     */
    public String getTaskAliveTime(Long taskId) {
        String key = RedisKeyUtils.genTaskActiveTimeKey(taskId);
        String lastActiveTimeStr = redisTemplate.opsForValue().get(key);
        return lastActiveTimeStr;
    }

    /**
     * 删除记录任务活跃时间redisKey
     * 注意:任务成功或者失败时不能使用此方法,因为前端轮询指令接口有可能还未获取到成功失败指令
     *
     * @param taskId
     */
    public void deleteTaskAliveRedisKey(Long taskId) {
        String key = RedisKeyUtils.genTaskActiveTimeKey(taskId);
        redisTemplate.delete(key);
        logger.info("删除记录任务活跃时间redisKey, taskId={}", taskId);
    }

}
