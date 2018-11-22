package com.treefinance.saas.grapserver.biz.facade;

import com.treefinance.saas.gateway.servicefacade.AppLicenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 兼容老的AppLicenseService
 * @author yh-treefinance on 2017/9/19.
 */
@Service("gatewayAppLicenseServiceFacade")
public class GatewayAppLicenseServiceFacadeImpl implements AppLicenseService {

    @Autowired
    private com.treefinance.saas.grapserver.biz.service.AppLicenseService appLicenseService;

    @Override
    public String getDataKey(String appId) {
        return appLicenseService.getDataKey(appId);
    }

}
