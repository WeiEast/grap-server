package com.treefinance.saas.grapserver.biz.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.rocketmq.shade.io.netty.util.internal.StringUtil;
import com.google.common.collect.Maps;
import com.treefinance.saas.grapserver.biz.mq.MessageProducer;
import com.treefinance.saas.grapserver.common.model.Constants;
import com.treefinance.saas.grapserver.common.model.dto.AppCallbackConfigDTO;
import com.treefinance.saas.grapserver.common.model.dto.TaskDTO;
import com.treefinance.saas.knife.result.SimpleResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 回调结果处理
 * Created by yh-treefinance on 2017/11/7.
 */
@Service
public class CallbackResultService {
    /**
     * logger
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * topic
     */
    private static final String TOPIC = "crawler_monitor";
    /**
     * tag
     */
    private static final String TAG = "callback_info";

    @Autowired
    private MessageProducer messageProducer;

    public void handleResult(TaskDTO task, String result, AppCallbackConfigDTO config, int httpCode) {
        Map<String, Object> messageMap = Maps.newHashMap();
        try {
            messageMap.put("taskId", task.getId());
            messageMap.put("taskid", task.getId());
            messageMap.put("callbackConfigId", config.getId());
            messageMap.put("appId", task.getAppId());
            messageMap.put("uniqueId", task.getUniqueId());
            messageMap.put("httpCode", httpCode);
            messageMap.put("result", result);
            result = result.trim();
            if (!StringUtil.isNullOrEmpty(result) && result.startsWith("{") && result.endsWith("}")) {
                SimpleResult simpleResult = JSON.parseObject(result, SimpleResult.class);
                if (simpleResult != null && StringUtils.isNotEmpty(simpleResult.getErrorMsg())) {
                    messageMap.put(Constants.ERROR_MSG_NAME, simpleResult.getErrorMsg());
                }
            }
            String jsonBody = JSON.toJSONString(messageMap);
            messageProducer.send(jsonBody, TOPIC, TAG, "");
            logger.info("send callback result message: message={}, result={}", JSON.toJSONString(messageMap), result);
        } catch (Exception e) {
            logger.error("send callback result message: httpCode={}, message={}，task={}, result={}",
                    httpCode, JSON.toJSONString(messageMap), JSON.toJSONString(task), result, e);
        }
    }
}
