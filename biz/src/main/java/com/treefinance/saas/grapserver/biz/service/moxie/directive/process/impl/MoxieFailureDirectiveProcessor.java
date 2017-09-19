package com.treefinance.saas.grapserver.biz.service.moxie.directive.process.impl;

import com.treefinance.saas.grapserver.biz.service.moxie.directive.process.MoxieAbstractDirectiveProcessor;
import com.treefinance.saas.grapserver.common.enums.TaskStatusEnum;
import com.treefinance.saas.grapserver.common.enums.moxie.EMoxieDirective;
import com.treefinance.saas.grapserver.common.model.dto.TaskDTO;
import com.treefinance.saas.grapserver.common.model.dto.moxie.MoxieDirectiveDTO;
import com.treefinance.saas.grapserver.dao.entity.AppLicense;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class MoxieFailureDirectiveProcessor extends MoxieAbstractDirectiveProcessor {

    @Override
    protected void doProcess(EMoxieDirective directive, MoxieDirectiveDTO directiveDTO) {
        TaskDTO taskDTO = directiveDTO.getTask();
        String appId = taskDTO.getAppId();

        // 任务置为失败
        taskDTO.setStatus(TaskStatusEnum.FAIL.getStatus());

        // 获取商户秘钥,包装数据:任务失败后返回失败信息加密后通过指令传递给前端
        AppLicense appLicense = appLicenseService.getAppLicense(appId);
        this.generateData(directiveDTO, appLicense);

        // 更新任务状态,记录失败任务日志
        taskService.failTaskWithStep(taskDTO.getId());


    }
}
