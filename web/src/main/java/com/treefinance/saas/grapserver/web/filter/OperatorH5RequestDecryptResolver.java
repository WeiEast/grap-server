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

package com.treefinance.saas.grapserver.web.filter;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.grapserver.biz.config.SystemConfig;
import com.treefinance.saas.grapserver.common.model.AppLicenseKey;
import com.treefinance.saas.grapserver.web.auth.OperatorH5RequestSecureHandler;
import com.treefinance.saas.grapserver.web.auth.RequestSecureHandler;
import com.treefinance.saas.grapserver.web.request.WrappedHttpServletRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

/**
 * 运营商参数解密
 */
class OperatorH5RequestDecryptResolver extends RequestDecryptResolver {
    /**
     * 全局用app
     */
    private AppLicenseKey h5GlobalAppLicenseKey;

    public OperatorH5RequestDecryptResolver(String target, AppLicenseKey h5GlobalAppLicenseKey, SystemConfig config) {
        super(target, config);
        this.h5GlobalAppLicenseKey = h5GlobalAppLicenseKey;
    }

    @Override
    HttpServletRequest resolve(HttpServletRequest request) throws ServletException, IOException {
        String[] params = request.getParameterValues(this.target);
        if (params == null || params.length == 0) {
            if (config.isTestMode()) {
                return request;
            }

            throw new IllegalArgumentException("Parameter '" + target + "' in request not found.");
        }

//        WebContext context = WebContextUtils.getWebContext(request);

        try {
            RequestSecureHandler secureHandler = new OperatorH5RequestSecureHandler(h5GlobalAppLicenseKey, config.isTestMode());
            Map<String, String[]> parameters = secureHandler.decrypt(params);


            HttpServletRequest wrapperRequest = new WrappedHttpServletRequest<>(request, parameters);
            logger.info("request {} {} : params={}", request.getRequestURI(), request.getMethod(), JSON.toJSONString(wrapperRequest.getParameterMap()));
            return wrapperRequest;
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            throw new ServletException("Error decrypting request parameters ...", e);
        }
    }

}
