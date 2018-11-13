package com.treefinance.saas.grapserver.common.model.dto;

import com.treefinance.saas.grapserver.common.model.dto.base.BaseDTO;

/**
 * @author yh-treefinance on 2017/7/7.
 */
public class CallBackLicenseDTO extends BaseDTO {

    private static final long serialVersionUID = -7728805234789955772L;

    /**
     * ID
     */
    private Integer callBackConfigId;

    /**
     * 商户ID
     */
    private String appId;

    /**
     * AES 数据密钥
     */
    private String dataSecretKey;

    @Override
    public String toString() {
        return "CallBackLicenseDTO{" +
                "callBackConfigId=" + callBackConfigId +
                ", appId='" + appId + '\'' +
                ", dataSecretKey='" + dataSecretKey + '\'' +
                '}';
    }

    public Integer getCallBackConfigId() {
        return callBackConfigId;
    }

    public void setCallBackConfigId(Integer callBackConfigId) {
        this.callBackConfigId = callBackConfigId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getDataSecretKey() {
        return dataSecretKey;
    }

    public void setDataSecretKey(String dataSecretKey) {
        this.dataSecretKey = dataSecretKey;
    }

}
