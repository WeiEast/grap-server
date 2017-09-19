package com.treefinance.saas.grapserver.biz.common;

import com.alibaba.fastjson.JSON;
import com.datatrees.toolkits.util.Base64Codec;
import com.datatrees.toolkits.util.crypto.RSA;
import com.datatrees.toolkits.util.crypto.core.Decryptor;
import com.datatrees.toolkits.util.crypto.core.Encryptor;
import com.datatrees.toolkits.util.json.Jackson;
import com.google.common.collect.Maps;
import com.treefinance.saas.grapserver.common.exception.CallbackEncryptException;
import com.treefinance.saas.grapserver.common.exception.CryptorException;
import com.treefinance.saas.grapserver.common.utils.AESSecureUtils;
import com.treefinance.saas.grapserver.common.utils.HttpClientUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by luoyihua on 2017/5/10.
 */
@Service
public class CallbackSecureHandler {
    private static final Logger logger = LoggerFactory.getLogger(CallbackSecureHandler.class);

    /**
     * RSA 加密
     *
     * @param data
     * @param publicKey
     * @return
     * @throws CallbackEncryptException
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
     *
     * @param data
     * @param privateKey
     * @return
     * @throws CallbackEncryptException
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
     *
     * @param data
     * @param dataKey
     * @return
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
     *
     * @param data
     * @param dataKey
     * @return
     */
    public String encryptByAES(Object data, String dataKey) throws CallbackEncryptException {
        try {
            String dataStr = JSON.toJSONString(data);
            byte[] encryData = AESSecureUtils.encrypt(dataKey, dataStr.getBytes());
            return Base64Codec.encode(encryData);
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

    public static void main(String[] args) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("params", "nBwsWIqBHRCyPxTb1ubZFuHp6o2ocATTs9GS27XW7IiFPq9K0i7Er4euT8s3mhbamXeQ5DDdagI4FGV5JRXr6%2Fp2IZIxplGhyCDDJ%2BGjU3wS7khw8b0XKLhH%2BfaEHdoQXZpybaqWqQj4l18gYfgyFA394IqLoHMPSvUV9tFjaipM%2Btl6sibNjKLCxbYbdWKVgYG6rHaubRsyWcs%2FhV%2BABuxvhc3zqayCg5nl3E1asiB9e8aFeslTaMIoX5sLEbQV4gLTjuxuhy%2F3af3uu%2FzA5haYdQanH5yq2oFkM6FwSL2mW4mrPshXjEcVQoHsvs33mOIlJLyIhiTBOeV2Tp56rXmSoohDJ7rSe1zOfZk%2BUOKnpBzjgbJ9g4xpQcNLSBma1FBKvzFhGKK09n%2BkY937ekgvj5vJmQ0yWcRTS7aZKMgy%2Fj4le%2FubY%2BZ5PBVdFYvWjMTta1B5XkwmbY1t4LygoNK379gpgzTYKQE0sXNcvdhEV%2B6FgpJ1YcVfz9w8GDhHHcvhZpsYwV16EpNmnND1%2F1xnYYxK8GnKIK68F4rXhWvhsY0Et2Gv8Ek6ZivlzUCgPZlqZU3b5qiC8vz1YsX2rbqOoZb7w2r1GEK79QTTjJSS61RJjLphSCsKeTOweLnIrEnCQiyGw9HbGMZc1P8%2FEAx21W%2FMwqFVvn6laH1bQII%3D");
        String url = "https://api.91xjcs.com/supermarket/thirdparty/ds/callback/xjcs";
        String out = HttpClientUtils.doGetWithTimoutAndRetryTimes(url,
                (byte) 10, (byte) 2, map);
        System.out.println(out);
    }
}
