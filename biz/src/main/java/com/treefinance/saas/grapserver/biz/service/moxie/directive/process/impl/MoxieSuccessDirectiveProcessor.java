package com.treefinance.saas.grapserver.biz.service.moxie.directive.process.impl;

import com.treefinance.saas.grapserver.biz.service.moxie.directive.process.MoxieAbstractDirectiveProcessor;
import com.treefinance.saas.grapserver.common.enums.ETaskStatus;
import com.treefinance.saas.grapserver.common.enums.moxie.EMoxieDirective;
import com.treefinance.saas.grapserver.common.model.dto.TaskDTO;
import com.treefinance.saas.grapserver.common.model.dto.moxie.MoxieDirectiveDTO;
import com.treefinance.saas.grapserver.dao.entity.AppLicense;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 成功指令处理
 * Created by yh-treefinance on 2017/7/6.
 */
@Component
public class MoxieSuccessDirectiveProcessor extends MoxieAbstractDirectiveProcessor {

    protected static final Logger logger = LoggerFactory.getLogger(MoxieSuccessDirectiveProcessor.class);

    @Override
    protected void doProcess(EMoxieDirective directive, MoxieDirectiveDTO directiveDTO) {
        TaskDTO taskDTO = directiveDTO.getTask();
        Long taskId = taskDTO.getId();
        String appId = taskDTO.getAppId();

        //任务置为成功
        taskDTO.setStatus(ETaskStatus.SUCCESS.getStatus());

        // 获取商户密钥,包装数据:洗数成功后返回的dataUrl加密后通过指令传递给前端
        AppLicense appLicense = appLicenseService.getAppLicense(appId);
        this.generateData(directiveDTO, appLicense);

        // 更新任务状态,记录任务成功日志
        taskService.updateTaskStatusWithStep(taskId, ETaskStatus.SUCCESS.getStatus());
    }


}
