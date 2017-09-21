package com.treefinance.saas.grapserver.biz.service.directive.process.impl;

import com.treefinance.saas.grapserver.common.enums.EDirective;
import com.treefinance.saas.grapserver.biz.service.MonitorService;
import com.treefinance.saas.grapserver.biz.service.directive.process.CallbackableDirectiveProcessor;
import com.treefinance.saas.grapserver.dao.entity.AppLicense;
import com.treefinance.saas.grapserver.common.enums.ETaskStatus;
import com.treefinance.saas.grapserver.common.model.dto.DirectiveDTO;
import com.treefinance.saas.grapserver.common.model.dto.TaskDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

/**
 * 成功指令处理
 * Created by yh-treefinance on 2017/7/6.
 */
@Component
public class SuccessDirectiveProcessor extends CallbackableDirectiveProcessor {
    @Autowired
    protected MonitorService monitorService;

    @Override
    protected void doProcess(EDirective directive, DirectiveDTO directiveDTO) {
        TaskDTO taskDTO = directiveDTO.getTask();
        Long taskId = taskDTO.getId();
        String appId = taskDTO.getAppId();
        // 1.记录任务日志
        taskLogService.insert(taskId, "爬数任务执行完成", new Date(), null);

        // 2.获取商户密钥
        AppLicense appLicense = appLicenseService.getAppLicense(appId);
        // 3.生成数据map
        Map<String, Object> dataMap = generateDataMap(directiveDTO);
        // 4.回调之前预处理
        precallback(dataMap, appLicense, directiveDTO);

        // 5.触发回调: 0-无需回调，1-回调成功，-1-回调失败
        int result = callback(dataMap, appLicense, directiveDTO);
        if (result == 0) {
            taskDTO.setStatus(ETaskStatus.SUCCESS.getStatus());
        } else if (result == 1) {
            taskDTO.setStatus(ETaskStatus.SUCCESS.getStatus());
        } else {
            // 指令发生变更 ： task_success -> callback_fail
            taskNextDirectiveService.insert(taskId, directiveDTO.getDirective());

            taskDTO.setStatus(ETaskStatus.FAIL.getStatus());
            directiveDTO.setDirective(EDirective.CALLBACK_FAIL.getText());
        }
        // 6.更新任务状态
        String stepCode = taskService.updateTaskStatusWithStep(taskId, taskDTO.getStatus());
        taskDTO.setStepCode(stepCode);
        // 7.发送监控消息
        monitorService.sendMonitorMessage(taskDTO);
    }

}
