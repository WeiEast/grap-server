package com.treefinance.saas.grapserver.biz.mq;

/**
 * @author 张琰佳
 * @since 4:02 PM 2019/1/15
 */
public class SuccessRemark {
    private long taskId;
    private String type;
    private long expirationTime;
    private String dataUrl;
    private long dataSize;
    private long timestamp;
    /**
     * 1:工商页面数据 无需爬取
     */
    private Byte crawlerStatus;

    public SuccessRemark() {
    }

    public SuccessRemark(long taskId, String type, long expirationTime, String dataUrl, long dataSize, long timestamp,Byte crawlerStatus) {
        this.taskId = taskId;
        this.type = type;
        this.expirationTime = expirationTime;
        this.dataUrl = dataUrl;
        this.dataSize = dataSize;
        this.timestamp = timestamp;
        this.crawlerStatus=crawlerStatus;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }

    public String getDataUrl() {
        return dataUrl;
    }

    public void setDataUrl(String dataUrl) {
        this.dataUrl = dataUrl;
    }

    public long getDataSize() {
        return dataSize;
    }

    public void setDataSize(long dataSize) {
        this.dataSize = dataSize;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Byte getCrawlerStatus() {
        return crawlerStatus;
    }

    public void setCrawlerStatus(Byte crawlerStatus) {
        this.crawlerStatus = crawlerStatus;
    }
}
