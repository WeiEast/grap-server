package com.treefinance.saas.grapserver.common.exception;

/**
 * @author luoyihua on 2017/5/12.
 */
public class ParamsCheckException extends RuntimeException {

    private static final long serialVersionUID = -4606174100558456104L;

    public ParamsCheckException() {}

    public ParamsCheckException(String message) {
        super(message);
    }

    public ParamsCheckException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParamsCheckException(Throwable cause) {
        super(cause);
    }

    public ParamsCheckException(String message, Throwable cause, boolean enableSuppression,
                                boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
