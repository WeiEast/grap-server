package com.treefinance.saas.grapserver.biz.service.thread;

import com.treefinance.saas.grapserver.biz.common.SpringUtils;
import com.treefinance.saas.grapserver.biz.config.DiamondConfig;
import com.treefinance.saas.grapserver.biz.service.TaskAliveService;
import com.treefinance.saas.grapserver.biz.service.TaskService;
import com.treefinance.saas.grapserver.dao.entity.Task;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

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
    private Task task;
    private Date startTime;

    public TaskActiveTimeoutThread(Task task, Date startTime) {
        this.taskAliveService = (TaskAliveService) SpringUtils.getBean("taskAliveService");
        this.taskService = (TaskService) SpringUtils.getBean("taskService");
        this.diamondConfig = (DiamondConfig) SpringUtils.getBean("diamondConfig");
        this.task = task;
        this.startTime = startTime;
    }

    @Override
    public void run() {
        String valueStr = taskAliveService.getTaskAliveTime(task.getId());
        if (StringUtils.isNotBlank(valueStr)) {
            Long lastActiveTime = Long.parseLong(valueStr);
            long diff = diamondConfig.getTaskMaxAliveTime();
            if (startTime.getTime() - lastActiveTime > diff) {
                logger.info("任务活跃时间超时,取消任务,taskId={}", task.getId());
                taskService.cancelTask(task.getId());
            }
        }
    }
}
