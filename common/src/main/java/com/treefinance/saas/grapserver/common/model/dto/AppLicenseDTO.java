package com.treefinance.saas.grapserver.common.model.dto;

import com.treefinance.saas.grapserver.common.model.dto.base.BaseDTO;

/**
 * Created by yh-treefinance on 2017/7/7.
 */
public class AppLicenseDTO extends BaseDTO{
    /** ID*/
    private Integer id;

    /** 商户ID */
    private String appId;

    /** SDK公钥 */
    private String sdkPublicKey;

    /** SDK私钥 */
    private String sdkPrivateKey;

    /** 回调用公钥 */
    private String serverPublicKey;

    /** 回调用私钥 */
    private String serverPrivateKey;

    /** AES 数据密钥*/
    private String dataSecretKey;

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

    public String getSdkPublicKey() {
        return sdkPublicKey;
    }

    public void setSdkPublicKey(String sdkPublicKey) {
        this.sdkPublicKey = sdkPublicKey;
    }

    public String getSdkPrivateKey() {
        return sdkPrivateKey;
    }

    public void setSdkPrivateKey(String sdkPrivateKey) {
        this.sdkPrivateKey = sdkPrivateKey;
    }

    public String getServerPublicKey() {
        return serverPublicKey;
    }

    public void setServerPublicKey(String serverPublicKey) {
        this.serverPublicKey = serverPublicKey;
    }

    public String getServerPrivateKey() {
        return serverPrivateKey;
    }

    public void setServerPrivateKey(String serverPrivateKey) {
        this.serverPrivateKey = serverPrivateKey;
    }

    public String getDataSecretKey() {
        return dataSecretKey;
    }

    public void setDataSecretKey(String dataSecretKey) {
        this.dataSecretKey = dataSecretKey;
    }
}
