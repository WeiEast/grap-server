package com.treefinance.saas.grapserver.common.model.dto.moxie;

import java.io.Serializable;

/**
 * Created by haojiahong on 2017/9/21.
 */
public class MoxieTaskEventNoticeDTO implements Serializable {
    private static final long serialVersionUID = 3646957264685997957L;

    private String moxieTaskId;//魔蝎回调通知对应的魔蝎任务id
    private String message;//魔蝎回调通知对应的回调信息

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
