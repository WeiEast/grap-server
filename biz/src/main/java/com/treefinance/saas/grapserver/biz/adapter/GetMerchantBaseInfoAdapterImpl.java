package com.treefinance.saas.grapserver.biz.adapter;

import com.treefinance.saas.grapserver.common.utils.DataConverterUtils;
import com.treefinance.saas.grapserver.biz.dto.MerchantBaseInfo;
import com.treefinance.saas.merchant.facade.request.console.GetMerchantByAppIdRequest;
import com.treefinance.saas.merchant.facade.result.console.MerchantBaseInfoResult;
import com.treefinance.saas.merchant.facade.result.console.MerchantResult;
import com.treefinance.saas.merchant.facade.service.MerchantBaseInfoFacade;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;

import java.util.List;

/**
 * @author:guoguoyun
 * @date:Created in 2018/11/16下午3:00
 */
@Component
public class GetMerchantBaseInfoAdapterImpl implements  GetMerchantBaseInfoAdapter{


    @Resource
    private MerchantBaseInfoFacade merchantBaseInfoFacade;

    @Override
    public MerchantBaseInfo getBaseInfoByAppId(String appId) {
        GetMerchantByAppIdRequest request = new GetMerchantByAppIdRequest();
        request.setAppId(appId);
        MerchantResult<List<MerchantBaseInfoResult>> merchantResult = merchantBaseInfoFacade.queryMerchantBaseByAppId(request);
        List<MerchantBaseInfo> list = DataConverterUtils.convert(merchantResult.getData(), MerchantBaseInfo.class);
        return CollectionUtils.isEmpty(list) ? null : list.get(0);
    }
        
}
