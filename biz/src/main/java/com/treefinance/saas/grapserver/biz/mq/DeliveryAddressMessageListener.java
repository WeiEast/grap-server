package com.treefinance.saas.grapserver.biz.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.treefinance.saas.grapserver.biz.mq.model.DeliveryAddressMessage;
import com.treefinance.saas.grapserver.biz.service.DeliveryAddressService;
import com.treefinance.saas.grapserver.biz.service.directive.DirectiveService;
import com.treefinance.saas.grapserver.common.model.dto.DirectiveDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus.CONSUME_SUCCESS;

/**
 * Created by luoyihua on 2017/4/26.
 */
@Service
public class DeliveryAddressMessageListener implements MessageListenerConcurrently {
    private static final Logger logger = LoggerFactory.getLogger(DeliveryAddressMessageListener.class);

    @Autowired
    private DeliveryAddressService deliveryAddressService;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        MessageExt msg = list.get(0);
        String message = new String(msg.getBody());
        try {
            logger.info("消费收货地址消息数据==>{}", message);
            DeliveryAddressMessage addressMessage = JSON.parseObject(message, DeliveryAddressMessage.class);
            deliveryAddressService.callback(addressMessage);
        } catch (Throwable cause) {
            if (msg.getReconsumeTimes() > 0) {
                logger.error("丢弃该消息,因为重试一次之后, 消费指令数据消息仍然出错.body={}", message, cause);
                return CONSUME_SUCCESS;
            } else {
                logger.error("消费指令数据的消息时出错,即将再重试一次body=%s", message, cause);
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        }
        return CONSUME_SUCCESS;
    }
}
