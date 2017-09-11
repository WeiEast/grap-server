package com.treefinance.saas.grapserver.biz.handler;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.grapserver.biz.mq.model.TaskLogMessage;
import com.treefinance.saas.grapserver.biz.service.TaskLogService;
import com.treefinance.saas.grapserver.biz.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by luoyihua on 2017/4/26.
 */
@Service
public class TaskLogHandler {
    private static final Logger logger = LoggerFactory.getLogger(TaskLogHandler.class);

    @Autowired
    private TaskLogService taskLogService;
    @Autowired
    private TaskService taskService;

    public void handle(String message) {
        TaskLogMessage taskLogMessage = JSON.parseObject(message, TaskLogMessage.class);
        if (taskLogMessage != null) {
            Long taskId = taskLogMessage.getTaskId();
            if (taskService.isTaskCompleted(taskId)) {
                logger.info("任务已完成，不再更新日志：message={}", taskLogMessage);
                return;
            }
            Date processTime = new Date(taskLogMessage.getTimestamp());
            taskLogService.insert(taskId, taskLogMessage.getMsg(), processTime, taskLogMessage.getErrorDetail());

            logger.info("日志保存成功：message={}", taskLogMessage);
        }
    }
}
