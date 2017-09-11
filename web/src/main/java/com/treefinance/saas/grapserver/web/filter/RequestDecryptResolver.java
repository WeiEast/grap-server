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
import com.treefinance.saas.grapserver.common.model.Constants;
import com.treefinance.saas.grapserver.common.model.WebContext;
import com.treefinance.saas.grapserver.common.utils.WebContextUtils;
import com.treefinance.saas.grapserver.web.auth.RequestSecureHandler;
import com.treefinance.saas.grapserver.web.request.WrappedHttpServletRequest;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

/**
 * <p/>
 *
 * @author Jerry
 * @version 1.0.3
 * @since 1.0.1.3 [15:39, 11/21/15]
 */
class RequestDecryptResolver extends RequestResolver {

    public static final String TARGET_PARAM = "params";
    protected String target = TARGET_PARAM;
    protected SystemConfig config;

    public RequestDecryptResolver(String target, SystemConfig config) {
        if (StringUtils.isNotBlank(target)) {
            this.target = target;
        }
        this.config = config;
    }

    @Override
    boolean isSupport(HttpServletRequest request) {
        return existsAttribute(request, Constants.WEB_CONTEXT_ATTRIBUTE) && existsParameter(
                request, this.target);
    }

    private boolean existsAttribute(HttpServletRequest request, String attributeName) {
        return request.getAttribute(attributeName) != null;
    }

    private boolean existsParameter(HttpServletRequest request, String paramName) {
        return request.getParameter(paramName) != null;
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

        WebContext context = WebContextUtils.getWebContext(request);

        try {
            RequestSecureHandler secureHandler = new RequestSecureHandler(context.getAppLicenseKey(),
                    config.isTestMode());
            Map<String, String[]> parameters = secureHandler.decrypt(params);

            HttpServletRequest wrapperRequest = new WrappedHttpServletRequest<>(request, parameters);
            logger.info("request {} {} : params={}", request.getRequestURI(), request.getMethod(), JSON.toJSONString(wrapperRequest.getParameterMap()));
            return wrapperRequest;
        } catch (Throwable e) {
            throw new ServletException("Error decrypting request parameters ...", e);
        }
    }

}
