package com.treefinance.saas.grapserver.biz.service.moxie.directive.process.impl;

import com.treefinance.saas.grapserver.biz.common.AsycExcutor;
import com.treefinance.saas.grapserver.biz.service.monitor.MonitorService;
import com.treefinance.saas.grapserver.biz.service.moxie.directive.process.MoxieAbstractDirectiveProcessor;
import com.treefinance.saas.grapserver.common.enums.ETaskStatus;
import com.treefinance.saas.grapserver.common.enums.moxie.EMoxieDirective;
import com.treefinance.saas.grapserver.common.model.dto.TaskDTO;
import com.treefinance.saas.grapserver.common.model.dto.moxie.MoxieDirectiveDTO;
import com.treefinance.saas.grapserver.dao.entity.AppLicense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 *
 */
@Component
public class MoxieFailureDirectiveProcessor extends MoxieAbstractDirectiveProcessor {
    @Autowired
    private AsycExcutor asycExcutor;
    @Autowired
    private MonitorService monitorService;

    @Override
    protected void doProcess(EMoxieDirective directive, MoxieDirectiveDTO directiveDTO) {
        TaskDTO taskDTO = directiveDTO.getTask();
        String appId = taskDTO.getAppId();

        // 任务置为失败
        taskDTO.setStatus(ETaskStatus.FAIL.getStatus());

        // 更新任务状态,记录失败任务日志
        String stepCode = taskService.failTaskWithStep(taskDTO.getId());
        taskDTO.setStepCode(stepCode);

        //发送监控消息
        monitorService.sendMonitorMessage(taskDTO.getId());

        //获取商户秘钥,包装数据:任务失败后返回失败信息加密后通过指令传递给前端
        AppLicense appLicense = appLicenseService.getAppLicense(appId);
        //成数据map
        Map<String, Object> dataMap = generateDataMap(directiveDTO);
        //回调之前预处理
        precallback(dataMap, appLicense, directiveDTO);
        //异步触发触发回调
        asycExcutor.runAsyc(directiveDTO, _directiveDTO -> {
            callback(dataMap, appLicense, _directiveDTO);
        });


    }
}
