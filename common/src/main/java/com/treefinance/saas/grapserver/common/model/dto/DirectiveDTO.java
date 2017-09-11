package com.treefinance.saas.grapserver.common.model.dto;

import com.treefinance.saas.grapserver.common.model.dto.base.BaseDTO;

/**
 * Created by yh-treefinance on 2017/7/6.
 */
public class DirectiveDTO extends BaseDTO {
    /**
     * 指令ID
     */
    private String directiveId;
    /**
     * 指令
     */
    private String directive;
    /**
     * 任务ID
     */
    private Long taskId;
    /**
     * 指令扩展信息：json格式
     */
    private String remark;
    /**
     * 任务详细信息
     */
    private TaskDTO task;


    public String getDirectiveId() {
        return directiveId;
    }

    public void setDirectiveId(String directiveId) {
        this.directiveId = directiveId;
    }

    public String getDirective() {
        return directive;
    }

    public void setDirective(String directive) {
        this.directive = directive;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public TaskDTO getTask() {
        return task;
    }

    public void setTask(TaskDTO task) {
        this.task = task;
    }
}
