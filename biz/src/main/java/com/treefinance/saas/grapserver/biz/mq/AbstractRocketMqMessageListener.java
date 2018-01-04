package com.treefinance.saas.grapserver.biz.mq;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus.CONSUME_SUCCESS;

/**
 * 抽象消息监听
 * Created by yh-treefinance on 2017/12/19.
 */
public abstract class AbstractRocketMqMessageListener implements MessageListenerConcurrently {
    /**
     * logger
     */
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        long start = System.currentTimeMillis();
        MessageExt msg = msgs.get(0);
        String message = new String(msg.getBody());
        try {
            handleMessage(message);
        } catch (Throwable cause) {
            if (msg.getReconsumeTimes() > 0) {
                logger.error("丢弃该消息,因为重试一次之后, 消费指令数据消息仍然出错.body={}", message, cause);
                return CONSUME_SUCCESS;
            }
            logger.error("消费指令数据的消息时出错,即将再重试一次body=%s", message, cause);
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        } finally {
            logger.info("消费MQ消息,耗时{}ms: topic={},tag={},msgId={},body={}", (System.currentTimeMillis() - start),
                    msg.getTopic(), msg.getTags(), msg.getMsgId(), message);
        }
        return CONSUME_SUCCESS;
    }

    /**
     * 消费处理消息
     *
     * @param message
     */
    protected abstract void handleMessage(String message);
}
