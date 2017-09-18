package com.treefinance.saas.grapserver.common.model.dto.moxie;

import com.treefinance.saas.grapserver.common.model.dto.TaskDTO;
import com.treefinance.saas.grapserver.common.model.dto.base.BaseDTO;

/**
 * Created by haojiahong on 2017/9/14.
 */
public class MoxieDirectiveDTO extends BaseDTO {

    private static final long serialVersionUID = 6201076878996673264L;

    private String directive;

    private Long taskId;

    private String moxieTaskId;

    private String remark;

    private TaskDTO task;

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

    public void setTask(TaskDTO taskDTO) {
        this.task = taskDTO;
    }

    public String getMoxieTaskId() {
        return moxieTaskId;
    }

    public void setMoxieTaskId(String moxieTaskId) {
        this.moxieTaskId = moxieTaskId;
    }
}
