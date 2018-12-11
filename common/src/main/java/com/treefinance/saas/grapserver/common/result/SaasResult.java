package com.treefinance.saas.grapserver.common.result;

import com.alibaba.fastjson.JSON;
import com.treefinance.toolkit.util.crypto.RSA;
import com.treefinance.toolkit.util.crypto.core.Encryptor;
import com.treefinance.toolkit.util.crypto.exception.CryptoException;
import com.treefinance.toolkit.util.json.Jackson;

/**
 * @author guoguoyun
 * @date Created in 2018/10/18下午10:27
 */
public class SaasResult<T> {

    private String msg;

    private Integer code;

    private T data;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public SaasResult() {
    }

    public SaasResult(T data) {

        this.data = data;
    }

    public SaasResult(String msg) {
        this.msg = msg;
    }

    public SaasResult(String msg, T data, Integer code) {
        this.msg = msg;
        this.data = data;
        this.code = code;
    }

    public static <T> SaasResult<T> successResult(T data) {
        SaasResult<T> result = new SaasResult<>(data);
        result.setCode(0);
        result.setMsg("success");
        return result;
    }

    public SaasResult(String msg, T data) {
        this.msg = msg;
        this.data = data;
    }

    public static <T> SaasResult<String> successEncryptByRSAResult(T data, String rsaPublicKey) {
        String encryptData = EncryptHelper.encryptResult(data, rsaPublicKey);
        return successResult(encryptData);
    }

    public static <T> SaasResult<T> failResult(String errorMsg) {
        return failResult(null,errorMsg,-1);
    }

    public static <T> SaasResult<T> failResult(T data, String msg,int code) {
        return new SaasResult<>(msg,data,code);
    }

    public static <T> SaasResult<String> failEncryptByRSAResult(T data, String errorMsg, String rsaPublicKey) {
        String encryptData = SaasResult.EncryptHelper.encryptResult(data, rsaPublicKey);
        return new SaasResult<>(errorMsg, encryptData);
    }

    private static class EncryptHelper {
        private EncryptHelper() {}

        public static Encryptor getEncryptor(String publicKey) {
            return RSA.createEncryptor(publicKey);
        }

        public static String encryptResult(Object data, String publicKey) {
            Encryptor encryptor = getEncryptor(publicKey);
            byte[] json = Jackson.toJSONByteArray(data);
            String result;

            try {
                result = encryptor.encryptAsBase64String(json);
                return result;
            } catch (CryptoException var6) {
                throw new RuntimeException("使用RSA加密数据时失败,data=" + JSON.toJSONString(data) + "rsaPublicKey=" + publicKey,
                    var6);
            }
        }
    }
}
