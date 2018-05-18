package com.treefinance.saas.grapserver.biz.service.monitor;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.treefinance.saas.assistant.model.TaskEcommeceMonitorMessage;
import com.treefinance.saas.assistant.model.TaskStep;
import com.treefinance.saas.assistant.plugin.TaskEcommerceMonitorPlugin;
import com.treefinance.saas.grapserver.biz.service.TaskAttributeService;
import com.treefinance.saas.grapserver.biz.service.TaskBuryPointLogService;
import com.treefinance.saas.grapserver.biz.service.TaskLogService;
import com.treefinance.saas.grapserver.common.enums.EProcessStep;
import com.treefinance.saas.grapserver.common.enums.ETaskStep;
import com.treefinance.saas.grapserver.common.model.dto.TaskDTO;
import com.treefinance.saas.grapserver.common.utils.DataConverterUtils;
import com.treefinance.saas.grapserver.dao.entity.TaskAttribute;
import com.treefinance.saas.grapserver.dao.entity.TaskBuryPointLog;
import com.treefinance.saas.grapserver.dao.entity.TaskLog;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by yh-treefinance on 2018/1/31.
 */
@Service
public class EcommerceMonitorService {

    private static final Logger logger = LoggerFactory.getLogger(EcommerceMonitorService.class);

    @Autowired
    private TaskEcommerceMonitorPlugin taskEcommerceMonitorPlugin;
    @Autowired
    private TaskAttributeService taskAttributeService;
    @Autowired
    private TaskBuryPointLogService taskBuryPointLogService;
    @Autowired
    private TaskLogService taskLogService;

    /**
     * 发送消息
     *
     * @param taskDTO
     */
    public void sendMessage(TaskDTO taskDTO) {
        long start = System.currentTimeMillis();
        Long taskId = taskDTO.getId();
        TaskEcommeceMonitorMessage message = DataConverterUtils.convert(taskDTO, TaskEcommeceMonitorMessage.class);

        // 1.获取任务属性
        List<TaskAttribute> attributeList = taskAttributeService.findByTaskId(taskId);
        Map<String, String> attributeMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(attributeList)) {
            attributeList.forEach(taskAttribute -> attributeMap.put(taskAttribute.getName(), taskAttribute.getValue()));
        }
        message.setTaskAttributes(attributeMap);

        // 2.获取任务步骤
        List<TaskStep> taskSteps = Lists.newArrayList();
        Map<Integer, TaskStep> taskStepMap = Maps.newHashMap();
        List<TaskLog> taskLogs = taskLogService.queryTaskLog(taskId, ETaskStep.TASK_CREATE.getText(),
                ETaskStep.LOGIN_SUCCESS.getText(), ETaskStep.LOGIN_FAIL.getText(), ETaskStep.CRAWL_SUCCESS.getText(),
                ETaskStep.DATA_SAVE_SUCCESS.getText(), ETaskStep.CALLBACK_SUCCESS.getText());
        List<String> taskLogMsgs = taskLogs.stream().map(TaskLog::getMsg).collect(Collectors.toList());
        // 任务创建
        if (taskLogMsgs.contains(ETaskStep.TASK_CREATE.getText())) {
            taskStepMap.put(1, new TaskStep(1, EProcessStep.CREATE.getCode(), EProcessStep.CREATE.getName()));
        }
        //一键登录
        String sourceType = attributeMap.get("sourceType");

        if (StringUtils.equalsIgnoreCase("1", sourceType)) {
            //来源sdk
            //sdk没有一键登录按钮,设置一键登录按钮数量与创建任务数量一致
            if (taskLogMsgs.contains(ETaskStep.TASK_CREATE.getText())) {
                taskStepMap.put(2, new TaskStep(2, EProcessStep.ONE_CLICK_LOGIN.getCode(), EProcessStep.ONE_CLICK_LOGIN.getName()));
            }
        } else if (StringUtils.equalsIgnoreCase("2", sourceType)) {
            //来源h5
            // H5版本基于埋点数据，判断是否一键登录
            List<TaskBuryPointLog> taskBuryPointLogs = taskBuryPointLogService.queryTaskBuryPointLogByCode(taskId, "100803");
            if (CollectionUtils.isNotEmpty(taskBuryPointLogs)) {
                taskStepMap.put(2, new TaskStep(2, EProcessStep.ONE_CLICK_LOGIN.getCode(), EProcessStep.ONE_CLICK_LOGIN.getName()));
            }
        } else {
            //其他来源
            logger.info("send task ecommerce message to saas-monitor,存在未知任务来源sourceType={},task={}", sourceType, JSON.toJSONString(taskDTO));
        }


        // 确认登录
        if (taskLogMsgs.contains(ETaskStep.LOGIN_SUCCESS.getText()) || taskLogMsgs.contains(ETaskStep.LOGIN_FAIL.getText())) {
            taskStepMap.put(3, new TaskStep(3, EProcessStep.CONFIRM_LOGIN.getCode(), EProcessStep.CONFIRM_LOGIN.getName()));
        }
        //登录成功
        if (taskLogMsgs.contains(ETaskStep.LOGIN_SUCCESS.getText())) {
            taskStepMap.put(4, new TaskStep(4, EProcessStep.LOGIN.getCode(), EProcessStep.LOGIN.getName()));
        }
        // 爬取成功
        if (taskLogMsgs.contains(ETaskStep.CRAWL_SUCCESS.getText())) {
            taskStepMap.put(5, new TaskStep(5, EProcessStep.CRAWL.getCode(), EProcessStep.CRAWL.getName()));
        }
        // 数据保存成功
        if (taskLogMsgs.contains(ETaskStep.DATA_SAVE_SUCCESS.getText())) {
            taskStepMap.put(6, new TaskStep(6, EProcessStep.PROCESS.getCode(), EProcessStep.PROCESS.getName()));
        }
        // 回调成功
        if (taskLogMsgs.contains(ETaskStep.CALLBACK_SUCCESS.getText())) {
            taskStepMap.put(7, new TaskStep(7, EProcessStep.CALLBACK.getCode(), EProcessStep.CALLBACK.getName()));
        }

        //判断任务步骤是否正确或有遗漏
        for (int i = 1; i <= 7; i++) {
            if (!taskStepMap.keySet().contains(i)) {
                break;
            }
            taskSteps.add(taskStepMap.get(i));
        }
        message.setTaskSteps(taskSteps);
        // 4.发送消息
        taskEcommerceMonitorPlugin.sendMessage(message);
        logger.info("send task ecommerce message to saas-monitor cost{}ms , message={}", System.currentTimeMillis() - start, JSON.toJSONString(message));
    }
}
