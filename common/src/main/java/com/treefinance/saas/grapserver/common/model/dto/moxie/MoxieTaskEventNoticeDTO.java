package com.treefinance.saas.grapserver.common.model.dto.moxie;

import java.io.Serializable;

/**
 * @author haojiahong on 2017/9/21.
 */
public class MoxieTaskEventNoticeDTO implements Serializable {

    private static final long serialVersionUID = 3646957264685997957L;

    /**
     * 魔蝎回调通知对应的魔蝎任务id
     */
    private String moxieTaskId;

    /**
     * 魔蝎回调通知对应的回调信息
     */
    private String message;

    @Override
    public String toString() {
        return "MoxieTaskEventNoticeDTO{" +
                "moxieTaskId='" + moxieTaskId + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

    public String getMoxieTaskId() {
        return moxieTaskId;
    }

    public void setMoxieTaskId(String moxieTaskId) {
        this.moxieTaskId = moxieTaskId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
