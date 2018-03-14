package com.treefinance.saas.grapserver.biz.service.monitor;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.grapserver.biz.processor.OperatorMonitorSpecialProcessor;
import com.treefinance.saas.grapserver.biz.processor.request.OperatorMonitorSpecialRequest;
import com.treefinance.saas.grapserver.biz.service.TaskService;
import com.treefinance.saas.grapserver.common.enums.EBizType;
import com.treefinance.saas.grapserver.common.enums.ETaskStatus;
import com.treefinance.saas.grapserver.common.model.dto.TaskDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

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
    @Autowired
    private EcommerceMonitorService ecommerceMonitorService;
    @Autowired
    private EmailMonitorService emailMonitorService;

    /**
     * 发送监控消息
     *
     * @param taskDTO
     */
    public void sendMonitorMessage(TaskDTO taskDTO) {
        try {
            logger.info("TransactionSynchronizationManager: start task={}", JSON.toJSONString(taskDTO));
            // 事务完成之后，发送消息
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
//                @Override
//                public void afterCommit() {
//                    logger.info("TransactionSynchronizationManager: running task={}", JSON.toJSONString(taskDTO));
//                    doSendMonitorMessage(taskDTO);
//                }

                @Override
                public void afterCompletion(int status) {
                    String statusCode = "";
                    if (status == 0) {
                        statusCode = "STATUS_COMMITTED";
                    } else if (status == 1) {
                        statusCode = "STATUS_ROLLED_BACK";
                    } else if (status == 2) {
                        statusCode = "STATUS_UNKNOWN";
                    }
                    logger.info("TransactionSynchronizationManager: completion : status={},statusCode={}, task={}", status, statusCode, JSON.toJSONString(taskDTO));
                    doSendMonitorMessage(taskDTO);
                }
            });

        } catch (Exception e) {
            logger.error("sendMonitorMessage failed : task={},", JSON.toJSONString(taskDTO), e);
        }
    }


    @Async
    public void doSendMonitorMessage(TaskDTO taskDTO) {
        taskDTO = taskService.getById(taskDTO.getId());
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
        OperatorMonitorSpecialRequest request = new OperatorMonitorSpecialRequest();
        request.setTaskId(taskDTO.getId());
        request.setTask(taskDTO);
        operatorMonitorSpecialProcessor.doService(request);
        logger.info("sendTaskOperatorMonitorMessage: task={},request={}", JSON.toJSONString(taskDTO), JSON.toJSONString(request));
    }


    /**
     * 发送电商监控消息
     *
     * @param taskDTO
     */
    private void sendEcommerceMonitorMessage(TaskDTO taskDTO) {
        ecommerceMonitorService.sendMessage(taskDTO);
        logger.info("sendEcommerceMonitorMessage: task={},request={}", JSON.toJSONString(taskDTO), JSON.toJSONString(taskDTO));
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
