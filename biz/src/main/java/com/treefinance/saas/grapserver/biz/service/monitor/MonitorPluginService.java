package com.treefinance.saas.grapserver.biz.service.monitor;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.treefinance.saas.assistant.model.HttpMonitorMessage;
import com.treefinance.saas.assistant.model.TaskMonitorMessage;
import com.treefinance.saas.assistant.model.TaskOperatorMonitorMessage;
import com.treefinance.saas.assistant.plugin.HttpMonitorPlugin;
import com.treefinance.saas.assistant.plugin.TaskMonitorPlugin;
import com.treefinance.saas.assistant.plugin.TaskOperatorMonitorPlugin;
import com.treefinance.saas.grapserver.biz.service.AppCallbackConfigService;
import com.treefinance.saas.grapserver.biz.service.TaskCallbackLogService;
import com.treefinance.saas.grapserver.common.model.dto.AppCallbackConfigDTO;
import com.treefinance.saas.grapserver.common.model.dto.TaskDTO;
import com.treefinance.saas.grapserver.dao.entity.TaskCallbackLog;
import com.treefinance.saas.grapserver.facade.enums.EDataType;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/**
 * Created by yh-treefinance on 2017/6/20.
 */
@Service
public class MonitorPluginService {
    private static final Logger logger = LoggerFactory.getLogger(MonitorPluginService.class);

    private final ConcurrentLinkedQueue<HttpMonitorMessage> httpQueue = new ConcurrentLinkedQueue<HttpMonitorMessage>();

    @Autowired
    private TaskMonitorPlugin taskMonitorPlugin;
    @Autowired
    private HttpMonitorPlugin httpMonitorPlugin;
    @Autowired
    private TaskOperatorMonitorPlugin taskOperatorMonitorPlugin;
    @Autowired
    private TaskCallbackLogService taskCallbackLogService;
    @Autowired
    private AppCallbackConfigService appCallbackConfigService;


    /**
     * 发送任务监控消息
     *
     * @param taskDTO
     */
    public void sendTaskMonitorMessage(TaskDTO taskDTO) {
        TaskMonitorMessage message = new TaskMonitorMessage();
        try {
            message.setAccountNo(taskDTO.getAccountNo());
            message.setAppId(taskDTO.getAppId());
            message.setBizType(taskDTO.getBizType());
            message.setCompleteTime(taskDTO.getCreateTime());
            message.setStatus(taskDTO.getStatus());
            message.setTaskId(taskDTO.getId());
            message.setWebSite(taskDTO.getWebSite());
            message.setUniqueId(taskDTO.getUniqueId());
            message.setStepCode(taskDTO.getStepCode());
            List<AppCallbackConfigDTO> appCallbackConfigList
                    = appCallbackConfigService.getByAppIdAndBizType(taskDTO.getAppId(), taskDTO.getBizType(), EDataType.MAIN_STREAM);
            List<Long> configIdList = appCallbackConfigList.stream()
                    .map(appCallbackConfigDTO -> Long.valueOf(appCallbackConfigDTO.getId())).collect(Collectors.toList());
            List<TaskCallbackLog> logList = taskCallbackLogService.getTaskCallbackLogs(taskDTO.getId(), configIdList);
            if (CollectionUtils.isNotEmpty(logList)) {
                TaskCallbackLog log = logList.get(0);
                Map<String, Object> attributeMap = Maps.newHashMap();
                attributeMap.put("callbackHttpCode", log.getHttpCode());
                attributeMap.put("callbackCode", log.getCallbackCode());
                attributeMap.put("callbackMsg", log.getCallbackMsg());
                message.setAttributes(attributeMap);
            }
            taskMonitorPlugin.sendMessage(message);
            logger.info("send message to monitor : message={}", JSON.toJSONString(taskDTO));

        } catch (Exception e) {
            logger.error(" send message to monitor failed : body=" + JSON.toJSONString(message), e);
        }
    }


    /**
     * 发送http监控消息
     *
     * @param message
     */
    public void pushHttpMonitorMessage(HttpMonitorMessage message) {
        httpQueue.offer(message);
    }

    /**
     * 定时执行消息发送
     */
    @Scheduled(fixedRate = 60000)
    public void scheduledSendHttpMonitorMessage() {
        List<HttpMonitorMessage> list = Lists.newArrayList();
        while (!httpQueue.isEmpty()) {
            HttpMonitorMessage message = httpQueue.poll();
            if (message != null) {
                list.add(message);
            }
        }
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        // 分批每批最大100个
        try {
            for (List<HttpMonitorMessage> messageList : Lists.partition(list, 100)) {
                httpMonitorPlugin.sendMessages(messageList);
            }
            logger.info("send message to monitor : message={}", JSON.toJSONString(list));
        } catch (Exception e) {
            logger.error(" send message to monitor failed : body=" + JSON.toJSONString(list), e);
            // 失败重新放入队列
            httpQueue.addAll(list);
        }
    }

    /**
     * 发送运营商监控消息
     *
     * @param message
     */
    public void sendTaskOperatorMonitorMessage(TaskOperatorMonitorMessage message) {
        try {
            taskOperatorMonitorPlugin.sendMessage(message);
            logger.info("运营商监控,send message to monitor : message={}", JSON.toJSONString(message));
        } catch (Exception e) {
            logger.error("运营商监控,send message to monitor failed : body={}", JSON.toJSONString(message), e);
        }

    }

}
