package com.treefinance.saas.grapserver.common.exception;

/**
 * @author luoyihua on 2017/5/10.
 */
public class CallbackEncryptException extends CryptoException{

    private static final long serialVersionUID = 7094919998954093717L;

    public CallbackEncryptException(String message) {
        super(message);
    }

    public CallbackEncryptException(String message, Throwable cause) {
        super(message, cause);
    }

}
