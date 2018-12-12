/*
 * Copyright © 2015 - 2017 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.treefinance.saas.grapserver.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.treefinance.saas.assistant.model.HttpMonitorMessage;
import com.treefinance.saas.assistant.plugin.HttpMonitorPlugin;
import com.treefinance.saas.grapserver.biz.service.MonitorService;
import com.treefinance.saas.grapserver.context.component.AbstractService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Jerry
 * @date 2018/12/12 13:27
 */
@Service("monitorService")
public class MonitorServiceImpl extends AbstractService implements MonitorService {

    private final ConcurrentLinkedQueue<HttpMonitorMessage> httpQueue = new ConcurrentLinkedQueue<>();

    @Autowired
    private HttpMonitorPlugin httpMonitorPlugin;

    /**
     * 发送http监控消息
     */
    @Override
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
            logger.error("send message to monitor failed : size={}", JSON.toJSONString(list), e);
            // 失败重新放入队列
            httpQueue.addAll(list);
        }
    }
}
