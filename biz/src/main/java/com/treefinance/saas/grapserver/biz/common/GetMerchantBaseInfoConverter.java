package com.treefinance.saas.grapserver.biz.common;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.treefinance.saas.grapserver.common.utils.DataConverterUtils;
import com.treefinance.saas.grapserver.dao.entity.MerchantBaseInfo;
import com.treefinance.saas.merchant.facade.request.console.GetMerchantByAppIdRequest;
import com.treefinance.saas.merchant.facade.result.console.MerchantBaseInfoResult;
import com.treefinance.saas.merchant.facade.result.console.MerchantResult;
import com.treefinance.saas.merchant.facade.service.MerchantBaseInfoFacade;

/**
 * @author:guoguoyun
 * @date:Created in 2018/11/16下午3:00
 */
@Component
public class GetMerchantBaseInfoConverter {


    @Resource
    private MerchantBaseInfoFacade merchantBaseInfoFacade;

    public MerchantBaseInfo getBaseInfoByAppId(String appId) {
        GetMerchantByAppIdRequest request = new GetMerchantByAppIdRequest();
        request.setAppId(appId);
        MerchantResult<List<MerchantBaseInfoResult>> merchantResult = merchantBaseInfoFacade.queryMerchantBaseByAppId(request);
        List<MerchantBaseInfo> list = DataConverterUtils.convert(merchantResult.getData(), MerchantBaseInfo.class);
        return CollectionUtils.isEmpty(list) ? null : list.get(0);
    }
        
}
