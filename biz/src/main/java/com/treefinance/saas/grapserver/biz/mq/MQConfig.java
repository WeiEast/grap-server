package com.treefinance.saas.grapserver.biz.mq;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author 张琰佳
 * @since 4:00 PM 2019/1/15
 */
@Service
public class MQConfig {
    @Value("${mq.namesrvAddr}")
    private String namesrvAddr;

    @Value("${mq.grap.groupName}")
    private String groupName;

    @Value("${mq.produce.directive.topic}")
    private String produceDirectiveTopic;

    @Value("${mq.produce.directive.tag}")
    private String produceDirectiveTag;

    public String getNamesrvAddr() {
        return namesrvAddr;
    }

    public void setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getProduceDirectiveTopic() {
        return produceDirectiveTopic;
    }

    public void setProduceDirectiveTopic(String produceDirectiveTopic) {
        this.produceDirectiveTopic = produceDirectiveTopic;
    }

    public String getProduceDirectiveTag() {
        return produceDirectiveTag;
    }

    public void setProduceDirectiveTag(String produceDirectiveTag) {
        this.produceDirectiveTag = produceDirectiveTag;
    }
}
