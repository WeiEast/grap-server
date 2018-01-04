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
        String params = "EOzn179mcRJfOaYYQo7fMvR%2BvcY1%2FMRL%2FBbEMT9nwg9fExwbedLyn8XnNHmLV8nfZG5xZXvtbsLvld5UHcION041XQO17HTiAkWHkbeLmbUlO80D9Qb5W4KgAzLDElyNMYxbzjz4drR464Y0j8hwa2gqX5ZQEWvghFvNlHoqZsvQGo%2FsJ4B8VyWfwcljo5O3o4iGOFZUQ2CnPb4JvhX3Hvi045utS%2B1ND8Trs658fy707s%2FqxhMgsPp357uSm%2FQS4oKVksuHvOJQeQIrvTyBlIhLA%2BuBMHoMcbZ70810O8Bl2sZ%2FjCSzjAwMqMNT%2BiDirAP7ypIPZLfen5Os1DtsVlKHYk%2FXwSRir4lnHmFdLPNaWoAhlqplVVAopVT3ybRmQAGtOxRfAgCq2xlzUtbpuKP8%2FcWo2MA6zzbBg9i43eY0M%2Bh7PGIbJSlUFqdfeYGundEuvj5Fj1qeyf3yXO%2F1QGxaRh3v71bak6a0I4T7daAF4htK93yll0M1U5x9c3LalUw3ERi1Puw9vVkyWPljnIPEJq1sOkeOaWQEqLVbPeEH5NxFouRvynH7AH92n9fpQFaGkN84BJ0ibs3tfY9APxqvPuLOmh%2FgvHgasaWxpS1btxlZrQATOO2IQ8TVjAo2RPdbSSvtt1KzqFLMbWTHWAZuEr1e71%2FTD43jTPWKVeM%3D";
        String key = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBANtHr+JgvD7vAgxmPma1R1Ta23rn/eu+5GahlDicFWY7zrmRw3nAW0B1GachmbQr4Sg3F0Mw6ID7ep9mUwl/BWJEob7Idf+SIc9d8bTI5Y0Gi+Nkoi/NSAx1pW3Mu6yGSGXGAN5jA6vFYlJBJaGx2Bl4TiSxMEaxofYoYt6Qz71/AgMBAAECgYB8jDMyeY9yj36yXECFReHGKKkRHkPzavF/+baekMj20HGSpWBJ/x/VdK2laEhNNb8lgGUOHPFykWQMtankcuky4mGExLyGWfG75+MHY1wgVVENvM9Wviv+aOEn+jsLO+P9c9PzgW3HLXfF2yZLAy3sSWDD+bhB/xw/zZsyfmJMAQJBAPI/PrGcd2+31Uqt4Z26PoI+u84C4tF5RwSV756Rr7toa52+UwKFGXm0FRlv08diveMliJBhdxRCFbLySRKj+EECQQDnuqU9FBrg8Q+9xhz1jqTwybHrQq430FXUwbDwW5OMw75aiXeyyvzX/AMJJYELkq+nxmuj1Q65nGeEDdNSkEW/AkAR2nNmZ5+tziCcFmCQXU+KDdGNh0zsH0IKpno4d4g6CaIUyBgaEXElNQ5xB3rWMQLJoNSFv4klJxGuVIjqJMeBAkEAzl61m8CU83EbwLl3vuRrVhl5/veiagh6+AALUPNtYzZMBxtX433NUZiZJ4Mj+qeHEdaus/ghpgF5WuxpH6VmxwJAC3AmtJ7UTOdT3zcTGB3auvwe+AScXJgs208yuaHs84iILJzEnAVtOFWQ+atqNZc1c4pz4eKJwwM0LWYyFocVpA==";
        String data = decrytData(params, key);
        System.out.println(data);
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
        params = URLEncoder.encode(params, "utf-8");
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
