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

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.grapserver.common.model.dto.CallBackLicenseDTO;
import com.treefinance.saas.grapserver.dao.entity.AppLicense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author Jerry
 * @since 19:14 25/04/2017
 */
@Component
public class AppLicenseService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    /**
     * APP密钥
     */
    private final static String APPID_SUFFIX = "saas_gateway_app_license:";
    /**
     * 回调密钥
     */
    private final static String CALLBACK_SUFFIX = "saas_gateway_callback_license:";

    public AppLicense getAppLicense(String appId) {
        String key = APPID_SUFFIX + appId;
        AppLicense result = null;
        if (stringRedisTemplate.hasKey(key)) {
            result = JSON.parseObject(stringRedisTemplate.opsForValue().get(key), AppLicense.class);
        }
        return result;
    }

    public String setAppLicense(AppLicense appLicense) {
        String key = APPID_SUFFIX + appLicense.getAppId();
        stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(appLicense));
        return key;
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
        String key = CALLBACK_SUFFIX + callbackId;
        CallBackLicenseDTO result = null;
        if (stringRedisTemplate.hasKey(key)) {
            result = JSON.parseObject(stringRedisTemplate.opsForValue().get(key), CallBackLicenseDTO.class);
        }
        return result;
    }
}
