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

import com.treefinance.saas.grapserver.context.component.RpcActionEnum;
import com.treefinance.saas.grapserver.exception.RpcServiceException;
import com.treefinance.saas.grapserver.manager.ColorConfigManager;
import com.treefinance.saas.grapserver.manager.domain.ColorConfigBO;
import com.treefinance.saas.merchant.facade.request.grapserver.GetAppColorConfigRequest;
import com.treefinance.saas.merchant.facade.result.console.MerchantResult;
import com.treefinance.saas.merchant.facade.result.grapsever.AppColorConfigResult;
import com.treefinance.saas.merchant.facade.service.AppColorConfigFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;

/**
 * @author guoguoyun
 * @date 2018/11/16上午10:39
 */
@Service
public class ColorConfigServiceAdapter extends AbstractMerchantServiceAdapter implements ColorConfigManager {

    private final AppColorConfigFacade appColorConfigFacade;

    @Autowired
    public ColorConfigServiceAdapter(AppColorConfigFacade appColorConfigFacade) {
        this.appColorConfigFacade = appColorConfigFacade;
    }

    @Override
    public ColorConfigBO queryAppColorConfig(@Nonnull String appId, String style) {
        GetAppColorConfigRequest request = new GetAppColorConfigRequest();
        request.setAppId(appId);
        request.setStyle(style);

        MerchantResult<AppColorConfigResult> result = appColorConfigFacade.queryAppColorConfig(request);

        try {
            validateResponse(result, RpcActionEnum.QUERY_APP_COLOR_CONFIG, request);
        } catch (RpcServiceException e) {
            // 允许返回值为空
            logger.warn(e.getMessage());
            return null;
        }

        return convert(result.getData(), ColorConfigBO.class);
    }
}
