package com.treefinance.saas.grapserver.common.exception;

/**
 * @author guoguoyun
 * @date Created in 2018/10/31下午5:31
 */
public class AppIdInvalidException extends  UnAuthorizedException {

    private static final long serialVersionUID = 7294549782874086612L;

    public AppIdInvalidException() {
        super();
    }

    public AppIdInvalidException(String message) {
        super(message);
    }

    public AppIdInvalidException(String message, Throwable cause) {
        super(message, cause);
    }

    public AppIdInvalidException(Throwable cause) {
        super(cause);
    }

    protected AppIdInvalidException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
