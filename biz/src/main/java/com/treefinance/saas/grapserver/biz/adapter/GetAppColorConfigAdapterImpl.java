package com.treefinance.saas.grapserver.biz.adapter;

import com.treefinance.saas.merchant.facade.request.grapserver.GetAppColorConfigRequest;
import com.treefinance.saas.merchant.facade.result.console.MerchantResult;
import com.treefinance.saas.merchant.facade.result.grapsever.AppColorConfigResult;
import com.treefinance.saas.merchant.facade.service.AppColorConfigFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author:guoguoyun
 * @date:Created in 2018/11/16上午10:39
 */
@Component
public class GetAppColorConfigAdapterImpl  implements GetAppColorConfigAdapter {


    @Autowired
    private  AppColorConfigFacade appColorConfigFacade;

    public  MerchantResult<AppColorConfigResult> queryAppColorConfig(String appid, String style) {
        GetAppColorConfigRequest getAppColorConfigRequest = new GetAppColorConfigRequest();
        getAppColorConfigRequest.setAppId(appid);
        getAppColorConfigRequest.setStyle(style);

        return appColorConfigFacade.queryAppColorConfig(getAppColorConfigRequest);
    }
}
