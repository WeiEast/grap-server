package com.treefinance.saas.grapserver.biz.processor.tasklog.operator;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.assistant.model.TaskOperatorMonitorMessage;
import com.treefinance.saas.grapserver.biz.processor.BaseBusinessComponent;
import com.treefinance.saas.grapserver.biz.service.MonitorService;
import com.treefinance.saas.grapserver.common.enums.ETaskStep;
import com.treefinance.saas.grapserver.common.model.dto.TaskDTO;
import com.treefinance.saas.grapserver.dao.entity.TaskAttribute;
import com.treefinance.saas.grapserver.dao.entity.TaskAttributeCriteria;
import com.treefinance.saas.grapserver.dao.entity.TaskLog;
import com.treefinance.saas.grapserver.dao.entity.TaskLogCriteria;
import com.treefinance.saas.grapserver.dao.mapper.TaskAttributeMapper;
import com.treefinance.saas.grapserver.dao.mapper.TaskLogMapper;
import com.treefinance.saas.grapserver.facade.model.enums.ETaskOperatorMonitorStatus;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by haojiahong on 2017/11/2.
 */
@Component("loginSuccessSpecialComponent")
public class LoginSuccessSpecialComponent extends BaseBusinessComponent<OperatorTaskLogSpecialRequest> {
    private final static Logger logger = LoggerFactory.getLogger(LoginSuccessSpecialComponent.class);

    @Autowired
    private MonitorService monitorService;
    @Autowired
    private TaskLogMapper taskLogMapper;
    @Autowired
    private TaskAttributeMapper taskAttributeMapper;

    @Override
    protected void doBusiness(OperatorTaskLogSpecialRequest request) {
        if (request == null || request.getTaskDTO() == null || StringUtils.isBlank(request.getMsg())) {
            return;
        }
        //收到的msg不是登录成功,不处理.
        if (!StringUtils.equalsIgnoreCase(request.getMsg(), ETaskStep.LOGIN_SUCCESS.getText())) {
            return;
        }
        String msg = request.getMsg();
        TaskDTO taskDTO = request.getTaskDTO();
        Date processTime = request.getProcessTime();
        TaskLogCriteria taskLogCriteria = new TaskLogCriteria();
        taskLogCriteria.createCriteria().andTaskIdEqualTo(taskDTO.getId()).andMsgEqualTo(msg);
        List<TaskLog> taskLogList = taskLogMapper.selectByExample(taskLogCriteria);
        if (CollectionUtils.isEmpty(taskLogList)) {
            logger.error("运营商监控,收到消息,未查询到相关任务日志,taskDTO={},msg={},processTime={}",
                    JSON.toJSONString(taskDTO), msg, processTime);
            return;
        }
        if (taskLogList.size() > 1) {
            logger.info("运营商监控,收到taskLog重复消息,重复消息不处理,taskDTO={},msg={},processTime={}",
                    JSON.toJSONString(taskDTO), msg, processTime);
            return;
        }
        TaskAttributeCriteria taskAttributeCriteria = new TaskAttributeCriteria();
        taskAttributeCriteria.createCriteria().andTaskIdEqualTo(taskDTO.getId());
        List<TaskAttribute> taskAttributeList = taskAttributeMapper.selectByExample(taskAttributeCriteria);
        if (CollectionUtils.isEmpty(taskAttributeList)) {
            logger.error("运营商监控,task_attribute表中未查到group_code和group_name信息,taskDTO={},msg={},processTime={}",
                    JSON.toJSONString(taskDTO), msg, processTime);
            return;
        }
        String groupCode = null;
        String groupName = null;
        for (TaskAttribute taskAttribute : taskAttributeList) {
            if (StringUtils.equalsIgnoreCase(taskAttribute.getName(), "group_code")) {
                groupCode = taskAttribute.getValue();
            }
            if (StringUtils.equalsIgnoreCase(taskAttribute.getName(), "group_name")) {
                groupName = taskAttribute.getName();
            }
        }
        if (StringUtils.isBlank(groupCode) || StringUtils.isBlank(groupName)) {
            logger.error("运营商监控,未取到groupCode和groupName字段信息,taskDTO={},msg={},processTime={}",
                    JSON.toJSONString(taskDTO), msg, processTime);
            return;
        }
        TaskOperatorMonitorMessage message = new TaskOperatorMonitorMessage();
        message.setTaskId(taskDTO.getId());
        message.setAppId(taskDTO.getAppId());
        message.setGroupCode(groupCode);
        message.setGroupName(groupName);
        message.setDataTime(processTime);
        message.setStatus(ETaskOperatorMonitorStatus.LOGIN_SUCCESS.getStatus());

        monitorService.sendTaskOperatorMonitorMessage(message);

    }
}
