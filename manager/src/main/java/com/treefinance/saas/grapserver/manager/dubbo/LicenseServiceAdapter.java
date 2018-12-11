/*
 * Copyright © 2015 - 2017 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.treefinance.saas.grapserver.manager.dubbo;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.grapserver.context.component.RpcActionEnum;
import com.treefinance.saas.grapserver.manager.LicenseManager;
import com.treefinance.saas.grapserver.manager.domain.AppLicenseBO;
import com.treefinance.saas.merchant.facade.request.grapserver.GetAppLicenseRequest;
import com.treefinance.saas.merchant.facade.result.console.MerchantResult;
import com.treefinance.saas.merchant.facade.result.grapsever.AppLicenseResult;
import com.treefinance.saas.merchant.facade.service.AppLicenseFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;

/**
 * @author Jerry
 * @date 2018/12/11 00:21
 */
@Service
public class LicenseServiceAdapter extends AbstractMerchantServiceAdapter implements LicenseManager {

    private final AppLicenseFacade appLicenseFacade;

    @Autowired
    public LicenseServiceAdapter(AppLicenseFacade appLicenseFacade) {
        this.appLicenseFacade = appLicenseFacade;
    }

    @Override
    public AppLicenseBO getAppLicenseByAppId(@Nonnull String appId) {
        GetAppLicenseRequest request = new GetAppLicenseRequest();
        request.setAppId(appId);

        MerchantResult<AppLicenseResult> result = appLicenseFacade.getAppLicense(request);

        if (logger.isDebugEnabled()) {
            logger.debug("请求app-license远程服务，appId: {} 结果：{}", appId, JSON.toJSONString(result));
        }

        validateResponse(result, RpcActionEnum.QUERY_APP_LICENSE_BY_APP_ID, request);

        return convert(result.getData(), AppLicenseBO.class);
    }
}
