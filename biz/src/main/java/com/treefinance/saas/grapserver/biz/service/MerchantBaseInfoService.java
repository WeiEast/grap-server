package com.treefinance.saas.grapserver.biz.service;

import com.treefinance.saas.grapserver.common.utils.DataConverterUtils;
import com.treefinance.saas.grapserver.dao.entity.MerchantBaseInfo;
import com.treefinance.saas.merchant.center.facade.request.console.GetMerchantByAppIdRequest;
import com.treefinance.saas.merchant.center.facade.result.console.MerchantBaseInfoResult;
import com.treefinance.saas.merchant.center.facade.result.console.MerchantResult;
import com.treefinance.saas.merchant.center.facade.service.MerchantBaseInfoFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author yh-treefinance on 2017/6/13.
 */
@Service
public class MerchantBaseInfoService {

    @Autowired
    private MerchantBaseInfoFacade merchantBaseInfoFacade;

    /**
     * 根据appId获取商户信息
     */
    public MerchantBaseInfo getMerchantBaseInfoByAppId(String appId) {
        GetMerchantByAppIdRequest request =  new GetMerchantByAppIdRequest();
        request.setAppId(appId);
        MerchantResult<List<MerchantBaseInfoResult>> merchantResult =merchantBaseInfoFacade.getBaseInfoByAppId(request);
        List<MerchantBaseInfo> list = DataConverterUtils.convert(merchantResult.getData(),MerchantBaseInfo.class);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

}
