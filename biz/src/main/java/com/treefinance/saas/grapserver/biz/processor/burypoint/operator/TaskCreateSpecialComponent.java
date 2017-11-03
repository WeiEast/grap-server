package com.treefinance.saas.grapserver.biz.processor.burypoint.operator;

import com.treefinance.saas.assistant.model.TaskOperatorMonitorMessage;
import com.treefinance.saas.grapserver.biz.processor.BaseBusinessComponent;
import com.treefinance.saas.grapserver.biz.service.MonitorService;
import com.treefinance.saas.grapserver.facade.model.enums.ETaskOperatorMonitorStatus;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 运营商任务创建人数监控
 *
 * @author haojiahong
 * @date 2017/11/2
 */
@Component("taskCreateSpecialComponent")
public class TaskCreateSpecialComponent extends BaseBusinessComponent<OperatorBuryPointSpecialRequest> {

    private final static Logger logger = LoggerFactory.getLogger(TaskCreateSpecialComponent.class);

    @Autowired
    private MonitorService monitorService;

    @Override
    protected void doBusiness(OperatorBuryPointSpecialRequest request) {
        if (request == null || StringUtils.isBlank(request.getCode())) {
            return;
        }

        if (!StringUtils.equalsIgnoreCase(request.getCode(), "300001")) {
            return;
        }
        TaskOperatorMonitorMessage message = new TaskOperatorMonitorMessage();
        message.setTaskId(request.getTaskId());
        message.setAppId(request.getAppId());
        message.setDataTime(new Date());
        message.setStatus(ETaskOperatorMonitorStatus.CREATE_TASK.getStatus());

        monitorService.sendTaskOperatorMonitorMessage(message);

    }
}
