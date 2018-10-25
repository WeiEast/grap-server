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

        String data1 ="NvVdpipe7zPOT5k4AEWGezVi3UzfQTvD+aOKiwYPcq16h5cNKTUrz2k0/u5fUQnchjuMERqK3maAieAkH2L26DMoAwWGvsuSh/qXdoYIJu+tUtCbYqjW6mh4GcF9nF4AeOrqmgmd2oLx+tKXSLNoZP9IIxW50XKU0lY6AdhHKxwZsKBWxSCgFS7sLgwn2EpxbDXdadL8hHJoip17IUDE+3p1CY8g8eGNtTpcAZJB3oLPi8z0d0sqYxlV6r7QGn7VKlo2iZDOTJH3uGD9kEXQDO9Z5UBP/n1ffbCy2h0VuGJEO1Mhh0gfro3PvlsGp+ODvX8XCSUGPamG4CRflCl0W3/iyaS4TD/KKcVyaMFM0Oe9dGI8SYuwioJtbFPRNk/RkK/O4oKgHculyy2SfugY0AnBavnAkRAB6eVYool6yFZezqRR7pK2I3T3uNam74Vt/nqHL+o4y5oqZN3WAfkwHvmUQ/OP234aAAU1DRzJti+f+SQcFBNiZqF/DsxGHh0BZqt+9W70IRx7VwIyAWO7tKioWUkFopXudDVM+Ro8iu/kPEu5Mtfi92+zln6h9Kol+QPqnyR2ouuhX7fMLEApo+BGEDMXcpjfxnEdYqAd3mWE9XJ3FsS3JU1rBbtwyiPW1OKap21NfV0MfpJ1PHeLTRmg/ovXXViSf7V0GfM3vv9hQNYcUeTdC2RE/AeDo0wZXO9FKXjdnirpKDAqsgT/0evt9P2z4T4DB4Nz4Nmobr3/GmNcZ62jMeyQV/yHc6aAZTs+Lo+vLT5Q304H8U/ogoVdaH1lZg577wjtyUj+pHwpWUubTLqKTkNiqG8s9pqWTGq31FwjTcY7AlGYK1AToglfOS2u7r8rumP3jJTDcW9GUk3cP1FI1V8NlvwP7/jkC5Ab+QAkuj6lyD7dGyoJsTxwiZdnRUVfh/PyKAzBqRNzSErqn4meFtvMwcBNqcnCIWJNnZXYb18hIWoRkeDdGoX3ksqZAX21qxJqw9g+mr27hoswHGoHEXq/eMRWbGqIWaIG8oeHj+CwN9Kaps2Mnkks11BGF070mcZd/69wKL/mqFQUTAVl55vRcNO1AUXWGIA8pbxrmM0uOoqFk256ejTgeXBi61u77J5EiqGIp2xGMag8UkRSmApVDcujafvtEWZZ6ZtTZlKw73vszeCJvVSGy2m5jSQBJFDQ7+PRDWlNfLEr9QtOeNCYi7M7WpVo8TJjM0OIQFpgbkJtWsYOywXHP7xBZU6yRlspyYVK6u7M/X1llaRsNUA17YZu8OZkwnYmgXVGf6uvX0veFXcVgDMRIy6adKSF9YWgHYM2V+ZViOkJtC4TN7otJn9cTU3XUkbqISvaWAGiL0+oPXJNBg==";
        String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAKNc0IpE+q61AL57xuY9PXlafqbs5IxqtAj89YTWLvGG9okVr+bcOwQUKhkFvXrTblFaWCfa820lM4n1GbpZ2eHB5K3cy6XcQMKXXjyMqmH2rUHzoLq+dZHoOaTV7FwtwEJPQ9IAjgiUlxKckkmkZglsmPFYy4I9ptsOVTyCb2WPAgMBAAECgYEAhYNDxN3fa43vD78lreJ7LYUyYcbWe5Rxk676EhjiwO8m6p7Y5nszmH/KcCzq0UmfrcmCpwAhVyDCYIv6/PyWPsWwtC3sWjitfBhLLMgiLnRUYpLDLHXdyrsUc7G4DJhVhz2HXlei+oVZNgqotUaanYLMw9TyJ/rOIOeDKuyvDzECQQDljPBApJauWPu7xO/l7qUVSOE6K0oB4VIQNiS454YjNnjA8RSU26M/vrY7hB1zn8DHSXNl/jEI6HJCvsYn6MALAkEAti+GyTiF3mKlCgB0hneR1XB3NhmXbTMn1kI7iUHBE4+01iWQi73uzE4yoD8WHz5rwkD/XBqshMpcCSNNgjcPDQJAe2S4nTccXJour6/ceVBAY2Gq2Jb+kGYrs9U3BkJGg9U7MhQlySML7S86TGHnZwkAGuBr7O6oMy6ohSKB6GAgIwJAZx65lRXIOBrvvZyKWD4/rmctItMTcfzdlJoCpuswBQl3WysCCQblroCoiSmMNP0Y82fk4lY6xiLgO0/fJFBT1QJBAJ9p++Ut8lNotZ617SF50o/AKWTiheauKDj6kwFtZSeNwBnPXATfrtAh8jNvopsY8e3K+5RPuB9m5kpmVHMsNEw=";
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
