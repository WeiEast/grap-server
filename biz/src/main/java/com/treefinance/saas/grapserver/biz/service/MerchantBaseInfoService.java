package com.treefinance.saas.grapserver.biz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.treefinance.saas.grapserver.biz.common.GetMerchantBaseInfoConverter;
import com.treefinance.saas.grapserver.dao.entity.MerchantBaseInfo;

/**
 * @author yh-treefinance on 2017/6/13.
 */
@Service
public class MerchantBaseInfoService {

    @Autowired
    private GetMerchantBaseInfoConverter getMerchantBaseInfoConverter;

    /**
     * 根据appId获取商户信息
     */
    public MerchantBaseInfo getMerchantBaseInfoByAppId(String appId) {
       return getMerchantBaseInfoConverter.getBaseInfoByAppId(appId);
    }

}
