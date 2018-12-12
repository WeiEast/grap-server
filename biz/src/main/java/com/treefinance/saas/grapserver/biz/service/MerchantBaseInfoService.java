package com.treefinance.saas.grapserver.biz.service;

import com.treefinance.saas.grapserver.biz.domain.MerchantBaseInfo;

import javax.annotation.Nonnull;

/**
 * @author yh-treefinance on 2017/6/13.
 */
public interface MerchantBaseInfoService {

    /**
     * 根据appId获取商户信息
     */
    MerchantBaseInfo getMerchantBaseInfoByAppId(@Nonnull String appId);
}
