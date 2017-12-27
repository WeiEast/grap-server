package com.treefinance.saas.grapserver.common.model.dto;

import java.io.Serializable;

/**
 * 异步数据数据抓取
 * Created by yh-treefinance on 2017/12/19.
 */
public class AsycGrapDTO implements Serializable {
    // 任务ID
    private Long taskId;
    // 用户ID
    private String uniqueId;
    // 状态: 1:成功，2:失败
    private Integer status;
    // 错误消息
    private String errorMsg;
    // 数据类型:0-主流程，1-收货地址，2-运营商流量
    private Integer dataType;
    // 数据地址（洗数提供），成功有值，失败为空
    private String dataUrl;
    // 数据大小（洗数提供），成功有值，失败为空
    private Long dataSize;
    // 超时时间（洗数提供），成功有值，失败为空
    private Long expiredTime;
    // 时间戳
    private Long timestamp;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Integer getDataType() {
        return dataType;
    }

    public void setDataType(Integer dataType) {
        this.dataType = dataType;
    }

    public String getDataUrl() {
        return dataUrl;
    }

    public void setDataUrl(String dataUrl) {
        this.dataUrl = dataUrl;
    }

    public Long getDataSize() {
        return dataSize;
    }

    public void setDataSize(Long dataSize) {
        this.dataSize = dataSize;
    }

    public Long getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(Long expiredTime) {
        this.expiredTime = expiredTime;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
