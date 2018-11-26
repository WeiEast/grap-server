package com.treefinance.saas.grapserver.biz.service;

import com.treefinance.saas.grapserver.biz.adapter.GetMerchantBaseInfoAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.treefinance.saas.grapserver.biz.adapter.GetMerchantBaseInfoAdapterImpl;
import com.treefinance.saas.grapserver.dao.entity.MerchantBaseInfo;

/**
 * @author yh-treefinance on 2017/6/13.
 */
@Service
public class MerchantBaseInfoService {

    @Autowired
    private GetMerchantBaseInfoAdapter getMerchantBaseInfoAdapter;

    /**
     * 根据appId获取商户信息
     */
    public MerchantBaseInfo getMerchantBaseInfoByAppId(String appId) {
       return getMerchantBaseInfoAdapter.getBaseInfoByAppId(appId);
    }

}
