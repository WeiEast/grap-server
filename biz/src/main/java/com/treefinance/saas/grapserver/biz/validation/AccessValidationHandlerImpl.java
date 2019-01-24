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

package com.treefinance.saas.grapserver.biz.validation;

import com.treefinance.saas.grapserver.biz.domain.BizLicenseInfo;
import com.treefinance.saas.grapserver.biz.service.AppBizLicenseService;
import com.treefinance.saas.grapserver.common.enums.EBizType;
import com.treefinance.saas.grapserver.context.Constants;
import com.treefinance.saas.grapserver.context.Preconditions;
import com.treefinance.saas.grapserver.exception.ForbiddenException;
import com.treefinance.saas.grapserver.manager.MerchantInfoManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Jerry
 * @date 2019-01-24 11:19
 */
@Component
public class AccessValidationHandlerImpl implements AccessValidationHandler {

    @Autowired
    private AppBizLicenseService appBizLicenseService;
    @Autowired
    private MerchantInfoManager merchantInfoManager;

    @Override
    public void validateTask(String appId, String uniqueId, EBizType bizType) {
        Preconditions.notBlank("appId", appId);
        Preconditions.notBlank("uniqueId", uniqueId);
        Preconditions.notNull("bizType", bizType);

        // 商户是否禁用状态
        if (!merchantInfoManager.isActive(appId)) {
            throw new ForbiddenException("商户已禁用！appId: " + appId);
        }

        if (isAccessForbidden(appId, bizType)) {
            throw new ForbiddenException("禁止访问！appId: " + appId + ", bizType: " + bizType);
        }
    }

    private boolean isAccessForbidden(String appId, EBizType bizType) {
        List<BizLicenseInfo> list = appBizLicenseService.getByAppId(appId);
        if (!list.isEmpty()) {
            for (BizLicenseInfo info : list) {
                // 仅启用状态授权可用
                if (Constants.OK.equals(info.getIsValid()) && info.getBizType().equals(bizType.getCode())) {
                    return false;
                }
            }
        }

        return true;
    }
}
