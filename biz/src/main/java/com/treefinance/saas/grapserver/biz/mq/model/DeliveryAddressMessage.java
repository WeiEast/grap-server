package com.treefinance.saas.grapserver.biz.mq.model;

import java.io.Serializable;

/**
 * 收货地址消息
 * Created by yh-treefinance on 2017/9/28.
 */
public class DeliveryAddressMessage implements Serializable {
    /**
     * 任务Id
     */
    private Long taskId;
    /**
     * 状态: 1:成功，2:失败（失败时data为空）
     */
    private Integer status;
    /**
     * 账号
     */
    private String accountNo;
    /**
     * 数据
     */
    private String data;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
