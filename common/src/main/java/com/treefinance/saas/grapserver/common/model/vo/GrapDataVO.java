package com.treefinance.saas.grapserver.common.model.vo;

import java.io.Serializable;

/**
 * @author yh-treefinance on 2018/2/7.
 */
public class GrapDataVO implements Serializable {

    private static final long serialVersionUID = 1395289788598719233L;

    private String appid;

    private Long taskId;

    @Override
    public String toString() {
        return "GrapDataVO{" +
                "appid='" + appid + '\'' +
                ", taskId=" + taskId +
                '}';
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

}
