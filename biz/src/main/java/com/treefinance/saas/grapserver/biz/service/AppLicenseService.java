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

import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.fastjson.JSON;
import com.treefinance.saas.grapserver.common.model.dto.CallBackLicenseDTO;
import com.treefinance.saas.grapserver.dao.entity.AppLicense;
import com.treefinance.saas.merchant.center.facade.request.grapserver.GetAppLicenseRequest;
import com.treefinance.saas.merchant.center.facade.request.grapserver.GetCallbackLicenseRequest;
import com.treefinance.saas.merchant.center.facade.request.grapserver.SetAppLicenseRequest;
import com.treefinance.saas.merchant.center.facade.result.console.MerchantResult;
import com.treefinance.saas.merchant.center.facade.result.grapsever.AppLicenseResult;
import com.treefinance.saas.merchant.center.facade.result.grapsever.CallbackLicenseResult;
import com.treefinance.saas.merchant.center.facade.result.grapsever.SetAppLicenseResult;
import com.treefinance.saas.merchant.center.facade.service.AppLicenseFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Jerry
 * @since 19:14 25/04/2017
 */
@Component
public class AppLicenseService {

    @Resource
    private AppLicenseFacade appLicenseFacade;

    private static final Logger logger = LoggerFactory.getLogger(AppBizLicenseService.class);

    /**
     * APP密钥
     */
    private final static String APPID_SUFFIX = "saas_gateway_app_license:";
    /**
     * 回调密钥
     */
    private final static String CALLBACK_SUFFIX = "saas_gateway_callback_license:";

    public AppLicense getAppLicense(String appId) {

        GetAppLicenseRequest request = new GetAppLicenseRequest();
        request.setAppId(appId);
        MerchantResult<AppLicenseResult> result;
        try {
            result = appLicenseFacade.getAppLicense(request);
        } catch (RpcException e) {
            logger.error("获取appLicense失败，错误信息：{}", e.getMessage());
            return null;
        }
        if (!result.isSuccess()) {
            return null;
        }

        AppLicenseResult appLicenseResult = result.getData();
        AppLicense appLicense = new AppLicense();

        BeanUtils.copyProperties(appLicenseResult, appLicense);

        logger.info(JSON.toJSONString(appLicense));

        return appLicense;
    }

    public String setAppLicense(AppLicense appLicense) {

        SetAppLicenseRequest request = new SetAppLicenseRequest();

        BeanUtils.copyProperties(appLicense, request);

        MerchantResult<SetAppLicenseResult> result;
        try {
            result = appLicenseFacade.setAppLicense(request);
        } catch (RpcException e) {
            logger.error("获取appLicense失败，错误信息：{}", e.getMessage());
            return null;
        }
        if (!result.isSuccess()) {
            return null;
        }

        SetAppLicenseResult appLicenseResult = result.getData();

        return appLicenseResult.getKey();
    }

    public String getDataKey(String appId) {
        AppLicense appLicense = this.getAppLicense(appId);
        if (appLicense == null) {
            return null;
        }
        return appLicense.getDataSecretKey();
    }

    /**
     * 获取回调配置
     *
     * @param callbackId
     * @return
     */
    public CallBackLicenseDTO getCallbackLicense(Integer callbackId) {

        GetCallbackLicenseRequest request = new GetCallbackLicenseRequest();

        request.setCallbackId(callbackId);

        MerchantResult<CallbackLicenseResult> result;
        try {
            result = appLicenseFacade.getCallbackLicense(request);
        } catch (RpcException e) {
            logger.error("获取appLicense失败，错误信息：{}", e.getMessage());
            return null;
        }
        if (!result.isSuccess()) {
            return null;
        }

        CallbackLicenseResult appLicenseResult = result.getData();


        CallBackLicenseDTO dto = new CallBackLicenseDTO();
        BeanUtils.copyProperties(appLicenseResult, dto);

        logger.info(JSON.toJSONString(dto));

        return dto;


    }
}
