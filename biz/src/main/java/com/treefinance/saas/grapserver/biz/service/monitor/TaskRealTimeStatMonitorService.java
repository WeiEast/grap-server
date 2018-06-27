package com.treefinance.saas.grapserver.biz.service.monitor;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.treefinance.saas.assistant.model.TaskRealTimeMonitorMessage;
import com.treefinance.saas.assistant.plugin.rocketmq.producer.MonitorMessageProducer;
import com.treefinance.saas.grapserver.biz.service.TaskAttributeService;
import com.treefinance.saas.grapserver.biz.service.TaskService;
import com.treefinance.saas.grapserver.common.model.dto.TaskDTO;
import com.treefinance.saas.grapserver.common.utils.GrapDateUtils;
import com.treefinance.saas.grapserver.dao.entity.TaskAttribute;
import com.treefinance.saas.grapserver.facade.enums.ETaskStatLink;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Good Luck Bro , No Bug !
 * 任务实时监控
 *
 * @author haojiahong
 * @date 2018/6/19
 */
@Service
public class TaskRealTimeStatMonitorService {
    private static final Logger logger = LoggerFactory.getLogger(TaskRealTimeStatMonitorService.class);

    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskAttributeService taskAttributeService;
    @Autowired
    private MonitorMessageProducer monitorMessageProducer;

    /**
     * 需要监控的日志环节
     */
    private static List<String> logLinkList = ETaskStatLink.getStepCodeListBySource("task_log");

    @Async
    public void handleTaskLog(Long taskId, String code, Date dataTime) {
        logger.info("任务实时监控日志环节处理,taskId={},code={},dataTime={}", taskId, code, GrapDateUtils.getDateStrByDate(dataTime));
        if (!logLinkList.contains(code)) {
            return;
        }
        ETaskStatLink taskStatLink = ETaskStatLink.getItemByStepCode(code);
        if (taskStatLink == null) {
            logger.error("任务实时监控日志环节处理,需统计的任务环节未在枚举定义中找到,taskId={},code={}", taskId, code);
            return;
        }
        TaskDTO taskDTO = taskService.getById(taskId);
        if (taskDTO == null) {
            return;
        }
        String taskLinkStatCode = taskStatLink.getStatCode();
        String taskLinkStatName = taskStatLink.getDesc();

        TaskRealTimeMonitorMessage message = new TaskRealTimeMonitorMessage();
        message.setTaskId(taskDTO.getId());
        message.setSaasEnv(String.valueOf(taskDTO.getSaasEnv()));
        message.setAccountNo(taskDTO.getAccountNo());
        message.setAppId(taskDTO.getAppId());
        message.setBizType(taskDTO.getBizType());
        message.setDataTime(dataTime);
        message.setStatus(taskDTO.getStatus());
        message.setUniqueId(taskDTO.getUniqueId());
        message.setWebSite(taskDTO.getWebSite());
        message.setStatCode(taskLinkStatCode);
        message.setStatName(taskLinkStatName);

        //获取任务属性
        List<TaskAttribute> attributeList = taskAttributeService.findByTaskId(taskId);
        Map<String, String> attributeMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(attributeList)) {
            attributeList.forEach(taskAttribute -> attributeMap.put(taskAttribute.getName(), taskAttribute.getValue()));
        }
        message.setTaskAttributes(attributeMap);
        monitorMessageProducer.send(message);
        logger.info("任务实时监控日志环节处理,发送消息,taskId={},code={},message={}", taskId, code, JSON.toJSONString(message));

    }
}
