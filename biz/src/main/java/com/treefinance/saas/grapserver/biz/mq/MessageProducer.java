package com.treefinance.saas.grapserver.biz.mq;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.SendStatus;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import com.treefinance.saas.grapserver.common.exception.FailureInSendingToMQException;

/**
 * @author luoyihua
 * @Title: MessageProducer.java
 * @Description: TODO
 * @date 2017年5月8日 下午1:58:14
 */
@Service
public class MessageProducer {
    private static final Logger logger = LoggerFactory.getLogger(MessageProducer.class);

    private DefaultMQProducer producer;
    @Autowired
    private MqConfig mqConfig;

    @PostConstruct
    public void init() throws MQClientException {
        logger.info("init message producer: config={}", JSON.toJSONString(mqConfig));
        producer = new DefaultMQProducer(mqConfig.getGatewayGroupName());
        producer.setNamesrvAddr(mqConfig.getNamesrvAddr());
        producer.setMaxMessageSize(1024 * 1024 * 2);
        producer.start();
    }

    @PreDestroy
    public void destroy() {
        producer.shutdown();
    }

    public void send(String body, String topic, String tag, String keyPrefix) throws FailureInSendingToMQException {
        String key = UUID.randomUUID().toString();
        if (StringUtils.isNotEmpty(keyPrefix)) {
            key = keyPrefix + "_" + tag;
        }
        try {
            SendResult sendResult = producer.send(new Message(topic, tag, key, body.getBytes("utf-8")));
            logger.debug("已发送消息[topic={},tag={},key={},body={},发送状态={}]", topic, tag, key, body, sendResult.getSendStatus());
            if (sendResult.getSendStatus() != SendStatus.SEND_OK) {
                throw new FailureInSendingToMQException(
                        String.format("发送MQ消息[topic=%s,tag=%s,key=%s,body=%s]到消息中间件失败,发送状态为%s", topic, tag, key, body, sendResult.getSendStatus()));
            }
        } catch (InterruptedException | RemotingException | MQClientException | MQBrokerException | UnsupportedEncodingException e) {
            throw new FailureInSendingToMQException(String.format("发送MQ消息[topic=%s,tag=%s,key=%s,body=%s]到消息中间件失败", topic, tag, key, body), e);
        }
    }

}
