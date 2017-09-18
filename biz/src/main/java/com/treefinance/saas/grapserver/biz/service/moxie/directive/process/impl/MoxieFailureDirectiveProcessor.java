package com.treefinance.saas.grapserver.biz.service.moxie.directive.process.impl;

import com.treefinance.saas.grapserver.biz.common.AsycExcutor;
import com.treefinance.saas.grapserver.biz.service.MonitorService;
import com.treefinance.saas.grapserver.biz.service.moxie.directive.process.MoxieAbstractDirectiveProcessor;
import com.treefinance.saas.grapserver.common.enums.TaskStatusEnum;
import com.treefinance.saas.grapserver.common.enums.moxie.EMoxieDirective;
import com.treefinance.saas.grapserver.common.model.dto.TaskDTO;
import com.treefinance.saas.grapserver.common.model.dto.moxie.MoxieDirectiveDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class MoxieFailureDirectiveProcessor extends MoxieAbstractDirectiveProcessor {
    @Autowired
    protected MonitorService monitorService;
    @Autowired
    private AsycExcutor asycExcutor;

    @Override
    protected void doProcess(EMoxieDirective directive, MoxieDirectiveDTO directiveDTO) {
        TaskDTO taskDTO = directiveDTO.getTask();

        // 1.任务置为失败
        taskDTO.setStatus(TaskStatusEnum.FAIL.getStatus());
        // 2.更新任务状态,记录失败任务日志
        taskService.failTaskWithStep(taskDTO.getId());

    }
}
