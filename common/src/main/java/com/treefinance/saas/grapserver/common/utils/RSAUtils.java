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
        String privateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAK6azTbf9fzjmkncZ4Y490B0vYwMJ3waRs3iAzFKZ0n932Kz2Pw5nL0L4Qd4bY9NoFM0oc+XDOxDMd2KvdoBYMsuDdyFvMJ9lN4R1y3ZWHsJ4rFkH2bfKUJDBCQflKd6h6t4d8iP2/WoURaYLlSSF7d9uwqWKkV3oYXjdaP5H1WVAgMBAAECgYA60r42MYeO9iLhpVZfOsvZtDqzdtjKj5OxAuaucXGQ1O2wNATzHHm4msrqGGFYuP89aOrmo+3fnteq2nYRfwtbeaWwi+kXK6BsyEpQW7QjcgAY+Jt32HM0OSSOGHI0MV9zDxuyeEjd7wohNJyaADzLxDJJcugxlHGAkbMDMn8oFQJBANZQkQGoufjO29QxJ5Knb9pHnU46CSeMK0gMHvuDtT4BVTZKFZh7Q8tEL7TJhgluEaaXJh4YPZCWFjwIVLcdpO8CQQDQkPFiFjNXXJK53JrfvHlwSVYBRoNX1Dn7FlxYRhbMrK/y6H67dr7T1PRaTMl4A9fZOgTxkGtazjRJYxKZk9W7AkAv0AeZxCfBT30RqOm4OSymGQYKNr5mGUpqp3GeETM1Ixz8oKiSvSVRyuONxMvnfKvpa50wZ54MjL9nI0CrHaz3AkBlO5UOKPxukO5e7Gb35gGDsPTf1Zv2y3oBF49nAU4btk4ItzScRTzGkPyxv8eiSys/stunbJPPFXtni4EBrjH1AkAcuO/PfxB2UnCRnNLhoWPMOvgQ0TfumGj1lrboina+Qclez+dUm+oxeLkVPRvN35xS9HmDqzcEVejDwuiJFLqm";
        String dData = decrytData(data, privateKey);
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
