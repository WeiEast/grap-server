package com.treefinance.saas.grapserver.biz.mq;

import com.github.diamond.client.extend.annotation.AfterUpdate;
import com.github.diamond.client.extend.annotation.BeforeUpdate;
import com.github.diamond.client.extend.annotation.DAttribute;
import com.github.diamond.client.extend.annotation.DResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by luoyihua on 2017/4/26.
 */
@Component("mqConfig")
@Scope
@DResource
public class MqConfig {
    private static final Logger logger = LoggerFactory.getLogger(MqConfig.class);
    
    @DAttribute(key = "gateway.group.name")
    private String gatewayGroupName;

    @DAttribute(key = "mq.namesrvAddr")
    private String namesrvAddr;

    @DAttribute(key = "directive.group.name")
    private String directiveGroupName;

    @DAttribute(key = "consume.directive.topic")
    private String consumeDirectiveTopic;

    @DAttribute(key = "consume.directice.tag")
    private String consumeDirectiveTag;

    @DAttribute(key = "task.log.group.name")
    private String taskLogGroupName;

    @DAttribute(key = "consume.task.log.topic")
    private String consumeTaskLogTopic;

    @DAttribute(key = "consume.task.log.tag")
    private String consumeTaskLogTag;

    @DAttribute(key = "provider.rawdata.topic")
    private String providerRawdataTopic;

    @DAttribute(key = "provider.rawdata.tag")
    private String providerRawdataTag;

    @DAttribute(key = "monitor.group.name")
    private String monitorGroupName;

    @DAttribute(key = "monitor.access.topic")
    private String monitorAccessTopic;

    @DAttribute(key = "monitor.access.tag")
    private String monitorAccessTag;

    public String getMonitorGroupName() {
        return monitorGroupName;
    }

    public void setMonitorGroupName(String monitorGroupName) {
        this.monitorGroupName = monitorGroupName;
    }

    public String getMonitorAccessTopic() {
        return monitorAccessTopic;
    }

    public void setMonitorAccessTopic(String monitorAccessTopic) {
        this.monitorAccessTopic = monitorAccessTopic;
    }

    public String getMonitorAccessTag() {
        return monitorAccessTag;
    }

    public void setMonitorAccessTag(String monitorAccessTag) {
        this.monitorAccessTag = monitorAccessTag;
    }

    public String getProviderRawdataTopic() {
        return providerRawdataTopic;
    }

    public void setProviderRawdataTopic(String providerRawdataTopic) {
        this.providerRawdataTopic = providerRawdataTopic;
    }

    public String getProviderRawdataTag() {
        return providerRawdataTag;
    }

    public void setProviderRawdataTag(String providerRawdataTag) {
        this.providerRawdataTag = providerRawdataTag;
    }

    @BeforeUpdate
    public void before(String key, Object newValue) {
        logger.info(key + " update to " + newValue + " start...");
    }

    @AfterUpdate
    public void after(String key, Object newValue) {
        logger.info(key + " update to " + newValue + " end...");
    }

    public String getNamesrvAddr() {
        return namesrvAddr;
    }

    public void setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
    }

    public String getDirectiveGroupName() {
        return directiveGroupName;
    }

    public void setDirectiveGroupName(String directiveGroupName) {
        this.directiveGroupName = directiveGroupName;
    }

    public String getConsumeDirectiveTopic() {
        return consumeDirectiveTopic;
    }

    public void setConsumeDirectiveTopic(String consumeDirectiveTopic) {
        this.consumeDirectiveTopic = consumeDirectiveTopic;
    }

    public String getConsumeDirectiveTag() {
        return consumeDirectiveTag;
    }

    public void setConsumeDirectiveTag(String consumeDirectiveTag) {
        this.consumeDirectiveTag = consumeDirectiveTag;
    }

    public String getTaskLogGroupName() {
        return taskLogGroupName;
    }

    public void setTaskLogGroupName(String taskLogGroupName) {
        this.taskLogGroupName = taskLogGroupName;
    }

    public String getConsumeTaskLogTopic() {
        return consumeTaskLogTopic;
    }

    public void setConsumeTaskLogTopic(String consumeTaskLogTopic) {
        this.consumeTaskLogTopic = consumeTaskLogTopic;
    }

    public String getConsumeTaskLogTag() {
        return consumeTaskLogTag;
    }

    public void setConsumeTaskLogTag(String consumeTaskLogTag) {
        this.consumeTaskLogTag = consumeTaskLogTag;
    }

		public String getGatewayGroupName() {
			return gatewayGroupName;
		}

		public void setGatewayGroupName(String gatewayGroupName) {
			this.gatewayGroupName = gatewayGroupName;
		}
}
