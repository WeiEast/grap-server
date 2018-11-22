package com.treefinance.saas.grapserver.common.model.dto;

import com.treefinance.saas.grapserver.common.model.dto.base.BaseDTO;

/**
 * @author henengqiang modefied on:
 * @date 2018/11/13
 */
public class AppCallbackConfigDTO extends BaseDTO {

    private static final long serialVersionUID = -3832771592115022214L;

    /** */
    private Integer id;

    /** */
    private String appId;

    /** */
    private Byte bizType;

    /**  */
    private String receiver;

    /** */
    private Byte version;

    /** */
    private Byte isNewKey;

    /** */
    private String url;

    /** */
    private Byte retryTimes;

    /** */
    private Byte timeOut;

    /** */
    private String remark;

    /** */
    private Byte isNotifyCancel;

    /** */
    private Byte isNotifyFailure;

    /** */
    private Byte isNotifySuccess;

    /** */
    private Byte notifyModel;

    private Byte dataType;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Byte getBizType() {
        return bizType;
    }

    public void setBizType(Byte bizType) {
        this.bizType = bizType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Byte getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(Byte retryTimes) {
        this.retryTimes = retryTimes;
    }

    public Byte getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(Byte timeOut) {
        this.timeOut = timeOut;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Byte getIsNotifyCancel() {
        return isNotifyCancel;
    }

    public void setIsNotifyCancel(Byte isNotifyCancel) {
        this.isNotifyCancel = isNotifyCancel;
    }

    public Byte getIsNotifyFailure() {
        return isNotifyFailure;
    }

    public void setIsNotifyFailure(Byte isNotifyFailure) {
        this.isNotifyFailure = isNotifyFailure;
    }

    public Byte getIsNotifySuccess() {
        return isNotifySuccess;
    }

    public void setIsNotifySuccess(Byte isNotifySuccess) {
        this.isNotifySuccess = isNotifySuccess;
    }

    public Byte getNotifyModel() {
        return notifyModel;
    }

    public void setNotifyModel(Byte notifyModel) {
        this.notifyModel = notifyModel;
    }

    public Byte getVersion() {
        return version;
    }

    public void setVersion(Byte version) {
        this.version = version;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public Byte getIsNewKey() {
        return isNewKey;
    }

    public void setIsNewKey(Byte isNewKey) {
        this.isNewKey = isNewKey;
    }

    public Byte getDataType() {
        return dataType;
    }

    public void setDataType(Byte dataType) {
        this.dataType = dataType;
    }

    @Override
    public String toString() {
        return "AppCallbackConfigDTO{" +
                "id=" + id +
                ", appId='" + appId + '\'' +
                ", bizType=" + bizType +
                ", receiver='" + receiver + '\'' +
                ", version=" + version +
                ", isNewKey=" + isNewKey +
                ", url='" + url + '\'' +
                ", retryTimes=" + retryTimes +
                ", timeOut=" + timeOut +
                ", remark='" + remark + '\'' +
                ", isNotifyCancel=" + isNotifyCancel +
                ", isNotifyFailure=" + isNotifyFailure +
                ", isNotifySuccess=" + isNotifySuccess +
                ", notifyModel=" + notifyModel +
                ", dataType=" + dataType +
                '}';
    }

}