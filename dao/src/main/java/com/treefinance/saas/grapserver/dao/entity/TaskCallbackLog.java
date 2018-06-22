package com.treefinance.saas.grapserver.dao.entity;

import java.io.Serializable;
import java.util.Date;

public class TaskCallbackLog implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_callback_log.id
     *
     * @mbg.generated Mon Jun 11 14:12:47 CST 2018
     */
    private Long id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_callback_log.taskId
     *
     * @mbg.generated Mon Jun 11 14:12:47 CST 2018
     */
    private Long taskId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_callback_log.configId
     *
     * @mbg.generated Mon Jun 11 14:12:47 CST 2018
     */
    private Long configId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_callback_log.type
     *
     * @mbg.generated Mon Jun 11 14:12:47 CST 2018
     */
    private Byte type;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_callback_log.url
     *
     * @mbg.generated Mon Jun 11 14:12:47 CST 2018
     */
    private String url;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_callback_log.requestParam
     *
     * @mbg.generated Mon Jun 11 14:12:47 CST 2018
     */
    private String requestParam;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_callback_log.responseData
     *
     * @mbg.generated Mon Jun 11 14:12:47 CST 2018
     */
    private String responseData;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_callback_log.consumeTime
     *
     * @mbg.generated Mon Jun 11 14:12:47 CST 2018
     */
    private Integer consumeTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_callback_log.httpCode
     *
     * @mbg.generated Mon Jun 11 14:12:47 CST 2018
     */
    private Integer httpCode;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_callback_log.callbackCode
     *
     * @mbg.generated Mon Jun 11 14:12:47 CST 2018
     */
    private String callbackCode;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_callback_log.callbackMsg
     *
     * @mbg.generated Mon Jun 11 14:12:47 CST 2018
     */
    private String callbackMsg;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_callback_log.failureReason
     *
     * @mbg.generated Mon Jun 11 14:12:47 CST 2018
     */
    private Byte failureReason;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_callback_log.createTime
     *
     * @mbg.generated Mon Jun 11 14:12:47 CST 2018
     */
    private Date createTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_callback_log.lastUpdateTime
     *
     * @mbg.generated Mon Jun 11 14:12:47 CST 2018
     */
    private Date lastUpdateTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table task_callback_log
     *
     * @mbg.generated Mon Jun 11 14:12:47 CST 2018
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_callback_log.id
     *
     * @return the value of task_callback_log.id
     *
     * @mbg.generated Mon Jun 11 14:12:47 CST 2018
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_callback_log.id
     *
     * @param id the value for task_callback_log.id
     *
     * @mbg.generated Mon Jun 11 14:12:47 CST 2018
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_callback_log.taskId
     *
     * @return the value of task_callback_log.taskId
     *
     * @mbg.generated Mon Jun 11 14:12:47 CST 2018
     */
    public Long getTaskId() {
        return taskId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_callback_log.taskId
     *
     * @param taskId the value for task_callback_log.taskId
     *
     * @mbg.generated Mon Jun 11 14:12:47 CST 2018
     */
    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_callback_log.configId
     *
     * @return the value of task_callback_log.configId
     *
     * @mbg.generated Mon Jun 11 14:12:47 CST 2018
     */
    public Long getConfigId() {
        return configId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_callback_log.configId
     *
     * @param configId the value for task_callback_log.configId
     *
     * @mbg.generated Mon Jun 11 14:12:47 CST 2018
     */
    public void setConfigId(Long configId) {
        this.configId = configId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_callback_log.type
     *
     * @return the value of task_callback_log.type
     *
     * @mbg.generated Mon Jun 11 14:12:47 CST 2018
     */
    public Byte getType() {
        return type;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_callback_log.type
     *
     * @param type the value for task_callback_log.type
     *
     * @mbg.generated Mon Jun 11 14:12:47 CST 2018
     */
    public void setType(Byte type) {
        this.type = type;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_callback_log.url
     *
     * @return the value of task_callback_log.url
     *
     * @mbg.generated Mon Jun 11 14:12:47 CST 2018
     */
    public String getUrl() {
        return url;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_callback_log.url
     *
     * @param url the value for task_callback_log.url
     *
     * @mbg.generated Mon Jun 11 14:12:47 CST 2018
     */
    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_callback_log.requestParam
     *
     * @return the value of task_callback_log.requestParam
     *
     * @mbg.generated Mon Jun 11 14:12:47 CST 2018
     */
    public String getRequestParam() {
        return requestParam;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_callback_log.requestParam
     *
     * @param requestParam the value for task_callback_log.requestParam
     *
     * @mbg.generated Mon Jun 11 14:12:47 CST 2018
     */
    public void setRequestParam(String requestParam) {
        this.requestParam = requestParam == null ? null : requestParam.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_callback_log.responseData
     *
     * @return the value of task_callback_log.responseData
     *
     * @mbg.generated Mon Jun 11 14:12:47 CST 2018
     */
    public String getResponseData() {
        return responseData;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_callback_log.responseData
     *
     * @param responseData the value for task_callback_log.responseData
     *
     * @mbg.generated Mon Jun 11 14:12:47 CST 2018
     */
    public void setResponseData(String responseData) {
        this.responseData = responseData == null ? null : responseData.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_callback_log.consumeTime
     *
     * @return the value of task_callback_log.consumeTime
     *
     * @mbg.generated Mon Jun 11 14:12:47 CST 2018
     */
    public Integer getConsumeTime() {
        return consumeTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_callback_log.consumeTime
     *
     * @param consumeTime the value for task_callback_log.consumeTime
     *
     * @mbg.generated Mon Jun 11 14:12:47 CST 2018
     */
    public void setConsumeTime(Integer consumeTime) {
        this.consumeTime = consumeTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_callback_log.httpCode
     *
     * @return the value of task_callback_log.httpCode
     *
     * @mbg.generated Mon Jun 11 14:12:47 CST 2018
     */
    public Integer getHttpCode() {
        return httpCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_callback_log.httpCode
     *
     * @param httpCode the value for task_callback_log.httpCode
     *
     * @mbg.generated Mon Jun 11 14:12:47 CST 2018
     */
    public void setHttpCode(Integer httpCode) {
        this.httpCode = httpCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_callback_log.callbackCode
     *
     * @return the value of task_callback_log.callbackCode
     *
     * @mbg.generated Mon Jun 11 14:12:47 CST 2018
     */
    public String getCallbackCode() {
        return callbackCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_callback_log.callbackCode
     *
     * @param callbackCode the value for task_callback_log.callbackCode
     *
     * @mbg.generated Mon Jun 11 14:12:47 CST 2018
     */
    public void setCallbackCode(String callbackCode) {
        this.callbackCode = callbackCode == null ? null : callbackCode.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_callback_log.callbackMsg
     *
     * @return the value of task_callback_log.callbackMsg
     *
     * @mbg.generated Mon Jun 11 14:12:47 CST 2018
     */
    public String getCallbackMsg() {
        return callbackMsg;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_callback_log.callbackMsg
     *
     * @param callbackMsg the value for task_callback_log.callbackMsg
     *
     * @mbg.generated Mon Jun 11 14:12:47 CST 2018
     */
    public void setCallbackMsg(String callbackMsg) {
        this.callbackMsg = callbackMsg == null ? null : callbackMsg.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_callback_log.failureReason
     *
     * @return the value of task_callback_log.failureReason
     *
     * @mbg.generated Mon Jun 11 14:12:47 CST 2018
     */
    public Byte getFailureReason() {
        return failureReason;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_callback_log.failureReason
     *
     * @param failureReason the value for task_callback_log.failureReason
     *
     * @mbg.generated Mon Jun 11 14:12:47 CST 2018
     */
    public void setFailureReason(Byte failureReason) {
        this.failureReason = failureReason;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_callback_log.createTime
     *
     * @return the value of task_callback_log.createTime
     *
     * @mbg.generated Mon Jun 11 14:12:47 CST 2018
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_callback_log.createTime
     *
     * @param createTime the value for task_callback_log.createTime
     *
     * @mbg.generated Mon Jun 11 14:12:47 CST 2018
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_callback_log.lastUpdateTime
     *
     * @return the value of task_callback_log.lastUpdateTime
     *
     * @mbg.generated Mon Jun 11 14:12:47 CST 2018
     */
    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_callback_log.lastUpdateTime
     *
     * @param lastUpdateTime the value for task_callback_log.lastUpdateTime
     *
     * @mbg.generated Mon Jun 11 14:12:47 CST 2018
     */
    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_callback_log
     *
     * @mbg.generated Mon Jun 11 14:12:47 CST 2018
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", taskId=").append(taskId);
        sb.append(", configId=").append(configId);
        sb.append(", type=").append(type);
        sb.append(", url=").append(url);
        sb.append(", requestParam=").append(requestParam);
        sb.append(", responseData=").append(responseData);
        sb.append(", consumeTime=").append(consumeTime);
        sb.append(", httpCode=").append(httpCode);
        sb.append(", callbackCode=").append(callbackCode);
        sb.append(", callbackMsg=").append(callbackMsg);
        sb.append(", failureReason=").append(failureReason);
        sb.append(", createTime=").append(createTime);
        sb.append(", lastUpdateTime=").append(lastUpdateTime);
        sb.append("]");
        return sb.toString();
    }
}