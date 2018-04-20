package com.treefinance.saas.grapserver.biz.service.directive.process.impl;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.gateway.servicefacade.enums.BizTypeEnum;
import com.treefinance.saas.grapserver.biz.common.AsycExcutor;
import com.treefinance.saas.grapserver.biz.service.directive.process.AbstractDirectiveProcessor;
import com.treefinance.saas.grapserver.biz.service.monitor.MonitorService;
import com.treefinance.saas.grapserver.common.enums.EBizType;
import com.treefinance.saas.grapserver.common.enums.EDirective;
import com.treefinance.saas.grapserver.common.enums.ETaskStatus;
import com.treefinance.saas.grapserver.common.model.Constants;
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

        handleTaskFailMsg(directiveDTO, taskDTO);
        //任务终态处理
        doFinalStateProcess(directive, directiveDTO);
        // 7.异步触发触发回调
        asycExcutor.runAsyc(directiveDTO, _directiveDTO -> {
            callback(dataMap, appLicense, _directiveDTO);
        });
    }

    /**
     * 处理返回到前端的消息
     *
     * @param directiveDTO
     * @param taskDTO
     */
    private void handleTaskFailMsg(DirectiveDTO directiveDTO, TaskDTO taskDTO) {
        try {
            if (BizTypeEnum.valueOfType(BizTypeEnum.OPERATOR).equals(taskDTO.getBizType())) {
                Map<String, Object> remarkMap = JSON.parseObject(directiveDTO.getRemark());
                //如果是运营商维护导致任务失败,爬数发来的任务指令中,directiveDTO的remark字段为{"errorMsg","当前运营商正在维护中，请稍后重试"}.
                //如果是其他原因导致的任务失败,则返回下面的默认值.
                if (remarkMap.get("errorMsg") == null) {
                    remarkMap.put("errorMsg", Constants.OPERATOR_TASK_FAIL_MSG);
                }
                directiveDTO.setRemark(JSON.toJSONString(remarkMap));
                logger.info("handle task-fail-msg: result={},directiveDTO={}", JSON.toJSONString(directiveDTO));
            }
            if (EBizType.DIPLOMA.getCode().equals(taskDTO.getBizType())) {
                Map<String, Object> remarkMap = JSON.parseObject(directiveDTO.getRemark());
                remarkMap.put("errorMsg", Constants.DIPLOMA_TASK_FAIL_MSG);
                directiveDTO.setRemark(JSON.toJSONString(remarkMap));
                logger.info("handle task-fail-msg: result={},directiveDTO={}", JSON.toJSONString(directiveDTO));
            }
        } catch (Exception e) {
            logger.info("handle result failed : directiveDTO={}", JSON.toJSONString(directiveDTO), e);
        }
    }
}
