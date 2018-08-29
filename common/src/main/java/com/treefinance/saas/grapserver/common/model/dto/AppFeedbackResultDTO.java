package com.treefinance.saas.grapserver.common.model.dto;

import java.io.Serializable;

/**
 * @author haojiahong
 * @date 2018/8/29
 */
public class AppFeedbackResultDTO implements Serializable {

    private static final long serialVersionUID = -6502289982033697840L;

    private String appId;
    private Byte bizType;
    private Long taskId;
    private String uniqueId;
    private String feedbackDesc;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Byte getBizType() {
        return bizType;
    }

    public void setBizType(Byte bizType) {
        this.bizType = bizType;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getFeedbackDesc() {
        return feedbackDesc;
    }

    public void setFeedbackDesc(String feedbackDesc) {
        this.feedbackDesc = feedbackDesc;
    }
}
