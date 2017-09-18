package com.treefinance.saas.grapserver.biz.service.moxie.directive.process.impl;

import com.treefinance.saas.grapserver.biz.service.moxie.directive.process.MoxieAbstractDirectiveProcessor;
import com.treefinance.saas.grapserver.common.enums.TaskStatusEnum;
import com.treefinance.saas.grapserver.common.enums.moxie.EMoxieDirective;
import com.treefinance.saas.grapserver.common.model.dto.TaskDTO;
import com.treefinance.saas.grapserver.common.model.dto.moxie.MoxieDirectiveDTO;
import org.springframework.stereotype.Component;

/**
 * 取消任务执行
 * Created by yh-treefinance on 2017/7/10.
 */
@Component
public class MoxieCancelDirectiveProcessor extends MoxieAbstractDirectiveProcessor {

    @Override
    protected void doProcess(EMoxieDirective directive, MoxieDirectiveDTO directiveDTO) {
        TaskDTO taskDTO = directiveDTO.getTask();
        taskDTO.setStatus(TaskStatusEnum.CANCLE.getStatus());
        // 取消任务
        taskService.cancelTaskWithStep(taskDTO.getId());

    }

}
