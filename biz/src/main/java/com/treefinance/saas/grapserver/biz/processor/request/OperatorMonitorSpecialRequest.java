package com.treefinance.saas.grapserver.biz.processor.request;

import com.treefinance.saas.grapserver.common.model.dto.TaskDTO;

import java.io.Serializable;

/**
 * Created by haojiahong on 2017/11/9.
 */
public class OperatorMonitorSpecialRequest implements Serializable {

    private static final long serialVersionUID = -1542398542334313658L;

    private Long taskId;//任务id

    private TaskDTO task;//任务信息

    private String groupCode;//运营商编码

    private String groupName;//运营商名称

    private Integer order;//操作顺序记录,如果上一步操作存在,则继续统计下一步数据,否则终止

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public TaskDTO getTask() {
        return task;
    }

    public void setTask(TaskDTO task) {
        this.task = task;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
}
