package com.treefinance.saas.grapserver.biz.service;

import com.treefinance.saas.grapserver.common.utils.DataConverterUtils;
import com.treefinance.saas.grapserver.dao.entity.AppColorConfig;
import com.treefinance.saas.merchant.center.facade.request.grapserver.GetAppColorConfigRequest;
import com.treefinance.saas.merchant.center.facade.result.console.MerchantResult;
import com.treefinance.saas.merchant.center.facade.result.grapsever.AppColorConfigResult;
import com.treefinance.saas.merchant.center.facade.service.AppColorConfigFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Created by luoyihua on 2017/5/2.
 */
@Service
public class AppColorConfigService {

    @Autowired
    private AppColorConfigFacade appColorConfigFacade;

    public AppColorConfig getByAppId(String appId, String style) {


        GetAppColorConfigRequest getAppColorConfigRequest = new GetAppColorConfigRequest();
        getAppColorConfigRequest.setStyle(style);
        getAppColorConfigRequest.setAppId(appId);
        MerchantResult<AppColorConfigResult> listMerchantResult = appColorConfigFacade.queryAppColorConfig(getAppColorConfigRequest);
        AppColorConfig appColorConfig = DataConverterUtils.convert(listMerchantResult.getData(),AppColorConfig.class);
        if (appColorConfig==null) {
            return null;
        }
        return appColorConfig;
    }
}
