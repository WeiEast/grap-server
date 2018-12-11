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
import com.treefinance.saas.grapserver.manager.BizLicenseManager;
import com.treefinance.saas.grapserver.manager.domain.BizLicenseInfoBO;
import com.treefinance.saas.merchant.facade.request.common.BaseRequest;
import com.treefinance.saas.merchant.facade.request.grapserver.GetAppLicenseByAppIdRequest;
import com.treefinance.saas.merchant.facade.result.console.AppBizLicenseResult;
import com.treefinance.saas.merchant.facade.result.console.MerchantResult;
import com.treefinance.saas.merchant.facade.service.AppBizLicenseFacade;
import javax.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Jerry
 * @date 2018/12/11 01:20
 */
@Service
public class BizLicenseServiceAdapter extends AbstractMerchantServiceAdapter implements BizLicenseManager {

    private final AppBizLicenseFacade appBizLicenseFacade;

    @Autowired
    public BizLicenseServiceAdapter(AppBizLicenseFacade appBizLicenseFacade) {
        this.appBizLicenseFacade = appBizLicenseFacade;
    }

    @Override
    public List<BizLicenseInfoBO> listBizLicenseInfosByAppId(@Nonnull String appId) {
        GetAppLicenseByAppIdRequest request = new GetAppLicenseByAppIdRequest();
        request.setAppId(appId);

        MerchantResult<List<AppBizLicenseResult>> result = appBizLicenseFacade.queryAppBizLicenseByAppId(request);

        if (logger.isDebugEnabled()) {
            logger.debug("请求biz-license远程服务，appId: {}, 结果：{}", appId, JSON.toJSONString(result));
        }

        validateResponse(result, RpcActionEnum.QUERY_APP_BIZ_LICENSE_BY_APP_ID, request);

        return convert(result.getData(), BizLicenseInfoBO.class);
    }

    @Override
    public List<BizLicenseInfoBO> listBizLicenseInfos() {
        BaseRequest request = new BaseRequest();

        MerchantResult<List<AppBizLicenseResult>> result = appBizLicenseFacade.queryAllAppBizLicense(request);

        if (logger.isDebugEnabled()) {
            logger.debug("请求biz-license远程服务，查询全部, 结果：{}", JSON.toJSONString(result));
        }

        validateResponse(result, RpcActionEnum.QUERY_APP_BIZ_LICENSE_ALL);

        return convert(result.getData(), BizLicenseInfoBO.class);
    }
}
