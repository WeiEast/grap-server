package com.treefinance.saas.grapserver.facade.model;

import java.io.Serializable;
import java.util.Date;

/**
 * taskRO
 *
 * @author haojiahong
 */
public class TaskRO implements Serializable {

    private Long id;
    private String uniqueId;
    private String appId;
    private String accountNo;
    private Byte bizType;
    /**
     * 业务类型名称
     */
    private String bizTypeName;
    private Byte status;
    private String webSite;
    private String stepCode;
    private Date createTime;
    private Date lastUpdateTime;

    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        return "TaskRO{" +
                "id=" + id +
                ", uniqueId='" + uniqueId + '\'' +
                ", appId='" + appId + '\'' +
                ", accountNo='" + accountNo + '\'' +
                ", bizType=" + bizType +
                ", bizTypeName='" + bizTypeName + '\'' +
                ", status=" + status +
                ", webSite='" + webSite + '\'' +
                ", stepCode='" + stepCode + '\'' +
                ", createTime=" + createTime +
                ", lastUpdateTime=" + lastUpdateTime +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public Byte getBizType() {
        return bizType;
    }

    public void setBizType(Byte bizType) {
        this.bizType = bizType;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getWebSite() {
        return webSite;
    }

    public void setWebSite(String webSite) {
        this.webSite = webSite;
    }

    public String getStepCode() {
        return stepCode;
    }

    public void setStepCode(String stepCode) {
        this.stepCode = stepCode;
    }

    public String getBizTypeName() {
        return bizTypeName;
    }

    public void setBizTypeName(String bizTypeName) {
        this.bizTypeName = bizTypeName;
    }

}
