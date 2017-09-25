package com.treefinance.saas.grapserver.biz.service.directive.process.impl;

import com.treefinance.saas.grapserver.biz.common.AsycExcutor;
import com.treefinance.saas.grapserver.biz.service.MonitorService;
import com.treefinance.saas.grapserver.biz.service.directive.process.AbstractDirectiveProcessor;
import com.treefinance.saas.grapserver.common.enums.EDirective;
import com.treefinance.saas.grapserver.common.enums.ETaskStatus;
import com.treefinance.saas.grapserver.common.model.dto.DirectiveDTO;
import com.treefinance.saas.grapserver.common.model.dto.TaskDTO;
import com.treefinance.saas.grapserver.dao.entity.AppLicense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 任务失败回调
 * Created by yh-treefinance on 2017/7/10.
 */
@Component
public class FailureDirectiveProcessor extends AbstractDirectiveProcessor {
    @Autowired
    protected MonitorService monitorService;
    @Autowired
    private AsycExcutor asycExcutor;

    @Override
    protected void doProcess(EDirective directive, DirectiveDTO directiveDTO) {
        TaskDTO taskDTO = directiveDTO.getTask();
        String appId = taskDTO.getAppId();

        // 1.任务置为失败
        taskDTO.setStatus(ETaskStatus.FAIL.getStatus());
        // 2.更新任务状态
        String errorCode = taskService.failTaskWithStep(taskDTO.getId());
        taskDTO.setStepCode(errorCode);
        // 3.发送监控消息
        monitorService.sendMonitorMessage(taskDTO);

        // 4.获取商户密钥
        AppLicense appLicense = appLicenseService.getAppLicense(appId);
        // 5.成数据map
        Map<String, Object> dataMap = generateDataMap(directiveDTO);
        // 6.回调之前预处理
        precallback(dataMap, appLicense, directiveDTO);

        // 7.异步触发触发回调
        asycExcutor.runAsyc(directiveDTO, _directiveDTO -> {
            callback(dataMap, appLicense, _directiveDTO);
        });
    }
}
