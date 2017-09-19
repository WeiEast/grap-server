package com.treefinance.saas.grapserver.biz.service.moxie.directive.process.impl;

import com.treefinance.saas.grapserver.biz.service.moxie.directive.process.MoxieAbstractDirectiveProcessor;
import com.treefinance.saas.grapserver.common.enums.TaskStepEnum;
import com.treefinance.saas.grapserver.common.enums.moxie.EMoxieDirective;
import com.treefinance.saas.grapserver.common.model.dto.TaskDTO;
import com.treefinance.saas.grapserver.common.model.dto.moxie.MoxieDirectiveDTO;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by haojiahong on 2017/9/15.
 */
@Component
public class MoxieLoginFailDirectiveProcessor extends MoxieAbstractDirectiveProcessor {

    @Override
    @Transactional
    protected void doProcess(EMoxieDirective directive, MoxieDirectiveDTO directiveDTO) {
        TaskDTO taskDTO = directiveDTO.getTask();
        // 1.记录登录日志
        taskLogService.insert(taskDTO.getId(), TaskStepEnum.LOGIN_FAIL.getText(), new Date(), directiveDTO.getRemark());


    }
}
