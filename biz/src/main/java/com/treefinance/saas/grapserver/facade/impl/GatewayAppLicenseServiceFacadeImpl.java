package com.treefinance.saas.grapserver.facade.impl;

import com.treefinance.saas.gateway.servicefacade.AppLicenseService;
import com.treefinance.saas.grapserver.biz.service.LicenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 兼容老的AppLicenseService
 * @author yh-treefinance on 2017/9/19.
 */
@Service("gatewayAppLicenseServiceFacade")
public class GatewayAppLicenseServiceFacadeImpl implements AppLicenseService {

    @Autowired
    private LicenseService licenseService;

    @Override
    public String getDataKey(String appId) {
        return licenseService.getDataSecretKey(appId);
    }

}
