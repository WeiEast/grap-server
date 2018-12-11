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
import com.treefinance.saas.grapserver.manager.CallbackBizManager;
import com.treefinance.saas.grapserver.manager.domain.CallbackBizInfoBO;
import com.treefinance.saas.merchant.facade.request.common.BaseRequest;
import com.treefinance.saas.merchant.facade.request.grapserver.GetAppCallBackBizByCallbackIdRequest;
import com.treefinance.saas.merchant.facade.result.console.MerchantResult;
import com.treefinance.saas.merchant.facade.result.grapsever.AppCallbackBizResult;
import com.treefinance.saas.merchant.facade.service.AppCallBackBizFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;

import java.util.List;

/**
 * @author Jerry
 * @date 2018/12/11 01:21
 */
@Service
public class CallbackBizServiceAdapter extends AbstractMerchantServiceAdapter implements CallbackBizManager {

    private final AppCallBackBizFacade appCallBackBizFacade;

    @Autowired
    public CallbackBizServiceAdapter(AppCallBackBizFacade appCallBackBizFacade) {
        this.appCallBackBizFacade = appCallBackBizFacade;
    }

    @Override
    public List<CallbackBizInfoBO> listCallBackBizInfosByCallbackId(@Nonnull Integer callbackId) {
        GetAppCallBackBizByCallbackIdRequest request = new GetAppCallBackBizByCallbackIdRequest();
        request.setCallbackId(callbackId);

        MerchantResult<List<AppCallbackBizResult>> result = appCallBackBizFacade.queryAppCallBackByCallbackId(request);

        if (logger.isDebugEnabled()) {
            logger.debug("请求callback-biz远程服务，callbackId: {}， 结果: {}", callbackId, JSON.toJSONString(result));
        }

        validateResponse(result, RpcActionEnum.QUERY_APP_CALLBACK_BIZ_BY_CALLBACK_ID, request);

        return convert(result.getData(), CallbackBizInfoBO.class);
    }

    @Override
    public List<CallbackBizInfoBO> listCallBackBizInfos() {
        MerchantResult<List<AppCallbackBizResult>> result = appCallBackBizFacade.queryAllAppCallBack(new BaseRequest());

        if (logger.isDebugEnabled()) {
            logger.debug("请求callback-biz远程服务，查询全部， 结果: {}", JSON.toJSONString(result));
        }

        validateResponse(result, RpcActionEnum.QUERY_APP_CALLBACK_BIZ_ALL);

        return convert(result.getData(), CallbackBizInfoBO.class);
    }
}
