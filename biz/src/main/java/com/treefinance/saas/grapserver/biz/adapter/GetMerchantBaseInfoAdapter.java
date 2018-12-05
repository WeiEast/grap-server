package com.treefinance.saas.grapserver.biz.adapter;

import com.treefinance.saas.grapserver.dao.entity.MerchantBaseInfo;

/**
 * @author:guoguoyun
 * @date:Created in 2018/11/26上午11:36
 */
public interface GetMerchantBaseInfoAdapter {

    MerchantBaseInfo getBaseInfoByAppId(String appId);

}
