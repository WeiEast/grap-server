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

        String data1 ="YDt4MqEGK6NHBLzLmMCB0+jY09OcGI+uYg5Wh+8gcTiImVSNsgmC8C1qU10bk3E9XPOHAyLPfI0G3GfsvLnNi196kcoUE2tYhU91X7iQwlxDEQhPq7IhigajQUL32sKDunnZ24ovTp5hirBJ0fOpaEhrD+NaSP6ltcZ+caoNsaYbVQC8St9QEGDfEYI/tdfkhAuY2t42mmKPtd5Zi7eyVj4SKD8rKKLSSVl901QKG7cFCZZfpWtmLx7rLenjEgLqG2NmEb7RQJABztjsS/phPsg99zljcAsJ2FB2O4hSlE18LdsF4OIuKmY24V6lca6JzPSL3TZWGO23lM0FhpAJdyrGuBqnjmsfNS6y7fkCBHzxv3Ig+eYotWj/5uGtTPF0aH+PWsyNc41QbIgluISWKXT1cWZeQvNglXWaqg4dvWx0vIju2RBJoy0ZVJsnk6737s9mopNF9KgupreAVvO2Cp6xmTBpU33d/pXy6966yGZRvq3cwp3QaKNWH971xOW0b20sdrA0mJJU/1/qwsR/hWZzj9wl2XIFh26kXP+/Wdfb/LXxmpJ9RxbBEaDtmzUsSWdJri6BJJgLRkdIPEp5iwbsa3cN9zDCve5wLP6HYDhw3v5hpNkPHqzFANJwz1yozT1/RTEHPdTPoeRdosyv81umo0CjzoDDHzbZd4utEcKD9lDRELrQmJD4e5v/qfnA5JkD7oUDmAilW71f9eWKCHYXaCm+XkAcMhv2fAtpghWF6yClWOf3hONDffNlBGUdSX1b+rgZrfb/8zzO20wXjSrf6JuMKdLXUw0IIOb2fpPXYCc6G1/Y2Dhtwb19TlvCdt/pcfr8FknWgGjdGMIk5aI30HLfBoaXWsxGXOkU0uIy9vy9t+vrtVVBNjBc1swFtIX6wrW744I5bXFsKmbEUbg1qihzB+lRxZ+a4jCgn+WJu3xpXgEeSHTrYnUhFlWilQqmFwDeRyfgYTW1IVjgSAx242fA6JIU7bWNwwhsSXuqw1o2Dlg2t7o0rjDe7Q3AG6ualDxwIrpuz7XnmvEF04zCWTcTd2zhmEABs3JB6ZOQ4KXHni2yGfRgwP1CMfjw4gh4S2unLWqveuQiS7JqFkmvEj26E9iHRDq1F+mnUwgXwRoWzgaC8qm79KaTa4Rz+LXhgod7zydjErtv80jngetBSkDiSKUo4auOB8H1fuEcGFGt3dN1e7hYXIZADmdgwJ18F58REiV045lxBbgCflO/i3DhK2ybHGUbiAuAZ7yPr5fC0By6cTcFlgESfjpKc+gvJF3WO5O567//f78oPhEvwOWHCmTHYhPPiGoJq547JXTaPRco8Wz2h3ONzI7P2PzzKxAOelMoaMiHkNdsRw==";
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
