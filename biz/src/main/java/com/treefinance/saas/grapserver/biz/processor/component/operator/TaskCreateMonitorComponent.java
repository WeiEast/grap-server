package com.treefinance.saas.grapserver.biz.processor.component.operator;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.assistant.model.TaskOperatorMonitorMessage;
import com.treefinance.saas.grapserver.biz.processor.BaseBusinessComponent;
import com.treefinance.saas.grapserver.biz.processor.request.OperatorMonitorSpecialRequest;
import com.treefinance.saas.grapserver.biz.service.MonitorService;
import com.treefinance.saas.grapserver.biz.service.TaskBuryPointLogService;
import com.treefinance.saas.grapserver.biz.service.TaskLogService;
import com.treefinance.saas.grapserver.common.enums.ETaskStep;
import com.treefinance.saas.grapserver.dao.entity.TaskLog;
import com.treefinance.saas.grapserver.facade.model.enums.ETaskOperatorMonitorStatus;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by haojiahong on 2017/11/9.
 */
@Component("taskCreateMonitorComponent")
public class TaskCreateMonitorComponent extends BaseBusinessComponent<OperatorMonitorSpecialRequest> {
    private final static Logger logger = LoggerFactory.getLogger(TaskCreateMonitorComponent.class);

    @Autowired
    private TaskBuryPointLogService taskBuryPointLogService;
    @Autowired
    private TaskLogService taskLogService;
    @Autowired
    private MonitorService monitorService;

    @Override
    protected void doBusiness(OperatorMonitorSpecialRequest request) {
        if (request == null || request.getTask() == null) {
            logger.error("运营商监控,发送任务创建(任务日志)消息到monitor,请求参数为空,request={}", JSON.toJSONString(request));
            return;
        }
        List<TaskLog> list = taskLogService.queryTaskLog(request.getTaskId(), ETaskStep.TASK_CREATE.getText());
        if (CollectionUtils.isEmpty(list)) {
            logger.info("运营商监控,发送任务创建(任务日志)消息到monitor,未查询到任务创建日志信息,request={}", JSON.toJSONString(request));
            return;
        }
        TaskOperatorMonitorMessage message = new TaskOperatorMonitorMessage();
        message.setTaskId(request.getTaskId());
        message.setAppId(request.getTask().getAppId());
        message.setDataTime(request.getTask().getCreateTime());
        message.setStatus(ETaskOperatorMonitorStatus.CREATE_TASK.getStatus());
        logger.info("运营商监控,发送任务创建(任务日志)消息到monitor,request={},message={},status={}",
                JSON.toJSON(request), JSON.toJSONString(message), ETaskOperatorMonitorStatus.CREATE_TASK);
        monitorService.sendTaskOperatorMonitorMessage(message);

    }
}
