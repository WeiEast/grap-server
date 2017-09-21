package com.treefinance.saas.grapserver.biz.service.moxie.directive.process.impl;

import com.google.common.collect.Maps;
import com.treefinance.saas.grapserver.biz.service.moxie.directive.process.MoxieAbstractDirectiveProcessor;
import com.treefinance.saas.grapserver.common.enums.ETaskStep;
import com.treefinance.saas.grapserver.common.enums.moxie.EMoxieDirective;
import com.treefinance.saas.grapserver.common.model.dto.TaskDTO;
import com.treefinance.saas.grapserver.common.model.dto.moxie.MoxieDirectiveDTO;
import com.treefinance.saas.grapserver.common.utils.JsonUtils;
import com.treefinance.saas.grapserver.dao.entity.TaskAttribute;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;

/**
 * Created by haojiahong on 2017/9/15.
 */
@Component
public class MoxieLoginFailDirectiveProcessor extends MoxieAbstractDirectiveProcessor {

    @Override
    @Transactional
    protected void doProcess(EMoxieDirective directive, MoxieDirectiveDTO directiveDTO) {
        TaskDTO taskDTO = directiveDTO.getTask();
        TaskAttribute taskAttribute = taskAttributeService.findByName(taskDTO.getId(), "moxie-taskId", false);
        String moxieTaskId = "";
        if (taskAttribute != null) {
            moxieTaskId = taskAttribute.getValue();
        }
        Map<String, Object> map = Maps.newHashMap();
        map.put("error", directiveDTO.getRemark());
        map.put("moxieTaskId", moxieTaskId);

        // 1.记录登录日志
        taskLogService.insert(taskDTO.getId(), ETaskStep.LOGIN_FAIL.getText(), new Date(), JsonUtils.toJsonString(map));


    }
}
