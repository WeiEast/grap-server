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
import com.treefinance.saas.grapserver.manager.CallbackConfigManager;
import com.treefinance.saas.grapserver.manager.domain.CallbackConfigBO;
import com.treefinance.saas.merchant.facade.request.common.BaseRequest;
import com.treefinance.saas.merchant.facade.request.grapserver.GetAppCallBackConfigByIdRequest;
import com.treefinance.saas.merchant.facade.result.console.MerchantResult;
import com.treefinance.saas.merchant.facade.result.grapsever.AppCallbackResult;
import com.treefinance.saas.merchant.facade.service.AppCallbackConfigFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;

import java.util.List;

/**
 * @author Jerry
 * @date 2018/12/11 01:20
 */
@Service
public class CallbackConfigServiceAdapter extends AbstractMerchantServiceAdapter implements CallbackConfigManager {

    private final AppCallbackConfigFacade appCallbackConfigFacade;

    @Autowired
    public CallbackConfigServiceAdapter(AppCallbackConfigFacade appCallbackConfigFacade) {
        this.appCallbackConfigFacade = appCallbackConfigFacade;
    }

    @Override
    public List<CallbackConfigBO> listCallbackConfigsByAppId(@Nonnull String appId) {
        GetAppCallBackConfigByIdRequest request = new GetAppCallBackConfigByIdRequest();
        request.setAppId(appId);

        MerchantResult<List<AppCallbackResult>> result = appCallbackConfigFacade.queryAppCallBackConfigByAppId(request);

        if (logger.isDebugEnabled()) {
            logger.debug("请求callback-config远程服务，appId: {}，结果: {}", appId, result);
        }

        validateResponse(result, RpcActionEnum.QUERY_APP_CALLBACK_CONFIG_BY_APP_ID, request);

        return convert(result.getData(), CallbackConfigBO.class);
    }

    @Override
    public List<CallbackConfigBO> listCallbackConfigs() {
        MerchantResult<List<AppCallbackResult>> result = appCallbackConfigFacade.queryAllAppCallBackConfig(new BaseRequest());

        if (logger.isDebugEnabled()) {
            logger.debug("请求callback-config远程服务，查询全部，结果: {}", result);
        }

        validateResponse(result, RpcActionEnum.QUERY_APP_CALLBACK_CONFIG_ALL);

        return convert(result.getData(), CallbackConfigBO.class);
    }
}
