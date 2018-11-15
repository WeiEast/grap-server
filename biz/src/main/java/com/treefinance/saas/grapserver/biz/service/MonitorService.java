package com.treefinance.saas.grapserver.biz.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.treefinance.saas.assistant.model.HttpMonitorMessage;
import com.treefinance.saas.assistant.plugin.HttpMonitorPlugin;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author yh-treefinance on 2017/6/20.
 */
@Service
public class MonitorService {

    private static final Logger logger = LoggerFactory.getLogger(MonitorService.class);

    private final ConcurrentLinkedQueue<HttpMonitorMessage> httpQueue = new ConcurrentLinkedQueue<HttpMonitorMessage>();

    @Autowired
    private HttpMonitorPlugin httpMonitorPlugin;

    /**
     * 发送http监控消息
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
            logger.info("send message to monitor : size={}", list.size());
        } catch (Exception e) {
            logger.error(" send message to monitor failed : size=" + JSON.toJSONString(list), e);
            // 失败重新放入队列
            httpQueue.addAll(list);
        }
    }

}
