package com.treefinance.saas.grapserver.biz.service;

import com.treefinance.saas.grapserver.biz.domain.MerchantBaseInfo;
import com.treefinance.saas.grapserver.context.component.AbstractService;
import com.treefinance.saas.grapserver.manager.MerchantInfoManager;
import com.treefinance.saas.grapserver.manager.domain.BaseMerchantInfoBO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;

/**
 * @author yh-treefinance on 2017/6/13.
 */
@Service
public class MerchantBaseInfoService extends AbstractService {

    @Autowired
    private MerchantInfoManager merchantInfoManager;

    /**
     * 根据appId获取商户信息
     */
    public MerchantBaseInfo getMerchantBaseInfoByAppId(@Nonnull String appId) {
        BaseMerchantInfoBO info = merchantInfoManager.getBaseInfoByAppId(appId);

        return convert(info, MerchantBaseInfo.class);
    }

}
