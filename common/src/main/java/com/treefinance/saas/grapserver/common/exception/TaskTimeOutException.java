package com.treefinance.saas.grapserver.common.exception;

public class TaskTimeOutException extends RuntimeException {
    private static final long serialVersionUID = -290315695168000010L;

    public TaskTimeOutException() {
        super();
    }

    public TaskTimeOutException(String message) {
        super(message);
    }

    public TaskTimeOutException(String message, Throwable cause) {
        super(message, cause);
    }

    public TaskTimeOutException(Throwable cause) {
        super(cause);
    }

    protected TaskTimeOutException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
