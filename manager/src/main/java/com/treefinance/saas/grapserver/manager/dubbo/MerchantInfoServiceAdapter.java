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
import com.treefinance.saas.grapserver.exception.IllegalBizDataException;
import com.treefinance.saas.grapserver.manager.MerchantInfoManager;
import com.treefinance.saas.grapserver.manager.domain.BaseMerchantInfoBO;
import com.treefinance.saas.merchant.facade.request.console.GetMerchantByAppIdRequest;
import com.treefinance.saas.merchant.facade.result.console.MerchantBaseInfoResult;
import com.treefinance.saas.merchant.facade.result.console.MerchantResult;
import com.treefinance.saas.merchant.facade.service.MerchantBaseInfoFacade;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;

import java.util.List;

/**
 * @author Jerry
 * @date 2018/12/11 01:05
 */
@Service
public class MerchantInfoServiceAdapter extends AbstractMerchantServiceAdapter implements MerchantInfoManager {

    private final MerchantBaseInfoFacade merchantBaseInfoFacade;

    @Autowired
    public MerchantInfoServiceAdapter(MerchantBaseInfoFacade merchantBaseInfoFacade) {
        this.merchantBaseInfoFacade = merchantBaseInfoFacade;
    }

    @Override
    public BaseMerchantInfoBO getBaseInfoByAppId(@Nonnull String appId) {
        GetMerchantByAppIdRequest request = new GetMerchantByAppIdRequest();
        request.setAppId(appId);

        MerchantResult<List<MerchantBaseInfoResult>> merchantResult = merchantBaseInfoFacade.queryMerchantBaseByAppId(request);

        validateResponse(merchantResult, RpcActionEnum.QUERY_MERCHANT_BASE_INFO_BY_APP_ID, request);

        List<BaseMerchantInfoBO> list = convert(merchantResult.getData(), BaseMerchantInfoBO.class);
        BaseMerchantInfoBO baseInfo = CollectionUtils.isEmpty(list) ? null : list.get(0);

        if (baseInfo == null) {
            throw new IllegalBizDataException("Can not find merchant base information which appId" + " is '" + appId + "'");
        }

        return baseInfo;
    }
}
