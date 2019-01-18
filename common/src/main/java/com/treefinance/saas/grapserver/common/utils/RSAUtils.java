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
