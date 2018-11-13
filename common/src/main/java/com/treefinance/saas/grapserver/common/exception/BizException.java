package com.treefinance.saas.grapserver.common.exception;

/**
 * @author luoyihua on 2017/5/12.
 */
public class BizException extends RuntimeException {

    private static final long serialVersionUID = 2024058202556452918L;

    private int code;

    private Object[] args;

    public BizException() {}

    public BizException(String message) {
        super(message);
    }

    public BizException(String message, Throwable cause) {
        super(message, cause);
    }

    public BizException(Throwable cause) {
        super(cause);
    }

    public BizException(String message, Throwable cause, boolean enableSuppression,
                        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public BizException(int code) {
        this.code = code;
    }

    public BizException(String message, int code) {
        super(message);
        this.code = code;
    }

    public BizException(String message, Throwable cause, int code) {
        super(message, cause);
        this.code = code;
    }

    public BizException(Throwable cause, int code) {
        super(cause);
        this.code = code;
    }

    public BizException(String message, Throwable cause, boolean enableSuppression,
                        boolean writableStackTrace, int code) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
    }

    public BizException(int code, Object[] args) {
        this.code = code;
        this.args = args;
    }

    public BizException(String message, int code, Object[] args) {
        super(message);
        this.code = code;
        this.args = args;
    }

    public BizException(String message, Throwable cause, int code, Object[] args) {
        super(message, cause);
        this.code = code;
        this.args = args;
    }

    public BizException(Throwable cause, int code, Object[] args) {
        super(cause);
        this.code = code;
        this.args = args;
    }

    public BizException(String message, Throwable cause, boolean enableSuppression,
                        boolean writableStackTrace, int code, Object[] args) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
        this.args = args;
    }

    public int getCode() {
        return code;
    }

    public Object[] getArgs() {
        return args;
    }

}
