package com.treefinance.saas.grapserver.biz.adapter;

import com.treefinance.saas.grapserver.dao.entity.AppLicense;

/**
 * @author:guoguoyun
 * @date:Created in 2018/11/26上午11:34
 */
public interface GetAppLicenseAdapter {
     AppLicense getAppLicenseByAppId(String appid);
}
