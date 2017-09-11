package com.treefinance.saas.grapserver.dao.entity;

import java.io.Serializable;
import java.util.Date;

public class Task implements Serializable {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task.Id
     *
     * @mbggenerated Wed Aug 23 14:51:20 CST 2017
     */
    private Long id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task.UniqueId
     *
     * @mbggenerated Wed Aug 23 14:51:20 CST 2017
     */
    private String uniqueId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task.AppId
     *
     * @mbggenerated Wed Aug 23 14:51:20 CST 2017
     */
    private String appId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task.AccountNo
     *
     * @mbggenerated Wed Aug 23 14:51:20 CST 2017
     */
    private String accountNo;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task.WebSite
     *
     * @mbggenerated Wed Aug 23 14:51:20 CST 2017
     */
    private String webSite;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task.BizType
     *
     * @mbggenerated Wed Aug 23 14:51:20 CST 2017
     */
    private Byte bizType;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task.Status
     *
     * @mbggenerated Wed Aug 23 14:51:20 CST 2017
     */
    private Byte status;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task.StepCode
     *
     * @mbggenerated Wed Aug 23 14:51:20 CST 2017
     */
    private String stepCode;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task.CreateTime
     *
     * @mbggenerated Wed Aug 23 14:51:20 CST 2017
     */
    private Date createTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task.LastUpdateTime
     *
     * @mbggenerated Wed Aug 23 14:51:20 CST 2017
     */
    private Date lastUpdateTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table task
     *
     * @mbggenerated Wed Aug 23 14:51:20 CST 2017
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task.Id
     *
     * @return the value of task.Id
     *
     * @mbggenerated Wed Aug 23 14:51:20 CST 2017
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task.Id
     *
     * @param id the value for task.Id
     *
     * @mbggenerated Wed Aug 23 14:51:20 CST 2017
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task.UniqueId
     *
     * @return the value of task.UniqueId
     *
     * @mbggenerated Wed Aug 23 14:51:20 CST 2017
     */
    public String getUniqueId() {
        return uniqueId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task.UniqueId
     *
     * @param uniqueId the value for task.UniqueId
     *
     * @mbggenerated Wed Aug 23 14:51:20 CST 2017
     */
    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId == null ? null : uniqueId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task.AppId
     *
     * @return the value of task.AppId
     *
     * @mbggenerated Wed Aug 23 14:51:20 CST 2017
     */
    public String getAppId() {
        return appId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task.AppId
     *
     * @param appId the value for task.AppId
     *
     * @mbggenerated Wed Aug 23 14:51:20 CST 2017
     */
    public void setAppId(String appId) {
        this.appId = appId == null ? null : appId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task.AccountNo
     *
     * @return the value of task.AccountNo
     *
     * @mbggenerated Wed Aug 23 14:51:20 CST 2017
     */
    public String getAccountNo() {
        return accountNo;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task.AccountNo
     *
     * @param accountNo the value for task.AccountNo
     *
     * @mbggenerated Wed Aug 23 14:51:20 CST 2017
     */
    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo == null ? null : accountNo.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task.WebSite
     *
     * @return the value of task.WebSite
     *
     * @mbggenerated Wed Aug 23 14:51:20 CST 2017
     */
    public String getWebSite() {
        return webSite;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task.WebSite
     *
     * @param webSite the value for task.WebSite
     *
     * @mbggenerated Wed Aug 23 14:51:20 CST 2017
     */
    public void setWebSite(String webSite) {
        this.webSite = webSite == null ? null : webSite.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task.BizType
     *
     * @return the value of task.BizType
     *
     * @mbggenerated Wed Aug 23 14:51:20 CST 2017
     */
    public Byte getBizType() {
        return bizType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task.BizType
     *
     * @param bizType the value for task.BizType
     *
     * @mbggenerated Wed Aug 23 14:51:20 CST 2017
     */
    public void setBizType(Byte bizType) {
        this.bizType = bizType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task.Status
     *
     * @return the value of task.Status
     *
     * @mbggenerated Wed Aug 23 14:51:20 CST 2017
     */
    public Byte getStatus() {
        return status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task.Status
     *
     * @param status the value for task.Status
     *
     * @mbggenerated Wed Aug 23 14:51:20 CST 2017
     */
    public void setStatus(Byte status) {
        this.status = status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task.StepCode
     *
     * @return the value of task.StepCode
     *
     * @mbggenerated Wed Aug 23 14:51:20 CST 2017
     */
    public String getStepCode() {
        return stepCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task.StepCode
     *
     * @param stepCode the value for task.StepCode
     *
     * @mbggenerated Wed Aug 23 14:51:20 CST 2017
     */
    public void setStepCode(String stepCode) {
        this.stepCode = stepCode == null ? null : stepCode.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task.CreateTime
     *
     * @return the value of task.CreateTime
     *
     * @mbggenerated Wed Aug 23 14:51:20 CST 2017
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task.CreateTime
     *
     * @param createTime the value for task.CreateTime
     *
     * @mbggenerated Wed Aug 23 14:51:20 CST 2017
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task.LastUpdateTime
     *
     * @return the value of task.LastUpdateTime
     *
     * @mbggenerated Wed Aug 23 14:51:20 CST 2017
     */
    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task.LastUpdateTime
     *
     * @param lastUpdateTime the value for task.LastUpdateTime
     *
     * @mbggenerated Wed Aug 23 14:51:20 CST 2017
     */
    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task
     *
     * @mbggenerated Wed Aug 23 14:51:20 CST 2017
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", uniqueId=").append(uniqueId);
        sb.append(", appId=").append(appId);
        sb.append(", accountNo=").append(accountNo);
        sb.append(", webSite=").append(webSite);
        sb.append(", bizType=").append(bizType);
        sb.append(", status=").append(status);
        sb.append(", stepCode=").append(stepCode);
        sb.append(", createTime=").append(createTime);
        sb.append(", lastUpdateTime=").append(lastUpdateTime);
        sb.append("]");
        return sb.toString();
    }
}