package com.treefinance.saas.grapserver.common.exception;

/**
 * @author hanif
 */
public class UnknownException extends RuntimeException {

    private static final long serialVersionUID = -290315695168000010L;

    public UnknownException() {
        super();
    }

    public UnknownException(String message) {
        super(message);
    }

    public UnknownException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownException(Throwable cause) {
        super(cause);
    }

    protected UnknownException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
