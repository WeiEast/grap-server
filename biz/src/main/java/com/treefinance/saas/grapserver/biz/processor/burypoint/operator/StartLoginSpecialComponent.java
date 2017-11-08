package com.treefinance.saas.grapserver.biz.processor.burypoint.operator;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.assistant.model.TaskOperatorMonitorMessage;
import com.treefinance.saas.grapserver.biz.processor.BaseBusinessComponent;
import com.treefinance.saas.grapserver.biz.service.MonitorService;
import com.treefinance.saas.grapserver.dao.entity.TaskAttribute;
import com.treefinance.saas.grapserver.dao.entity.TaskAttributeCriteria;
import com.treefinance.saas.grapserver.dao.mapper.TaskAttributeMapper;
import com.treefinance.saas.grapserver.facade.enums.ETaskAttribute;
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
 * 开始登陆人数监控
 *
 * @author haojiahong
 * @date 2017/11/2
 */
@Component("startLoginSpecialComponent")
public class StartLoginSpecialComponent extends BaseBusinessComponent<OperatorBuryPointSpecialRequest> {
    private final static Logger logger = LoggerFactory.getLogger(StartLoginSpecialComponent.class);

    @Autowired
    private MonitorService monitorService;
    @Autowired
    private TaskAttributeMapper taskAttributeMapper;

    @Override
    protected void doBusiness(OperatorBuryPointSpecialRequest request) {
        if (request == null || StringUtils.isBlank(request.getCode())) {
            return;
        }

        if (!StringUtils.equalsIgnoreCase(request.getCode(), "300701")) {
            return;
        }
        TaskAttributeCriteria taskAttributeCriteria = new TaskAttributeCriteria();
        taskAttributeCriteria.createCriteria().andTaskIdEqualTo(request.getTaskId());
        List<TaskAttribute> taskAttributeList = taskAttributeMapper.selectByExample(taskAttributeCriteria);
        if (CollectionUtils.isEmpty(taskAttributeList)) {
            logger.error("运营商监控,task_attribute表中未查到group_code和group_name信息,taskId={}", request.getTaskId());
            return;
        }
        String groupCode = null;
        String groupName = null;
        for (TaskAttribute taskAttribute : taskAttributeList) {
            if (StringUtils.equalsIgnoreCase(taskAttribute.getName(), ETaskAttribute.OPERATOR_GROUP_CODE.getAttribute())) {
                groupCode = taskAttribute.getValue();
            }
            if (StringUtils.equalsIgnoreCase(taskAttribute.getName(), ETaskAttribute.OPERATOR_GROUP_NAME.getAttribute())) {
                groupName = taskAttribute.getValue();
            }
        }
        if (StringUtils.isBlank(groupCode) || StringUtils.isBlank(groupName)) {
            logger.error("运营商监控,未取到groupCode和groupName字段信息,taskId={}", request.getTaskId());
            return;
        }
        TaskOperatorMonitorMessage message = new TaskOperatorMonitorMessage();
        message.setTaskId(request.getTaskId());
        message.setAppId(request.getAppId());
        message.setGroupCode(groupCode);
        message.setGroupName(groupName);
        message.setDataTime(new Date());
        message.setStatus(ETaskOperatorMonitorStatus.START_LOGIN.getStatus());
        logger.info("运营商监控,发送确认手机号(埋点)消息到monitor,message={},status={}",
                JSON.toJSONString(message), ETaskOperatorMonitorStatus.START_LOGIN);
        monitorService.sendTaskOperatorMonitorMessage(message);

    }
}
