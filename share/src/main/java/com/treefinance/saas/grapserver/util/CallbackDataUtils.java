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

package com.treefinance.saas.grapserver.util;

import com.treefinance.b2b.saas.util.DataUtils;
import com.treefinance.saas.grapserver.exception.CallbackCryptoException;
import com.treefinance.toolkit.util.crypto.exception.CryptoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author luoyihua on 2017/5/10.
 */
public final class CallbackDataUtils {

    private static final Logger logger = LoggerFactory.getLogger(CallbackDataUtils.class);

    private CallbackDataUtils() {}

    /**
     * RSA 加密
     */
    public static String encrypt(Object data, String publicKey) throws CallbackCryptoException {
        if (data == null) {
            return null;
        }
        try {
            String encryptedData = DataUtils.encryptBeanAsBase64StringByRsa(data, publicKey);

            logger.debug("Finish encrypting callback for encryptedData '{}'.", encryptedData);
            return encryptedData;
        } catch (CryptoException e) {
            throw new CallbackCryptoException("Error encrypting callback data", e);
        }
    }

    /**
     * RSA 解密
     */
    public static String decrypt(String data, String privateKey) throws CallbackCryptoException {
        if (data == null) {
            return null;
        }
        try {
            String decryptedData = DataUtils.decryptWithBase64AsStringByRsa(data, privateKey);

            logger.debug("Finish decrypting callback for decryptedData '{}'.", decryptedData);
            return decryptedData;
        } catch (CryptoException e) {
            throw new CallbackCryptoException("Error decrypting callback data", e);
        }
    }

    /**
     * AES 解密
     */
    public static String decryptByAES(byte[] data, String dataKey) throws CallbackCryptoException {
        try {
            return DataUtils.decryptAsStringByAes(data, dataKey);
        } catch (Exception e) {
            throw new CallbackCryptoException("decryptByAES exception", e);
        }
    }


}
