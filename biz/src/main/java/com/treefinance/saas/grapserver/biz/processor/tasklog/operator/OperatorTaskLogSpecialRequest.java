package com.treefinance.saas.grapserver.biz.processor.tasklog.operator;

import com.treefinance.saas.grapserver.common.model.dto.TaskDTO;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by haojiahong on 2017/11/2.
 */
public class OperatorTaskLogSpecialRequest implements Serializable {

    private static final long serialVersionUID = -2615611820116357124L;
    private TaskDTO taskDTO;//任务信息
    private String msg;//下游回传的任务日志消息
    private Date processTime;//任务日志消息时间

    public TaskDTO getTaskDTO() {
        return taskDTO;
    }

    public void setTaskDTO(TaskDTO taskDTO) {
        this.taskDTO = taskDTO;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Date getProcessTime() {
        return processTime;
    }

    public void setProcessTime(Date processTime) {
        this.processTime = processTime;
    }
}
