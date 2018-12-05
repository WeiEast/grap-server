package com.treefinance.saas.grapserver.biz.adapter;

import com.treefinance.saas.grapserver.biz.dto.AppBizLicense;

import java.util.List;

/**
 * @author:guoguoyun
 * @date:Created in 2018/11/26上午11:37
 */
public interface QueryAppBizLicenseAdapter {

    List<AppBizLicense> queryAppBizLicenseByAppId(String appid);

    List<AppBizLicense> queryAllAppBizLicense();

}
