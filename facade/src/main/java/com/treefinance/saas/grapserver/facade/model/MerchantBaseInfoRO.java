package com.treefinance.saas.grapserver.facade.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by haojiahong on 2017/10/20.
 */
public class MerchantBaseInfoRO implements Serializable {
    private static final long serialVersionUID = 6394903432168886204L;

    /**
     * appId
     */
    private String appId;

    /**
     * 用户Id
     */
    private String uniqueId;

    /**
     * app名称
     */
    private String appName;

    /**
     * 公司简称
     */
    private String chName;

    /**
     * 公司名称
     */
    private String company;

    /**
     * 业务1
     */
    private String bussiness;

    /**
     * 业务2
     */
    private String bussiness2;

    /**
     * 商户地址
     */
    private String address;

    /**
     * 联系人
     */
    private String contactPerson;

    /**
     * 联系方式
     */
    private String contactValue;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 最近修改时间
     */
    private Date lastUpdateTime;


    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getChName() {
        return chName;
    }

    public void setChName(String chName) {
        this.chName = chName;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getBussiness() {
        return bussiness;
    }

    public void setBussiness(String bussiness) {
        this.bussiness = bussiness;
    }

    public String getBussiness2() {
        return bussiness2;
    }

    public void setBussiness2(String bussiness2) {
        this.bussiness2 = bussiness2;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getContactValue() {
        return contactValue;
    }

    public void setContactValue(String contactValue) {
        this.contactValue = contactValue;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}
