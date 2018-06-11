package com.treefinance.saas.grapserver.common.model.dto;

import java.io.Serializable;

/**
 * Good Luck Bro , No Bug !
 *
 * @author haojiahong
 * @date 2018/6/11
 */
public class CallbackFailureReasonDTO implements Serializable {

    private static final long serialVersionUID = -3496927105989144684L;

    private Long taskId;

    private Long callbackConfigId;

    private Byte failureReason;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Byte getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(Byte failureReason) {
        this.failureReason = failureReason;
    }

    public Long getCallbackConfigId() {
        return callbackConfigId;
    }

    public void setCallbackConfigId(Long callbackConfigId) {
        this.callbackConfigId = callbackConfigId;
    }
}
