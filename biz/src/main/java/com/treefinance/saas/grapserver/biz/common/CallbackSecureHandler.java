package com.treefinance.saas.grapserver.biz.common;

import com.alibaba.fastjson.JSON;
import com.datatrees.toolkits.util.Base64Codec;
import com.datatrees.toolkits.util.crypto.RSA;
import com.datatrees.toolkits.util.crypto.core.Decryptor;
import com.datatrees.toolkits.util.crypto.core.Encryptor;
import com.datatrees.toolkits.util.json.Jackson;
import com.treefinance.saas.grapserver.common.exception.CallbackEncryptException;
import com.treefinance.saas.grapserver.common.exception.CryptorException;
import com.treefinance.saas.grapserver.common.utils.AESSecureUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author luoyihua on 2017/5/10.
 */
@Service
public class CallbackSecureHandler {

    private static final Logger logger = LoggerFactory.getLogger(CallbackSecureHandler.class);

    /**
     * RSA 加密
     */
    public String encrypt(Object data, String publicKey) throws CallbackEncryptException {
        if (data == null) {
            return null;
        }
        String encryptedData = Helper.encryptResult(data, publicKey);
        logger.debug("Finish encrypting callback for encryptedData '{}'.", encryptedData);
        return encryptedData;
    }

    /**
     * RSA 解密
     */
    public String decrypt(Object data, String privateKey) throws CallbackEncryptException {
        if (data == null) {
            return null;
        }
        String decryptedData = Helper.decryptResult(data, privateKey);
        logger.debug("Finish decrypting callback for decryptedData '{}'.", decryptedData);

        return decryptedData;
    }

    /**
     * AES 解密
     */
    public String decryptByAES(byte[] data, String dataKey) throws CallbackEncryptException {
        try {
            return AESSecureUtils.decrypt(dataKey, data);
        } catch (Exception e) {
            throw new CallbackEncryptException("decryptByAES exception", e);
        }
    }

    /**
     * AES 加密( 先AES，后Base64)
     */
    public String encryptByAES(Object data, String dataKey) throws CallbackEncryptException {
        try {
            String dataStr = JSON.toJSONString(data);
            byte[] encryptData = AESSecureUtils.encrypt(dataKey, dataStr.getBytes());
            return Base64Codec.encode(encryptData);
        } catch (Exception e) {
            throw new CallbackEncryptException("encryptByAES exception", e);
        }
    }


    /**
     * 辅助类
     */
    static class Helper {

        public static Encryptor getEncryptor(String publicKey) {
            try {
                if (StringUtils.isEmpty(publicKey)) {
                    throw new IllegalArgumentException("Can not find commercial tenant's public key.");
                }

                return RSA.createEncryptor(publicKey);
            } catch (Exception e) {
                throw new CryptorException(
                        "Error creating Encryptor with publicKey '" + publicKey + " to encrypt callback.", e);
            }
        }

        public static String encryptResult(Object data, String publicKey) throws CallbackEncryptException {
            Encryptor encryptor = getEncryptor(publicKey);
            try {
                byte[] json = Jackson.toJSONByteArray(data);
                return encryptor.encryptAsBase64String(json);
            } catch (Exception e) {
                throw new CallbackEncryptException("Error encrypting callback data", e);
            }
        }

        public static Decryptor getDecryptor(String privateKey) {
            try {
                if (StringUtils.isEmpty(privateKey)) {
                    throw new IllegalArgumentException("Can not find commercial tenant's private key.");
                }
                return RSA.createDecryptor(privateKey);
            } catch (Exception e) {
                throw new CryptorException(
                        "Error creating Decryptor with privateKey '" + privateKey + " to encrypt callback.", e);
            }
        }

        public static String decryptResult(Object data, String privateKey) throws CallbackEncryptException {
            Decryptor decryptor = getDecryptor(privateKey);
            try {
                byte[] json = Jackson.toJSONByteArray(data);
                return decryptor.decryptWithBase64AsString(json);
            } catch (Exception e) {
                throw new CallbackEncryptException("Error decrypting callback data", e);
            }
        }
    }

}
