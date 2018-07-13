package com.treefinance.saas.grapserver.biz.service;

import com.treefinance.saas.grapserver.biz.cache.RedisDao;
import com.treefinance.saas.grapserver.common.enums.ETaskStatus;
import com.treefinance.saas.grapserver.common.utils.GrapDateUtils;
import com.treefinance.saas.grapserver.common.utils.RedisKeyUtils;
import com.treefinance.saas.grapserver.dao.entity.Task;
import com.treefinance.saas.grapserver.dao.entity.TaskAttribute;
import com.treefinance.saas.grapserver.dao.mapper.TaskMapper;
import com.treefinance.saas.grapserver.facade.enums.ETaskAttribute;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
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
    private RedisDao redisDao;
    @Autowired
    private TaskAttributeService taskAttributeService;

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
        Date date = new Date();
        taskAttributeService.insertOrUpdateSelective(taskId, ETaskAttribute.ALIVE_TIME.getAttribute(), GrapDateUtils.getDateStrByDate(date));

        String key = RedisKeyUtils.genTaskActiveTimeKey(taskId);
        String value = date.getTime() + "";
        redisDao.setEx(key, value, 30, TimeUnit.MINUTES);

    }

    /**
     * 更新任务最近活跃时间
     *
     * @param taskId 任务id
     * @param date
     */
    public void updateTaskActiveTime(Long taskId, Date date) {
        Task task = taskMapper.selectByPrimaryKey(taskId);
        if (task == null) {
            return;
        }
        if (!ETaskStatus.RUNNING.getStatus().equals(task.getStatus())) {
            logger.info("任务已结束,无需更新任务活跃时间,taskId={}", taskId);
            return;
        }
        String key = RedisKeyUtils.genTaskActiveTimeKey(taskId);
        String value = date.getTime() + "";
        redisDao.setEx(key, value, 30, TimeUnit.MINUTES);

        taskAttributeService.insertOrUpdateSelective(taskId, ETaskAttribute.ALIVE_TIME.getAttribute(), GrapDateUtils.getDateStrByDate(date));


    }

    /**
     * 获取任务最近活跃时间
     *
     * @param taskId
     * @return
     */
    public String getTaskAliveTime(Long taskId) {
        String key = RedisKeyUtils.genTaskActiveTimeKey(taskId);
        String lastActiveTimeStr = redisDao.get(key);
        if (StringUtils.isNotBlank(lastActiveTimeStr)) {
            return lastActiveTimeStr;
        } else {
            TaskAttribute taskAttribute = taskAttributeService.findByName(taskId, ETaskAttribute.ALIVE_TIME.getAttribute(), false);
            if (taskAttribute == null) {
                return null;
            }
            String dateStr = taskAttribute.getValue();
            Date date = GrapDateUtils.getDateByStr(dateStr);
            this.updateTaskActiveTime(taskId, date);
            return date.getTime() + "";
        }
    }

    /**
     * 删除记录任务活跃时间redisKey
     * 注意:任务成功或者失败时不能使用此方法,因为前端轮询指令接口有可能还未获取到成功失败指令
     *
     * @param taskId
     */
    public void deleteTaskAliveTime(Long taskId) {
        String key = RedisKeyUtils.genTaskActiveTimeKey(taskId);
        redisDao.deleteKey(key);
        taskAttributeService.deleteByTaskIdAndName(taskId, ETaskAttribute.ALIVE_TIME.getAttribute());
        logger.info("删除记录的任务活跃时间, taskId={}", taskId);
    }

}
