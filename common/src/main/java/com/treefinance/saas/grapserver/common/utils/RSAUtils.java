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
        //
        // String params =
        // "{\"idcard\":\"110000199001041119\",\"name\":\"xxx\",\"email\":\"787394773@qq.com\",\"mobile\":\"15871362990\"}";
        // String key =
        // "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCMe0CmYZFqmhj9cnRYz8eP0ChRaz8LR8gVEtgNj4XuhpJOGR3xnGEjVvi0NrJx8DAsoJRvJDZS4fZu0094X4kPAB9dZ8z3KNT+g4LnqLufua0JhCaPo1XtlynTL2I7l9Zz+EV3q8eXiL0G3yCDtAZPix3UsHoQxi+Q9VFafWAuxwIDAQAB";
        // String data = encryptData(params, key);
        // System.out.println(data);



            String data1 =
                "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";
        String privateKey =
            "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAIHVSUvJnBk4vcbNtKjOsBp1Egy8mpjm8EA3H/VEBRotqAqRYrlLrcBBJgXCgWE8B6vJXyfVScsQtXwBiH/rl7HOpRQ536d1Q/RQLqwDjvO7XlsyyVj2VMmNU54ORaEIl/dSPY9jKgbCFznUwCis/5wz105blSVSqdvecEHQucyHAgMBAAECgYAZtNE+YDy4AbQRmMkcY1Aa7PMV8oHppFANZty9Ayq/dCrBbOfPKFzOpBeTLF/RkME0Ejr0+BYUTsqgMu2D5nGySPWiH6i/6B4ObZ0WUSzX++uuuvhmbouILra4AIS6bzMK/RKBHkF30Zd02/8vfb+4rQlswzgEk80/j886tuq8gQJBAOMmDagaCK6cKYMy93C+5ypx2shlwl2xZ6gw2+H76e/boJ2X9BWdcS9f8UhEo9utXjwNEvZa5r2X3W9EhFDzbK0CQQCSUu4owigSI2bScBsyeWCsoSob9vlCvXMN4Kcy+zctTagkwWufkdSlCdvkyZNmAzNMqTXHnnhuNUgKf9BRQ/CDAkBUTldbQO5gAE3YCB6WlgQuWLufDUWqiKG0Zw31Pg6Bm75tP2y0aQ8NSkq/S9qVOi9zklarYOmrDKZ/GasScIaxAkBcqdYWy7YSnoa8F7CxizpUuI9xPDtoL4+QJ0fbOkocD4S/Ghps6C5RAehWf+0vejFfh/z92HaN3IIt2/lxrWO/AkBZn6/jcHe7tk82Q++UsLamBm1S33En02PykZ2IIdOj4N4slGKsgzqG/2PniUmGnApfeal+ybXY/qCxH5k/QlIX";
        String dData = decrytData(data1, privateKey);
        System.out.println(dData);
    }

    /**
     * 解密数据
     *
     * @param params 请求返回的参数
     * @param key rsa私钥
     * @return
     * @throws Exception
     */
    public static String decrytData(String params, String key) throws Exception {
        // 1.decode
        // params = URLDecoder.decode(params, "utf-8");
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
        // params = URLEncoder.encode(params, "utf-8");
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

            return doFinalInBlock(cipher, getBlockSize((RSAKey)key, true), data);
        }

        private static byte[] decrypt(byte[] data, String privateKey) throws Exception {
            if (ArrayUtils.isEmpty(data)) {
                return ArrayUtils.EMPTY_BYTE_ARRAY;
            }

            PrivateKey key = toPrivateKey(privateKey);

            Cipher cipher = getCipher(Cipher.DECRYPT_MODE, key);

            return doFinalInBlock(cipher, getBlockSize((RSAKey)key, false), data);
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

        private static Cipher getCipher(int mode, Key key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
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
