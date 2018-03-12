package com.treefinance.saas.grapserver.biz.mq;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.exception.MQClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import static com.alibaba.rocketmq.common.protocol.heartbeat.MessageModel.CLUSTERING;

/**
 * Created by luoyihua on 2017/4/26.
 */
@Service
public class ListenerStarter {
    private static final Logger logger = LoggerFactory.getLogger(ListenerStarter.class);

    @Autowired
    private MqConfig mqConfig;
    private DefaultMQPushConsumer directiveConsumer;
    private DefaultMQPushConsumer taskLogConsumer;
    private DefaultMQPushConsumer deliveryAddressConsumer;
    private DefaultMQPushConsumer asyncGrapDataConsumer;

    @Autowired
    private DirectiveMessageListener directiveMessageListener;
    @Autowired
    private TaskLogMessageListener taskLogMessageListener;
    @Autowired
    private DeliveryAddressMessageListener deliveryAddressMessageListener;
    @Autowired
    private AsyncGrapDataMessageListener asyncGrapDataMessageListener;

    @PostConstruct
    public void init() throws MQClientException {
        initDirectiveMessageMQ();
        initTaskLogMessageMQ();
        initDeliveryAddressMessageMQ();
        initAsyncGrapDataMessageMQ();
    }

    @PreDestroy
    public void destroy() {
        directiveConsumer.shutdown();
        logger.info("关闭指令数据的消费者");

        taskLogConsumer.shutdown();
        logger.info("关闭任务log数据的消费者");

        deliveryAddressConsumer.shutdown();
        logger.info("关闭收货地址数据的消费者");

        asyncGrapDataConsumer.shutdown();
        logger.info("关闭异步数据抓取的消费者");
    }

    private void initDirectiveMessageMQ() throws MQClientException {
        directiveConsumer = new DefaultMQPushConsumer(mqConfig.getDirectiveGroupName());
//        directiveConsumer.setInstanceName(mqConfig.getDirectiveGroupName());
        directiveConsumer.setNamesrvAddr(mqConfig.getNamesrvAddr());
        directiveConsumer.subscribe(mqConfig.getConsumeDirectiveTopic(), mqConfig.getConsumeDirectiveTag());
        directiveConsumer.setMessageModel(CLUSTERING);
        directiveConsumer.registerMessageListener(directiveMessageListener);
        directiveConsumer.start();
        logger.info("启动指令数据的消费者.nameserver:{},topic:{},tag:{}", mqConfig.getNamesrvAddr(),
                mqConfig.getConsumeDirectiveTopic(), mqConfig.getConsumeDirectiveTag());
    }

    private void initTaskLogMessageMQ() throws MQClientException {
        taskLogConsumer = new DefaultMQPushConsumer(mqConfig.getTaskLogGroupName());
//        taskLogConsumer.setInstanceName(mqConfig.getTaskLogGroupName());
        taskLogConsumer.setNamesrvAddr(mqConfig.getNamesrvAddr());
        taskLogConsumer.subscribe(mqConfig.getConsumeTaskLogTopic(), mqConfig.getConsumeTaskLogTag());
        taskLogConsumer.setMessageModel(CLUSTERING);
        taskLogConsumer.registerMessageListener(taskLogMessageListener);
        taskLogConsumer.start();
        logger.info("启动任务log数据的消费者.nameserver:{},topic:{},tag:{}", mqConfig.getNamesrvAddr(),
                mqConfig.getConsumeTaskLogTopic(), mqConfig.getConsumeTaskLogTag());
    }


    private void initDeliveryAddressMessageMQ() throws MQClientException {
        String group = "grap-data";
        deliveryAddressConsumer = new DefaultMQPushConsumer(group);
//        deliveryAddressConsumer.setInstanceName(group);
        deliveryAddressConsumer.setNamesrvAddr(mqConfig.getNamesrvAddr());
        String topic = "ecommerce_trade_address";
        String tag = "ecommerce";
        deliveryAddressConsumer.subscribe(topic, tag);
        deliveryAddressConsumer.setMessageModel(CLUSTERING);
        deliveryAddressConsumer.registerMessageListener(deliveryAddressMessageListener);
        deliveryAddressConsumer.start();
        logger.info("启动收货地址数据的消费者.nameserver:{},topic:{},tag:{}",
                mqConfig.getNamesrvAddr(), topic, tag);
    }


    /**
     * 异步数据抓取
     *
     * @throws MQClientException
     */
    private void initAsyncGrapDataMessageMQ() throws MQClientException {
        String group = "async_grap_data_group";
        asyncGrapDataConsumer = new DefaultMQPushConsumer(group);
        asyncGrapDataConsumer.setNamesrvAddr(mqConfig.getNamesrvAddr());
        String topic = "async-grap-data";
        String tag = "*";
        asyncGrapDataConsumer.subscribe(topic, tag);
        asyncGrapDataConsumer.setMessageModel(CLUSTERING);
        asyncGrapDataConsumer.registerMessageListener(asyncGrapDataMessageListener);
        asyncGrapDataConsumer.start();
        logger.info("启动异步数据抓取的消费者.nameserver:{},topic:{},tag:{}",
                mqConfig.getNamesrvAddr(), topic, tag);
    }
}
