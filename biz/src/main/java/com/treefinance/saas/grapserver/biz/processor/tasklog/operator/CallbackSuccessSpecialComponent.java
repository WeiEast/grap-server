package com.treefinance.saas.grapserver.biz.processor.tasklog.operator;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.assistant.model.TaskOperatorMonitorMessage;
import com.treefinance.saas.grapserver.biz.processor.BaseBusinessComponent;
import com.treefinance.saas.grapserver.biz.service.MonitorService;
import com.treefinance.saas.grapserver.common.enums.ETaskStep;
import com.treefinance.saas.grapserver.common.model.dto.TaskDTO;
import com.treefinance.saas.grapserver.dao.entity.TaskLog;
import com.treefinance.saas.grapserver.dao.entity.TaskLogCriteria;
import com.treefinance.saas.grapserver.dao.mapper.TaskLogMapper;
import com.treefinance.saas.grapserver.facade.model.enums.ETaskOperatorMonitorStatus;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * Created by haojiahong on 2017/11/2.
 */
@Component("callbackSuccessSpecialComponent")
public class CallbackSuccessSpecialComponent extends BaseBusinessComponent<OperatorTaskLogSpecialRequest> {
    private final static Logger logger = LoggerFactory.getLogger(CallbackSuccessSpecialComponent.class);

    @Autowired
    private MonitorService monitorService;
    @Autowired
    private TaskLogMapper taskLogMapper;

    @Override
    protected void doBusiness(OperatorTaskLogSpecialRequest request) {
        if (request == null || request.getTaskDTO() == null || StringUtils.isBlank(request.getMsg())) {
            return;
        }
        //收到的msg不是回调成功,不处理.
        if (!StringUtils.equalsIgnoreCase(request.getMsg(), ETaskStep.CALLBACK_SUCCESS.getText())) {
            return;
        }
        String msg = request.getMsg();
        TaskDTO taskDTO = request.getTaskDTO();
        Date processTime = request.getProcessTime();
        TaskLogCriteria taskLogCriteria = new TaskLogCriteria();
        taskLogCriteria.createCriteria().andTaskIdEqualTo(taskDTO.getId()).andMsgEqualTo(msg);
        List<TaskLog> taskLogList = taskLogMapper.selectByExample(taskLogCriteria);
        if (taskLogList.size() >= 1) {
            logger.info("运营商监控,回调日志重复不处理,taskDTO={},msg={},processTime={}",
                    JSON.toJSONString(taskDTO), msg, processTime);
            return;
        }
        TaskOperatorMonitorMessage message = new TaskOperatorMonitorMessage();
        message.setTaskId(taskDTO.getId());
        message.setAppId(taskDTO.getAppId());
        message.setDataTime(processTime);
        message.setStatus(ETaskOperatorMonitorStatus.CALLBACK_SUCCESS.getStatus());
        logger.info("运营商监控,发送回调成功(任务日志)消息到monitor,message={},status={}",
                JSON.toJSONString(message), ETaskOperatorMonitorStatus.CALLBACK_SUCCESS);
        monitorService.sendTaskOperatorMonitorMessage(message);

    }
}
