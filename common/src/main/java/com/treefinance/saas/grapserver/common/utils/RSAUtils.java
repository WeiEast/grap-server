package com.treefinance.saas.grapserver.common.utils;

/**
 * Created by yh-treefinance on 2017/10/19.
 */

import org.apache.commons.lang3.ArrayUtils;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.*;
import java.security.interfaces.RSAKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSAUtils {
    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        String params = "{\"idcard\":\"110000199001041119\",\"name\":\"xxx\",\"email\":\"xxx\",\"mobile\":\"12312xxx\"}";
        String key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCMe0CmYZFqmhj9cnRYz8eP0ChRaz8LR8gVEtgNj4XuhpJOGR3xnGEjVvi0NrJx8DAsoJRvJDZS4fZu0094X4kPAB9dZ8z3KNT+g4LnqLufua0JhCaPo1XtlynTL2I7l9Zz+EV3q8eXiL0G3yCDtAZPix3UsHoQxi+Q9VFafWAuxwIDAQAB";
        String data = encryptData(params, key);
        System.out.println(data);

        String data1 ="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";
        String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAKNc0IpE+q61AL57xuY9PXlafqbs5IxqtAj89YTWLvGG9okVr+bcOwQUKhkFvXrTblFaWCfa820lM4n1GbpZ2eHB5K3cy6XcQMKXXjyMqmH2rUHzoLq+dZHoOaTV7FwtwEJPQ9IAjgiUlxKckkmkZglsmPFYy4I9ptsOVTyCb2WPAgMBAAECgYEAhYNDxN3fa43vD78lreJ7LYUyYcbWe5Rxk676EhjiwO8m6p7Y5nszmH/KcCzq0UmfrcmCpwAhVyDCYIv6/PyWPsWwtC3sWjitfBhLLMgiLnRUYpLDLHXdyrsUc7G4DJhVhz2HXlei+oVZNgqotUaanYLMw9TyJ/rOIOeDKuyvDzECQQDljPBApJauWPu7xO/l7qUVSOE6K0oB4VIQNiS454YjNnjA8RSU26M/vrY7hB1zn8DHSXNl/jEI6HJCvsYn6MALAkEAti+GyTiF3mKlCgB0hneR1XB3NhmXbTMn1kI7iUHBE4+01iWQi73uzE4yoD8WHz5rwkD/XBqshMpcCSNNgjcPDQJAe2S4nTccXJour6/ceVBAY2Gq2Jb+kGYrs9U3BkJGg9U7MhQlySML7S86TGHnZwkAGuBr7O6oMy6ohSKB6GAgIwJAZx65lRXIOBrvvZyKWD4/rmctItMTcfzdlJoCpuswBQl3WysCCQblroCoiSmMNP0Y82fk4lY6xiLgO0/fJFBT1QJBAJ9p++Ut8lNotZ617SF50o/AKWTiheauKDj6kwFtZSeNwBnPXATfrtAh8jNvopsY8e3K+5RPuB9m5kpmVHMsNEw=";
        String dData = decrytData("MvP9mtR9ZeUW+joPeOwJO6LEDDdQjiFimaND+fyVD43b9Zmw4enfgWBspJm0JqMIXOq+JhC/PGzry4DE15EtyOUKgT06Z58xuciNc1T6Piw3riPpZ2kwvqyQ+MFO+eUnAYSzYXpcZsJGwbzDV8cPReazWywzThOBV1quSIPUAkM=", "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBALJ0fC10AWOV6zwA45CfC0zr39vsmZAczSPhn4ck23c/0Un+2YIlZ9qqnIHZ8V9GMzjSa2TRTwkzrBs7XONZhYP8p8Y+rIlbUeymdVYDS90bIy2BCUiRnipQ8laA71BePhpR8WE7EekA1RYfCoffvg+hmDZPmuvpOtimnOeFzqSzAgMBAAECgYBUvc3uA6VZNerjroRhu7SxDV1bYQo3DgY4IL8RfOYexZZL7hI5pCuiAdyaKPVeycYvGHTZBi/i7b+vmsvsRimJDXxvneoxH2uVWszxByfGY8nJi9n1dc6nyk9TRgPOxn6snXiR6Bq95D+DdZp33OjmsaTnjgborX2Y4PqVUv70yQJBAN5jHkybdE6IPMyeH/DJDheyWLD1Y+1oC5bxSzgbBp/W00MqWFmmzOuXzPWM10eMOkKTH4ADL1DquXCCVak6hW8CQQDNbX0knebuIdJY8hpwxhnIYpj/Il5FuiRHhcHCEipZ3LGoHbNEeFQvH4bUw6O3wFn4F39F3Jk+p/BvDneGjJr9AkEAoC18UWlW6ImwYSEzmFb4U5Ed2Lem9gSO9HVv3EV7C/3LxQDnCgveZYK9GdAL73jNLc6STb39gqbOUn9N+8ou+QJAKT2y4vgEVYhat1qbbkqHAJCy0H2w2WS+RdGXjYOiFvpUfEDCLZvOoss8gVhpdMlcBo+JEJhx9381qMYJvuxZbQJAHKTmS4bWjsZUDIOUVVXdqTkaNzv2Pu80p+dsjhKkTtRwR5/UaY3gaQ29JC5AD3s/jOf7Gg+w1BI+qJV7WizA+A==");
        System.out.println(dData);
    }

    /**
     * 解密数据
     *
     * @param params 请求返回的参数
     * @param key    rsa私钥
     * @return
     * @throws Exception
     */
    public static String decrytData(String params, String key) throws Exception {
        // 1.decode
//        params = URLDecoder.decode(params, "utf-8");
        // 2.base64
        byte[] data = Base64.getDecoder().decode(params);
        // 3.rsa解密
        data = Helper.decrypt(data, key);
        return new String(data);
    }

    /**
     * RSA 数据加密
     *
     * @param data 数据字符串
     * @param key
     * @return
     * @throws Exception
     */
    public static String encryptData(String data, String key) throws Exception {
        byte[] bytes = Helper.encrypt(data.getBytes(), key);
        String params = Base64.getEncoder().encodeToString(bytes);
//        params = URLEncoder.encode(params, "utf-8");
        return params;
    }

    /**
     * 加解密辅助类
     */
    private static class Helper {

        private static final String ALGORITHM = "RSA";
        private static final KeyFactory keyFactory;

        static {
            try {
                keyFactory = KeyFactory.getInstance(ALGORITHM);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("No such algorithm '" + ALGORITHM + "'", e);
            }
        }

        private static byte[] encrypt(byte[] data, String publicKey) throws Exception {
            if (ArrayUtils.isEmpty(data)) {
                return ArrayUtils.EMPTY_BYTE_ARRAY;
            }

            PublicKey key = toPublicKey(publicKey);

            Cipher cipher = getCipher(Cipher.ENCRYPT_MODE, key);

            return doFinalInBlock(cipher, getBlockSize((RSAKey) key, true), data);
        }

        private static byte[] decrypt(byte[] data, String privateKey) throws Exception {
            if (ArrayUtils.isEmpty(data)) {
                return ArrayUtils.EMPTY_BYTE_ARRAY;
            }

            PrivateKey key = toPrivateKey(privateKey);

            Cipher cipher = getCipher(Cipher.DECRYPT_MODE, key);

            return doFinalInBlock(cipher, getBlockSize((RSAKey) key, false), data);
        }

        private static int getBlockSize(RSAKey key, boolean encrypt) {
            int size = key.getModulus().bitLength() / 8;
            return encrypt ? size - 11 : size;
        }

        private static byte[] doFinalInBlock(Cipher cipher, int blockSize, byte[] data) throws Exception {
            int length = data.length;
            if (blockSize <= 0 || length <= blockSize) {
                return cipher.doFinal(data);
            }

            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                int offset = 0;
                for (; offset + blockSize <= length; offset += blockSize) {
                    byte[] bytes = cipher.doFinal(data, offset, blockSize);
                    out.write(bytes);
                }

                if (offset < length) {
                    out.write(cipher.doFinal(data, offset, length - offset));
                }

                out.flush();

                return out.toByteArray();
            }
        }

        private static Cipher getCipher(int mode, Key key)
                throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(mode, key);
            return cipher;
        }

        private static PublicKey toPublicKey(String publicKey) throws InvalidKeySpecException {
            byte[] keyBytes = decode(publicKey);

            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);

            return keyFactory.generatePublic(keySpec);
        }

        private static PrivateKey toPrivateKey(String privateKey) throws InvalidKeySpecException {
            byte[] keyBytes = decode(privateKey);

            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);

            return keyFactory.generatePrivate(keySpec);
        }

        private static byte[] decode(String key) {
            return Base64.getMimeDecoder().decode(key);
        }
    }


    private static class KeyGenerator {

        private static final KeyPairGenerator keyGenerator;

        static {
            try {
                keyGenerator = KeyPairGenerator.getInstance(Helper.ALGORITHM);
                keyGenerator.initialize(1024);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("No such algorithm '" + Helper.ALGORITHM + "'", e);
            }
        }

        public static SimpleKeyPair generateKey() {
            KeyPair keyPair = keyGenerator.generateKeyPair();
            byte[] privateKey = keyPair.getPrivate().getEncoded();
            byte[] publicKey = keyPair.getPublic().getEncoded();
            return new SimpleKeyPair(privateKey, publicKey);
        }
    }

    private static class SimpleKeyPair implements Serializable {

        private static final long serialVersionUID = -3699965134810358040L;
        private final byte[] privateKey;
        private final byte[] publicKey;

        SimpleKeyPair(byte[] privateKey, byte[] publicKey) {
            this.privateKey = privateKey.clone();
            this.publicKey = publicKey.clone();
        }

        public byte[] getPrivateKey() {
            return privateKey.clone();
        }

        public byte[] getPublicKey() {
            return publicKey.clone();
        }

        public String getPrivateKeyString() {
            return Base64.getEncoder().encodeToString(getPrivateKey());
        }

        public String getPublicKeyString() {
            return Base64.getEncoder().encodeToString(getPublicKey());
        }
    }
}
