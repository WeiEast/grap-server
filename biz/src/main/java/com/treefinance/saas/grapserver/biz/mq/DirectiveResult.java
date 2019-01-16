package com.treefinance.saas.grapserver.biz.mq;

import java.io.Serializable;

/**
 * @author 张琰佳
 * @since 3:57 PM 2019/1/15
 */
public class DirectiveResult implements Serializable {
    private Long taskId;
    private Directive directive;
    private String remark;

    public DirectiveResult(Long taskId, Directive directive) {
        this.taskId = taskId;
        this.directive = directive;
    }

    public DirectiveResult(Long taskId, Directive directive, String remark) {
        this.taskId = taskId;
        this.directive = directive;
        this.remark = remark;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Directive getDirective() {
        return directive;
    }

    public void setDirective(Directive directive) {
        this.directive = directive;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public static enum Directive {
        task_success, task_fail
    }
}
