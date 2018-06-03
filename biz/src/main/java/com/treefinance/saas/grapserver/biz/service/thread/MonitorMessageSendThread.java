package com.treefinance.saas.grapserver.biz.service.thread;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.grapserver.biz.common.SpringUtils;
import com.treefinance.saas.grapserver.biz.service.TaskService;
import com.treefinance.saas.grapserver.biz.service.monitor.EcommerceMonitorService;
import com.treefinance.saas.grapserver.biz.service.monitor.EmailMonitorService;
import com.treefinance.saas.grapserver.biz.service.monitor.MonitorPluginService;
import com.treefinance.saas.grapserver.biz.service.monitor.OperatorMonitorService;
import com.treefinance.saas.grapserver.common.enums.EBizType;
import com.treefinance.saas.grapserver.common.enums.ETaskStatus;
import com.treefinance.saas.grapserver.common.model.dto.TaskDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Good Luck Bro , No Bug !
 *
 * @author haojiahong
 * @date 2018/5/29
 */
public class MonitorMessageSendThread implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(MonitorMessageSendThread.class);

    private Long taskId;
    private TaskService taskService;
    private MonitorPluginService monitorPluginService;
    private OperatorMonitorService operatorMonitorService;
    private EcommerceMonitorService ecommerceMonitorService;
    private EmailMonitorService emailMonitorService;

    public MonitorMessageSendThread(Long taskId) {
        this.taskService = (TaskService) SpringUtils.getBean("taskService");
        this.monitorPluginService = (MonitorPluginService) SpringUtils.getBean("monitorPluginService");
        this.operatorMonitorService = (OperatorMonitorService) SpringUtils.getBean("operatorMonitorService");
        this.ecommerceMonitorService = (EcommerceMonitorService) SpringUtils.getBean("ecommerceMonitorService");
        this.emailMonitorService = (EmailMonitorService) SpringUtils.getBean("emailMonitorService");
        this.taskId = taskId;
    }


    @Override
    public void run() {
        TaskDTO taskDTO = taskService.getById(taskId);
        Byte status = taskDTO.getStatus();
        // 仅成功、失败、取消发送任务
        if (!ETaskStatus.SUCCESS.getStatus().equals(status)
                && !ETaskStatus.FAIL.getStatus().equals(status)
                && !ETaskStatus.CANCEL.getStatus().equals(status)) {
            return;
        }

        //发送任务监控消息
        monitorPluginService.sendTaskMonitorMessage(taskDTO);
        EBizType eBizType = EBizType.of(taskDTO.getBizType());
        switch (eBizType) {
            case OPERATOR:
                //发送运营商监控消息
                this.sendTaskOperatorMonitorMessage(taskDTO);
                break;
            case ECOMMERCE:
                // 发送电商监控消息
                this.sendEcommerceMonitorMessage(taskDTO);
                break;
            case EMAIL:
                this.sendEmailMonitorMessage(taskDTO);
                break;
            case EMAIL_H5:
                this.sendEmailMonitorMessage(taskDTO);
                break;
        }
    }

    /**
     * 发送运营商监控功能消息
     *
     * @param taskDTO
     */
    private void sendTaskOperatorMonitorMessage(TaskDTO taskDTO) {
        operatorMonitorService.sendMessage(taskDTO);
        logger.info("sendOperatorMonitorMessage: task={}", JSON.toJSONString(taskDTO));
    }


    /**
     * 发送电商监控消息
     *
     * @param taskDTO
     */
    private void sendEcommerceMonitorMessage(TaskDTO taskDTO) {
        ecommerceMonitorService.sendMessage(taskDTO);
        logger.info("sendEcommerceMonitorMessage: task={}", JSON.toJSONString(taskDTO));
    }

    /**
     * 发送邮箱监控消息
     *
     * @param taskDTO
     */
    private void sendEmailMonitorMessage(TaskDTO taskDTO) {
        emailMonitorService.sendMessage(taskDTO);
        logger.info("sendEcommerceMonitorMessage: task={}", JSON.toJSONString(taskDTO));
    }

}
