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
