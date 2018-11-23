package com.treefinance.saas.grapserver.biz.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.treefinance.saas.merchant.facade.request.grapserver.GetAppColorConfigRequest;
import com.treefinance.saas.merchant.facade.result.console.MerchantResult;
import com.treefinance.saas.merchant.facade.result.grapsever.AppColorConfigResult;
import com.treefinance.saas.merchant.facade.service.AppColorConfigFacade;

/**
 * @author:guoguoyun
 * @date:Created in 2018/11/16上午10:39
 */
@Component
public class GetAppColorConfigConverter {

    private  final Logger logger = LoggerFactory.getLogger(GetAppColorConfigConverter.class);

    @Autowired
    private  AppColorConfigFacade appColorConfigFacade;

    public  MerchantResult<AppColorConfigResult> queryAppColorConfig(String appid, String style) {
        GetAppColorConfigRequest getAppColorConfigRequest = new GetAppColorConfigRequest();
        getAppColorConfigRequest.setAppId(appid);
        getAppColorConfigRequest.setStyle(style);

        return appColorConfigFacade.queryAppColorConfig(getAppColorConfigRequest);
    }
}
