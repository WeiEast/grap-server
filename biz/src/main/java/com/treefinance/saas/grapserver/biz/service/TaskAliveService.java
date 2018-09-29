package com.treefinance.saas.grapserver.biz.service;

import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;
import com.treefinance.saas.taskcenter.facade.service.TaskAliveFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

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
    private TaskAliveFacade taskAliveFacade;

    /**
     * 更新任务最近活跃时间
     * 可能存在多个请求同时更新活跃时间,未获得锁的请求可过滤掉
     *
     * @param taskId 任务id
     */
    public void updateTaskActiveTime(Long taskId) {
        taskAliveFacade.updateTaskActiveTime(taskId);
    }

    /**
     * 更新任务最近活跃时间
     *
     * @param taskId 任务id
     * @param date
     */

    public void updateTaskActiveTime(Long taskId, Date date) {
        taskAliveFacade.updateTaskActiveTime(taskId, date);
    }

    /**
     * 获取任务最近活跃时间
     *
     * @param taskId
     * @return
     */
    public String getTaskAliveTime(Long taskId) {
        TaskResult<String> rpcResult = taskAliveFacade.getTaskAliveTime(taskId);
        return rpcResult.getData();
    }
}
