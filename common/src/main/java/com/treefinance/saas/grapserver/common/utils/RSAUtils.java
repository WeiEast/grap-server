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

//        String params = "{\"idcard\":\"110000199001041119\",\"name\":\"xxx\",\"email\":\"xxx\",\"mobile\":\"12312xxx\"}";
//        String key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCMe0CmYZFqmhj9cnRYz8eP0ChRaz8LR8gVEtgNj4XuhpJOGR3xnGEjVvi0NrJx8DAsoJRvJDZS4fZu0094X4kPAB9dZ8z3KNT+g4LnqLufua0JhCaPo1XtlynTL2I7l9Zz+EV3q8eXiL0G3yCDtAZPix3UsHoQxi+Q9VFafWAuxwIDAQAB";
//        String data = encryptData(params, key);
//        System.out.println(data);

        String data1 ="b28SKWVc4d9DwjXijTNtxqU9BY8WFLy9a4GUgGWoXNNxOty65%2BVOGBI8t9Ug1LUenPseE%2BnKLQEegm69m9ErBwhHi5%2Fp%2FyEeq%2B2ZdJvvT1ltw1qaLF7QrKnPs1znMGzQp%2BEdXKGC9yw0ovXJV1NN5sKLn%2BI7hQv0UH28YAVWykUv1yyfEqLY1pWvy9L9CJ%2Fwf%2Bzc%2FPiEpaUkfNsA03oJ8WMOaNjRPEvTngPG27V0Wp799ssixxJCJehZ2umok%2Bac6ajRmcf4gUDL8f2MFX%2BsT9dR%2F1LISHaiwA1N2gP9AbbSY4GDI%2FVZkSzMTFdMnEIzmk39ZMSs8K3pexcuviyEkg%3D%3D";
        String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIT0AQ9e2pyIHf8PVeJ0WeDSuid53JLCs8H3ZkZOQIESAGJe/T8zndMCpF4wBY0vZFr5wIuDFglJfJWp3ydgzrAKDJ9dCBWzsRn08pTlHnDAuoF70eV3CxpvO9r94+5DcwJAZStsO1PTsP+39dOOaR7SIczRWj2BmM3wnk5VrHtNAgMBAAECgYB3KYdlslt33pwcBi+w9x2zXsvQI+3pcC7TE52MOAiiZnMHuCF9KxMyk/SkIqBnKWnmDGFh6YyvqDjMn2NOFPtKCrXv4ozyHQMdU2/qHGbGW4/44xdqlcqJXsFZ2zr7DQedPb6KaHzm/0quj3fRmf3kSxpUbC2a5AMxW3CrvGR/gQJBAMoAJm4OCwgTBQzChLCAXHp6ZjOtV9aLeRIh5AP8qWGBCI3wytHsWx37GqGVC+BiQoyOixDrG6BsrabJ52TNiO0CQQCofpzaKHVpw16W+HDV53trgf0MWtUbHQc9vHkV0Zt2u8DH7b/80kDcKRfWtxqTJJQEIzZbYz4hqmNDb53UHk/hAkEAkD1NM7bpX6mdKIBZmWL/JiHyiqITn8pV+IGkvMgU9t/ZiOlRGlcItT7RxGGWanIfjRhX6wvG3WXdFcUEavFs/QJAG3VGqmade7bS21dxS+qFOmfcjP7ga6K3Y2Are1rFMvw11l7wQosg9r/bmqoMMp5rYKE8Yngxz9qsgnjWB5NJAQJARX8Ud6bI0RxMFZMt/uFMNCh6aewPPjv0GgJJxc6wvrb927SIbRLgUcAozcw+YEWWYWaYMKQBHfmQg12OQl+LeA==";
        String dData = decrytData(data1, privateKey);
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
//         1.decode
        params = URLDecoder.decode(params, "utf-8");
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
