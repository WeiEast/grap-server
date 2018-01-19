package com.treefinance.saas.grapserver.biz.mq.model;

import java.io.Serializable;

/**
 * Created by haojiahong on 2018/1/18.
 */
public class QRCodeAccountNoMessage implements Serializable {

    private static final long serialVersionUID = -4929043581748474777L;

    private Long taskId;
    private String accountNo;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }
}
