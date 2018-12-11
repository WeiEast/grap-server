/*
 * Copyright © 2015 - 2017 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.treefinance.saas.grapserver.biz.service;

import com.treefinance.saas.grapserver.biz.domain.AppLicense;
import com.treefinance.saas.grapserver.context.component.AbstractService;
import com.treefinance.saas.grapserver.manager.LicenseManager;
import com.treefinance.saas.grapserver.manager.domain.AppLicenseBO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Jerry
 * @since 19:14 25/04/2017
 */
@Component
public class LicenseService extends AbstractService {


    @Autowired
    private LicenseManager licenseManager;


    public AppLicense getAppLicense(String appId) {
        AppLicenseBO license = licenseManager.getAppLicenseByAppId(appId);

        return convert(license, AppLicense.class);
    }

    public String getDataSecretKey(String appId) {
        AppLicenseBO license = licenseManager.getAppLicenseByAppId(appId);

        return license.getDataSecretKey();
    }

}
