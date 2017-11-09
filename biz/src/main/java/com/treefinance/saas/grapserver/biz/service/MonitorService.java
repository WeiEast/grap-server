package com.treefinance.saas.grapserver.biz.service;

import com.treefinance.saas.grapserver.biz.processor.OperatorMonitorSpecialProcessor;
import com.treefinance.saas.grapserver.biz.processor.request.OperatorMonitorSpecialRequest;
import com.treefinance.saas.grapserver.common.enums.EBizType;
import com.treefinance.saas.grapserver.common.enums.ETaskStatus;
import com.treefinance.saas.grapserver.common.model.dto.TaskDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Created by yh-treefinance on 2017/6/20.
 */
@Service
public class MonitorService {
    private static final Logger logger = LoggerFactory.getLogger(MonitorService.class);

    @Autowired
    private MonitorPluginService monitorPluginService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private OperatorMonitorSpecialProcessor operatorMonitorSpecialProcessor;

    /**
     * 发送监控消息
     *
     * @param taskDTO
     */
    @Async
    public void sendMonitorMessage(TaskDTO taskDTO) {
        Byte status = taskDTO.getStatus();
        // 仅成功、失败、取消发送任务
        if (!ETaskStatus.SUCCESS.getStatus().equals(status)
                && !ETaskStatus.FAIL.getStatus().equals(status)
                && !ETaskStatus.CANCEL.getStatus().equals(status)) {
            return;
        }
        //发送任务监控消息
        monitorPluginService.sendTaskMonitorMessage(taskDTO);
        //发送运营商监控消息
        this.sendTaskOperatorMonitorMessage(taskDTO);

    }


    /**
     * 发送运营商监控功能消息
     *
     * @param taskDTO
     */
    private void sendTaskOperatorMonitorMessage(TaskDTO taskDTO) {
        if (taskDTO == null || taskDTO.getId() == null) {
            return;
        }
        taskDTO = taskService.getById(taskDTO.getId());
        if (taskDTO != null && EBizType.OPERATOR.getCode().equals(taskDTO.getBizType())) {
            OperatorMonitorSpecialRequest request = new OperatorMonitorSpecialRequest();
            request.setTaskId(taskDTO.getId());
            request.setTask(taskDTO);
            operatorMonitorSpecialProcessor.doService(request);
        }


    }


}
