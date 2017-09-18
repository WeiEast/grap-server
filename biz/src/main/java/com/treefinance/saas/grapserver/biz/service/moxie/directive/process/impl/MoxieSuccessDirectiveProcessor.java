package com.treefinance.saas.grapserver.biz.service.moxie.directive.process.impl;

import com.treefinance.saas.grapserver.biz.service.moxie.directive.process.MoxieAbstractDirectiveProcessor;
import com.treefinance.saas.grapserver.common.enums.TaskStatusEnum;
import com.treefinance.saas.grapserver.common.enums.moxie.EMoxieDirective;
import com.treefinance.saas.grapserver.common.model.dto.TaskDTO;
import com.treefinance.saas.grapserver.common.model.dto.moxie.MoxieDirectiveDTO;
import org.springframework.stereotype.Component;

/**
 * 成功指令处理
 * Created by yh-treefinance on 2017/7/6.
 */
@Component
public class MoxieSuccessDirectiveProcessor extends MoxieAbstractDirectiveProcessor {

    @Override
    protected void doProcess(EMoxieDirective directive, MoxieDirectiveDTO directiveDTO) {
        TaskDTO taskDTO = directiveDTO.getTask();
        Long taskId = taskDTO.getId();
        // 1.更新任务状态,记录任务成功日志
        taskService.updateTaskStatusWithStep(taskId, TaskStatusEnum.SUCCESS.getStatus());
    }

}
