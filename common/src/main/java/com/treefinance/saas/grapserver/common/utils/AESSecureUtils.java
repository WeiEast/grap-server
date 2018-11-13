package com.treefinance.saas.grapserver.common.utils;

import com.datatrees.toolkits.util.crypto.AES;
import com.datatrees.toolkits.util.crypto.core.EnhancedEncryptor;
import com.datatrees.toolkits.util.crypto.exception.CryptoException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * 数据加密解密
 *
 * @author hanif
 */
public final class AESSecureUtils {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final byte[] IV = {0x30, 0x31, 0x30, 0x32, 0x30, 0x33, 0x30, 0x34, 0x30, 0x35,
            0x30, 0x36, 0x30, 0x37,
            0x30, 0x38};

    /**
     * 加密
     */
    public static byte[] encrypt(String secretKey, byte[] object) throws CryptoException {
        EnhancedEncryptor encryptor = AES.of("CBC", "PKCS5Padding").getEncryptor(secretKey);
        return encryptor.encrypt(object);
    }

    /**
     * 解密
     */
    public static String decrypt(String dataSecretKey, byte[] object)
            throws Exception {
        // 加密串转成SecretKeySpec对象
        SecretKeySpec secretKey = new SecretKeySpec(Base64.getDecoder().decode(dataSecretKey),
                ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);

        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(IV));
        byte[] data = cipher.doFinal(object);
        return new String(data);
    }
}
