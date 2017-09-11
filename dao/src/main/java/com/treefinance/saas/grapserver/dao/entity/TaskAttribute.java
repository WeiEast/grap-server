package com.treefinance.saas.grapserver.dao.entity;

import java.io.Serializable;
import java.util.Date;

public class TaskAttribute implements Serializable {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_attribute.Id
     *
     * @mbggenerated Wed Jul 05 15:00:03 CST 2017
     */
    private Long id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_attribute.TaskId
     *
     * @mbggenerated Wed Jul 05 15:00:03 CST 2017
     */
    private Long taskId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_attribute.name
     *
     * @mbggenerated Wed Jul 05 15:00:03 CST 2017
     */
    private String name;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_attribute.value
     *
     * @mbggenerated Wed Jul 05 15:00:03 CST 2017
     */
    private String value;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_attribute.CreateTime
     *
     * @mbggenerated Wed Jul 05 15:00:03 CST 2017
     */
    private Date createTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_attribute.LastUpdateTime
     *
     * @mbggenerated Wed Jul 05 15:00:03 CST 2017
     */
    private Date lastUpdateTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table task_attribute
     *
     * @mbggenerated Wed Jul 05 15:00:03 CST 2017
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_attribute.Id
     *
     * @return the value of task_attribute.Id
     *
     * @mbggenerated Wed Jul 05 15:00:03 CST 2017
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_attribute.Id
     *
     * @param id the value for task_attribute.Id
     *
     * @mbggenerated Wed Jul 05 15:00:03 CST 2017
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_attribute.TaskId
     *
     * @return the value of task_attribute.TaskId
     *
     * @mbggenerated Wed Jul 05 15:00:03 CST 2017
     */
    public Long getTaskId() {
        return taskId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_attribute.TaskId
     *
     * @param taskId the value for task_attribute.TaskId
     *
     * @mbggenerated Wed Jul 05 15:00:03 CST 2017
     */
    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_attribute.name
     *
     * @return the value of task_attribute.name
     *
     * @mbggenerated Wed Jul 05 15:00:03 CST 2017
     */
    public String getName() {
        return name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_attribute.name
     *
     * @param name the value for task_attribute.name
     *
     * @mbggenerated Wed Jul 05 15:00:03 CST 2017
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_attribute.value
     *
     * @return the value of task_attribute.value
     *
     * @mbggenerated Wed Jul 05 15:00:03 CST 2017
     */
    public String getValue() {
        return value;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_attribute.value
     *
     * @param value the value for task_attribute.value
     *
     * @mbggenerated Wed Jul 05 15:00:03 CST 2017
     */
    public void setValue(String value) {
        this.value = value == null ? null : value.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_attribute.CreateTime
     *
     * @return the value of task_attribute.CreateTime
     *
     * @mbggenerated Wed Jul 05 15:00:03 CST 2017
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_attribute.CreateTime
     *
     * @param createTime the value for task_attribute.CreateTime
     *
     * @mbggenerated Wed Jul 05 15:00:03 CST 2017
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_attribute.LastUpdateTime
     *
     * @return the value of task_attribute.LastUpdateTime
     *
     * @mbggenerated Wed Jul 05 15:00:03 CST 2017
     */
    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_attribute.LastUpdateTime
     *
     * @param lastUpdateTime the value for task_attribute.LastUpdateTime
     *
     * @mbggenerated Wed Jul 05 15:00:03 CST 2017
     */
    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_attribute
     *
     * @mbggenerated Wed Jul 05 15:00:03 CST 2017
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", taskId=").append(taskId);
        sb.append(", name=").append(name);
        sb.append(", value=").append(value);
        sb.append(", createTime=").append(createTime);
        sb.append(", lastUpdateTime=").append(lastUpdateTime);
        sb.append("]");
        return sb.toString();
    }
}