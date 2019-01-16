package com.treefinance.saas.grapserver.biz.mq;

import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.SendStatus;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import com.treefinance.saas.assistant.exception.FailureInSendingToMQException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.UUID;

/**
 * @author 张琰佳
 * @since 3:58 PM 2019/1/15
 */
@Service
public class MessageProducer {
    private static final Logger logger = LoggerFactory.getLogger(MessageProducer.class);

    @Autowired
    private MQConfig mqConfig;

    private DefaultMQProducer producer;

    @PostConstruct
    public void init() throws MQClientException {
        producer = new DefaultMQProducer(mqConfig.getGroupName());
        producer.setNamesrvAddr(mqConfig.getNamesrvAddr());
        producer.setMaxMessageSize(1024 * 1024 * 20);
        producer.start();
    }
    @PreDestroy
    public void destroy() {
        producer.shutdown();
    }

    public void send(String body, String topic, String tag, String key) throws FailureInSendingToMQException {
        if (StringUtils.isBlank(key)) {
            key = UUID.randomUUID().toString();
        }

        try {
            SendResult sendResult = producer.send(new Message(topic, tag, key, body.getBytes()));
            logger.info("已发送消息[topic={},tag={},key={},发送状态为{},message={}]", topic, tag, key, body,
                sendResult.getSendStatus());
            if (sendResult.getSendStatus() != SendStatus.SEND_OK) {
                throw new FailureInSendingToMQException(String.format("发送消息[key=%s,body=%s]到消息中间件失败,发送状态为%s", key,
                    body, sendResult.getSendStatus()));
            }
        } catch (InterruptedException | RemotingException | MQClientException | MQBrokerException e) {
            throw new FailureInSendingToMQException(String.format("发送消息[key=%s,body=%s]到消息中间件失败", key, body), e);
        }
    }
}
