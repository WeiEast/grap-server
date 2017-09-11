package com.treefinance.saas.grapserver.biz.service;


/**
 * 查询数据密钥
 */
public interface AppLicenseService {

    /**
     * 查询数据密钥
     * @param appId
     * @return
     */
    String getDataKey(String appId);
}
