package com.treefinance.saas.grapserver.common.result;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.treefinance.toolkit.util.crypto.RSA;
import com.treefinance.toolkit.util.crypto.core.Encryptor;
import com.treefinance.toolkit.util.crypto.exception.CryptoException;
import com.treefinance.toolkit.util.json.Jackson;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

/**
 * @author guoguoyun
 * @date Created in 2018/10/18下午10:27
 */
@JsonInclude(Include.NON_NULL)
public class SaasResult<T> implements Serializable {

    private Integer code;
    private String msg;
    private T data;

    public SaasResult() {}

    private SaasResult(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private SaasResult(T data) {
        this.code = 0;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

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

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE).append("code", code).append("msg", msg).append("data", data).toString();
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

    public static <T> SaasResult success(T data) {
        return new SaasResult<>(data);
    }

    public static SaasResult failure(Integer code, String msg) {
        return new SaasResult<>(code, msg);
    }

    public static SaasResult failure( String msg) {
        return new SaasResult<>(-1, msg);
    }

    public static <T> SaasResult<String> successEncryptByRSAResult(T data, String rsaPublicKey) {
        String encryptData = EncryptHelper.encryptResult(data, rsaPublicKey);
        return successResult(encryptData);
    }

    @Deprecated
    public static <T> SaasResult<T> failResult(String errorMsg) {
        return failure(errorMsg);
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
