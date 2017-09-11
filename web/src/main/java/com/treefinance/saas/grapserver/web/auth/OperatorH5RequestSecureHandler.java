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

package com.treefinance.saas.grapserver.web.auth;

import com.datatrees.toolkits.util.json.Jackson;
import com.fasterxml.jackson.databind.JsonNode;
import com.treefinance.saas.grapserver.common.exception.RequestDecryptException;
import com.treefinance.saas.grapserver.common.model.AppLicenseKey;
import com.treefinance.saas.grapserver.web.request.RequestParamsParser;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jerry
 * @since 15:53 26/04/2017
 */
public class OperatorH5RequestSecureHandler extends RequestSecureHandler {

    public OperatorH5RequestSecureHandler(AppLicenseKey appLicenseKey, boolean test) {
        super(appLicenseKey, test);
        if (logger.isDebugEnabled()) {
            logger.debug("Building request secure handler for appLicenseKey '" + appLicenseKey.getAppId() + "'");
        }
    }

    @Override
    public Map<String, String[]> decrypt(String[] params) throws RequestDecryptException {
        long start = System.currentTimeMillis();
        if (ArrayUtils.isEmpty(params)) {
            throw new IllegalArgumentException("Incorrect parameters!");
        }
        int length = params.length;
        Map<String, String[]> parameters = new HashMap<>(length);

        StringBuffer paramsBuffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            String param = params[i];
            if (StringUtils.isBlank(param)) {
                continue;
            }
            String text = param.replaceAll("\\s+", "");
            try {
                String content = decryptor.decryptWithBase64AsString(text);
                paramsBuffer.append(content);
            } catch (Throwable e) {
                throw new RequestDecryptException("Error decrypting request params : index=" + i + ",param=" + text, e);
            }
        }
        String json = "";
        try {
            json = URLDecoder.decode(paramsBuffer.toString(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RequestDecryptException("Error decrypting request params : param=" + paramsBuffer.toString(), e);
        }
        JsonNode data = Jackson.parse(json);
        parameters.putAll(RequestParamsParser.parse(data));

        if (logger.isInfoEnabled()) {
            logger.debug("Finish decrypting parameters for appLicenseKey '{}' decry {} times cost {} ms : param={}",
                    appLicenseKey.getAppId(), length, (System.currentTimeMillis() - start), json);
        }
        return parameters;
    }

}
