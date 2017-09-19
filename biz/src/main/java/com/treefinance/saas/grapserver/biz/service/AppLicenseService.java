package com.treefinance.saas.grapserver.biz.service;


import com.treefinance.saas.grapserver.dao.entity.AppLicense;

/**
 * 查询数据密钥
 */
public interface AppLicenseService {

    /**
     * 获取授权信息
     *
     * @param appId
     * @return
     */
    AppLicense getAppLicense(String appId);

    /**
     * 查询数据密钥
     *
     * @param appId
     * @return
     */
    String getDataKey(String appId);
}
