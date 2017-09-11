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

import com.datatrees.toolkits.util.crypto.RSA;
import com.datatrees.toolkits.util.crypto.core.Decryptor;
import com.datatrees.toolkits.util.json.Jackson;
import com.fasterxml.jackson.databind.JsonNode;
import com.treefinance.saas.grapserver.common.exception.CryptorException;
import com.treefinance.saas.grapserver.common.exception.RequestDecryptException;
import com.treefinance.saas.grapserver.common.model.AppLicenseKey;
import com.treefinance.saas.grapserver.web.request.RequestParamsParser;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jerry
 * @since 15:53 26/04/2017
 */
public class RequestSecureHandler extends SecureHandler {

    protected final Decryptor decryptor;
    protected boolean test;

    public RequestSecureHandler(AppLicenseKey appLicenseKey, boolean test) {
        super(appLicenseKey);
        if (logger.isDebugEnabled()) {
            logger.debug("Building request secure handler for appLicenseKey '" + appLicenseKey.getAppId() + "'");
        }
        this.decryptor = getDecryptor(appLicenseKey);
        this.test = test;
    }

    protected Decryptor getDecryptor(AppLicenseKey appLicenseKey) {
        try {
            String sdkPrivateKey = appLicenseKey.getSdkPrivateKey();
            if (StringUtils.isEmpty(sdkPrivateKey)) {
                throw new IllegalArgumentException("Can not find private key.");
            }

            return RSA.createDecryptor(sdkPrivateKey);
        } catch (Exception e) {
            throw new CryptorException(
                    "Error creating decryptor with appId '" + appLicenseKey.getAppId() + " to decrypt request.", e);
        }
    }

    public Map<String, String[]> decrypt(String[] params) throws RequestDecryptException {
        if (ArrayUtils.isEmpty(params)) {
            throw new IllegalArgumentException("Incorrect parameters!");
        }

        Map<String, String[]> parameters = new HashMap<>(params.length);

        for (String param : params) {
            if (StringUtils.isBlank(param)) {
                continue;
            }

            String text = param.replaceAll("\\s+", "");
            JsonNode data;
            try {
                byte[] content = decryptor.decryptWithBase64(text);
                data = Jackson.parse(content);
            } catch (Throwable e) {
                if (test) {
                    try {
                        data = Jackson.parse(text);
                    } catch (Throwable e1) {
                        throw new RequestDecryptException("Error decrypting request params", e);
                    }
                } else {
                    throw new RequestDecryptException("Error decrypting request params", e);
                }
            }

            parameters.putAll(RequestParamsParser.parse(data));
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Finish decrypting parameters for appLicenseKey '{}'.", appLicenseKey.getAppId());
        }

        return parameters;
    }


}
