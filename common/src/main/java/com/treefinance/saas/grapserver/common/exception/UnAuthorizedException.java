package com.treefinance.saas.grapserver.common.exception;

/**
 * @author guoguoyun
 * @date Created in 2018/10/31下午3:38
 */
public class UnAuthorizedException extends  RuntimeException{

    private static final long serialVersionUID = -8210918888367319225L;

    public UnAuthorizedException() {
        super();
    }

    public UnAuthorizedException(String message) {
        super(message);
    }

    public UnAuthorizedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnAuthorizedException(Throwable cause) {
        super(cause);
    }

    protected UnAuthorizedException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
