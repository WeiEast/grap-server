package com.treefinance.saas.grapserver.biz.processor.burypoint.operator;

import java.io.Serializable;

/**
 * @author haojiahong
 * @date 2017/11/2
 */
public class OperatorBuryPointSpecialRequest implements Serializable {

    private static final long serialVersionUID = 5784266964329534677L;
    private String extra;//需要特殊处理的json字符串
    private Long taskId;//任务id
    private String appId;//appId
    private String code;//埋点编码

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
