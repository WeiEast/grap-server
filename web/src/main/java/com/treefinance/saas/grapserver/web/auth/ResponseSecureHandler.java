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

import com.datatrees.toolkits.util.crypto.AES;
import com.datatrees.toolkits.util.crypto.core.Encryptor;
import com.datatrees.toolkits.util.json.Jackson;
import com.treefinance.saas.knife.result.SimpleResult;
import com.treefinance.saas.grapserver.common.exception.CryptorException;
import com.treefinance.saas.grapserver.common.exception.ResponseEncryptException;
import com.treefinance.saas.grapserver.common.model.AppLicenseKey;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Jerry
 * @since 15:53 26/04/2017
 */
public class ResponseSecureHandler extends SecureHandler {

  private final Encryptor encryptor;

  public ResponseSecureHandler(AppLicenseKey appLicenseKey) {
    super(appLicenseKey);
    if (logger.isDebugEnabled()) {
      logger.debug("Building request secure handler for appLicenseKey '" + appLicenseKey.getAppId() + "'");
    }
    this.encryptor = getEncryptor(appLicenseKey);
  }

  private Encryptor getEncryptor(AppLicenseKey appLicenseKey) {
    try {
      String sdkPublicKey = appLicenseKey.getSdkPublicKey();
      if (StringUtils.isEmpty(sdkPublicKey)) {
        throw new IllegalArgumentException("Can not find commercial tenant's public key.");
      }

      return AES.of("CBC", "PKCS5Padding").getEncryptor(sdkPublicKey);
    } catch (Exception e) {
      throw new CryptorException(
          "Error creating Encryptor with appId '" + appLicenseKey.getAppId() + " to encrypt response.", e);
    }
  }

  public SimpleResult encrypt(SimpleResult result) throws ResponseEncryptException {
    if (result != null) {
      Object data = result.getData();
      if (data == null) {
        return result;
      }

      String encryptedData = encryptResult(data);

      if (logger.isDebugEnabled()) {
        logger.debug("Finish encrypting response for appLicenseKey '{}'.", appLicenseKey.getAppId());
      }

      return new SimpleResult(result.getTimestamp(), result.getErrorMsg(), encryptedData);
    }

    return null;
  }

  private String encryptResult(Object data) throws ResponseEncryptException {
    Encryptor encryptor = getEncryptor(appLicenseKey);

    try {
      byte[] json = Jackson.toJSONByteArray(data);

      return encryptor.encryptAsBase64String(json);
    } catch (Exception e) {
      throw new ResponseEncryptException("Error encrypting response data", e);
    }
  }

}
