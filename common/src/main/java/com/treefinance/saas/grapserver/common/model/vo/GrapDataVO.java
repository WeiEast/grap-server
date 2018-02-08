package com.treefinance.saas.grapserver.common.model.vo;

import java.io.Serializable;

/**
 * Created by yh-treefinance on 2018/2/7.
 */
public class GrapDataVO implements Serializable {

    private String appid;

    private Long taskId;

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
