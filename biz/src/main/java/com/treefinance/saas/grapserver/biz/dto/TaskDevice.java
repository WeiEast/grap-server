/*
 * Copyright © 2015 - 2017 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.treefinance.saas.grapserver.biz.dto;

import java.io.Serializable;
import java.util.Date;

public class TaskDevice implements Serializable {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_device.Id
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    private Long id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_device.TaskId
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    private Long taskId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_device.ProvinceName
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    private String provinceName;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_device.CityName
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    private String cityName;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_device.PositionData
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    private String positionData;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_device.PositionX
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    private Double positionX;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_device.PositionY
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    private Double positionY;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_device.AppVersion
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    private String appVersion;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_device.platformId
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    private Integer platformId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_device.PhoneBrand
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    private String phoneBrand;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_device.PhoneModel
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    private String phoneModel;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_device.OperatorName
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    private String operatorName;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_device.PhoneVersion
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    private String phoneVersion;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_device.NetModel
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    private String netModel;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_device.IpAddress
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    private String ipAddress;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_device.IpPosition
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    private String ipPosition;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_device.Idfa
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    private String idfa;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_device.Openudid
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    private String openudid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_device.Imei
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    private String imei;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_device.MacAddress
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    private String macAddress;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_device.CreateTime
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    private Date createTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_device.LastUpdateTime
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    private Date lastUpdateTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_device.Comment
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    private String comment;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_device.OperatorCode
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    private String operatorCode;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_device.Cpuabi
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    private String cpuabi;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_device.IsEmulator
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    private Boolean isEmulator;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_device.IsJailbreak
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    private Boolean isJailbreak;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_device.Imsi
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    private String imsi;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table task_device
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_device.Id
     *
     * @return the value of task_device.Id
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_device.Id
     *
     * @param id the value for task_device.Id
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_device.TaskId
     *
     * @return the value of task_device.TaskId
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    public Long getTaskId() {
        return taskId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_device.TaskId
     *
     * @param taskId the value for task_device.TaskId
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_device.ProvinceName
     *
     * @return the value of task_device.ProvinceName
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    public String getProvinceName() {
        return provinceName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_device.ProvinceName
     *
     * @param provinceName the value for task_device.ProvinceName
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName == null ? null : provinceName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_device.CityName
     *
     * @return the value of task_device.CityName
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    public String getCityName() {
        return cityName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_device.CityName
     *
     * @param cityName the value for task_device.CityName
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    public void setCityName(String cityName) {
        this.cityName = cityName == null ? null : cityName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_device.PositionData
     *
     * @return the value of task_device.PositionData
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    public String getPositionData() {
        return positionData;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_device.PositionData
     *
     * @param positionData the value for task_device.PositionData
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    public void setPositionData(String positionData) {
        this.positionData = positionData == null ? null : positionData.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_device.PositionX
     *
     * @return the value of task_device.PositionX
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    public Double getPositionX() {
        return positionX;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_device.PositionX
     *
     * @param positionX the value for task_device.PositionX
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    public void setPositionX(Double positionX) {
        this.positionX = positionX;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_device.PositionY
     *
     * @return the value of task_device.PositionY
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    public Double getPositionY() {
        return positionY;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_device.PositionY
     *
     * @param positionY the value for task_device.PositionY
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    public void setPositionY(Double positionY) {
        this.positionY = positionY;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_device.AppVersion
     *
     * @return the value of task_device.AppVersion
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    public String getAppVersion() {
        return appVersion;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_device.AppVersion
     *
     * @param appVersion the value for task_device.AppVersion
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion == null ? null : appVersion.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_device.platformId
     *
     * @return the value of task_device.platformId
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    public Integer getPlatformId() {
        return platformId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_device.platformId
     *
     * @param platformId the value for task_device.platformId
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    public void setPlatformId(Integer platformId) {
        this.platformId = platformId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_device.PhoneBrand
     *
     * @return the value of task_device.PhoneBrand
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    public String getPhoneBrand() {
        return phoneBrand;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_device.PhoneBrand
     *
     * @param phoneBrand the value for task_device.PhoneBrand
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    public void setPhoneBrand(String phoneBrand) {
        this.phoneBrand = phoneBrand == null ? null : phoneBrand.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_device.PhoneModel
     *
     * @return the value of task_device.PhoneModel
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    public String getPhoneModel() {
        return phoneModel;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_device.PhoneModel
     *
     * @param phoneModel the value for task_device.PhoneModel
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    public void setPhoneModel(String phoneModel) {
        this.phoneModel = phoneModel == null ? null : phoneModel.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_device.OperatorName
     *
     * @return the value of task_device.OperatorName
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    public String getOperatorName() {
        return operatorName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_device.OperatorName
     *
     * @param operatorName the value for task_device.OperatorName
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName == null ? null : operatorName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_device.PhoneVersion
     *
     * @return the value of task_device.PhoneVersion
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    public String getPhoneVersion() {
        return phoneVersion;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_device.PhoneVersion
     *
     * @param phoneVersion the value for task_device.PhoneVersion
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    public void setPhoneVersion(String phoneVersion) {
        this.phoneVersion = phoneVersion == null ? null : phoneVersion.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_device.NetModel
     *
     * @return the value of task_device.NetModel
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    public String getNetModel() {
        return netModel;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_device.NetModel
     *
     * @param netModel the value for task_device.NetModel
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    public void setNetModel(String netModel) {
        this.netModel = netModel == null ? null : netModel.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_device.IpAddress
     *
     * @return the value of task_device.IpAddress
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_device.IpAddress
     *
     * @param ipAddress the value for task_device.IpAddress
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress == null ? null : ipAddress.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_device.IpPosition
     *
     * @return the value of task_device.IpPosition
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    public String getIpPosition() {
        return ipPosition;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_device.IpPosition
     *
     * @param ipPosition the value for task_device.IpPosition
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    public void setIpPosition(String ipPosition) {
        this.ipPosition = ipPosition == null ? null : ipPosition.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_device.Idfa
     *
     * @return the value of task_device.Idfa
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    public String getIdfa() {
        return idfa;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_device.Idfa
     *
     * @param idfa the value for task_device.Idfa
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    public void setIdfa(String idfa) {
        this.idfa = idfa == null ? null : idfa.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_device.Openudid
     *
     * @return the value of task_device.Openudid
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    public String getOpenudid() {
        return openudid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_device.Openudid
     *
     * @param openudid the value for task_device.Openudid
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    public void setOpenudid(String openudid) {
        this.openudid = openudid == null ? null : openudid.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_device.Imei
     *
     * @return the value of task_device.Imei
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    public String getImei() {
        return imei;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_device.Imei
     *
     * @param imei the value for task_device.Imei
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    public void setImei(String imei) {
        this.imei = imei == null ? null : imei.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_device.MacAddress
     *
     * @return the value of task_device.MacAddress
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    public String getMacAddress() {
        return macAddress;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_device.MacAddress
     *
     * @param macAddress the value for task_device.MacAddress
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress == null ? null : macAddress.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_device.CreateTime
     *
     * @return the value of task_device.CreateTime
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_device.CreateTime
     *
     * @param createTime the value for task_device.CreateTime
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_device.LastUpdateTime
     *
     * @return the value of task_device.LastUpdateTime
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_device.LastUpdateTime
     *
     * @param lastUpdateTime the value for task_device.LastUpdateTime
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_device.Comment
     *
     * @return the value of task_device.Comment
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    public String getComment() {
        return comment;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_device.Comment
     *
     * @param comment the value for task_device.Comment
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    public void setComment(String comment) {
        this.comment = comment == null ? null : comment.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_device.OperatorCode
     *
     * @return the value of task_device.OperatorCode
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    public String getOperatorCode() {
        return operatorCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_device.OperatorCode
     *
     * @param operatorCode the value for task_device.OperatorCode
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    public void setOperatorCode(String operatorCode) {
        this.operatorCode = operatorCode == null ? null : operatorCode.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_device.Cpuabi
     *
     * @return the value of task_device.Cpuabi
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    public String getCpuabi() {
        return cpuabi;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_device.Cpuabi
     *
     * @param cpuabi the value for task_device.Cpuabi
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    public void setCpuabi(String cpuabi) {
        this.cpuabi = cpuabi == null ? null : cpuabi.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_device.IsEmulator
     *
     * @return the value of task_device.IsEmulator
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    public Boolean getIsEmulator() {
        return isEmulator;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_device.IsEmulator
     *
     * @param isEmulator the value for task_device.IsEmulator
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    public void setIsEmulator(Boolean isEmulator) {
        this.isEmulator = isEmulator;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_device.IsJailbreak
     *
     * @return the value of task_device.IsJailbreak
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    public Boolean getIsJailbreak() {
        return isJailbreak;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_device.IsJailbreak
     *
     * @param isJailbreak the value for task_device.IsJailbreak
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    public void setIsJailbreak(Boolean isJailbreak) {
        this.isJailbreak = isJailbreak;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_device.Imsi
     *
     * @return the value of task_device.Imsi
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    public String getImsi() {
        return imsi;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_device.Imsi
     *
     * @param imsi the value for task_device.Imsi
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    public void setImsi(String imsi) {
        this.imsi = imsi == null ? null : imsi.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_device
     *
     * @mbggenerated Wed May 03 19:48:10 CST 2017
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", taskId=").append(taskId);
        sb.append(", provinceName=").append(provinceName);
        sb.append(", cityName=").append(cityName);
        sb.append(", positionData=").append(positionData);
        sb.append(", positionX=").append(positionX);
        sb.append(", positionY=").append(positionY);
        sb.append(", appVersion=").append(appVersion);
        sb.append(", platformId=").append(platformId);
        sb.append(", phoneBrand=").append(phoneBrand);
        sb.append(", phoneModel=").append(phoneModel);
        sb.append(", operatorName=").append(operatorName);
        sb.append(", phoneVersion=").append(phoneVersion);
        sb.append(", netModel=").append(netModel);
        sb.append(", ipAddress=").append(ipAddress);
        sb.append(", ipPosition=").append(ipPosition);
        sb.append(", idfa=").append(idfa);
        sb.append(", openudid=").append(openudid);
        sb.append(", imei=").append(imei);
        sb.append(", macAddress=").append(macAddress);
        sb.append(", createTime=").append(createTime);
        sb.append(", lastUpdateTime=").append(lastUpdateTime);
        sb.append(", comment=").append(comment);
        sb.append(", operatorCode=").append(operatorCode);
        sb.append(", cpuabi=").append(cpuabi);
        sb.append(", isEmulator=").append(isEmulator);
        sb.append(", isJailbreak=").append(isJailbreak);
        sb.append(", imsi=").append(imsi);
        sb.append("]");
        return sb.toString();
    }
}