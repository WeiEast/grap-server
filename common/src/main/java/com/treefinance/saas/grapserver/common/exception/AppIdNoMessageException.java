package com.treefinance.saas.grapserver.common.exception;

/**
 * @author guoguoyun
 * @date Created in 2018/10/31下午5:32
 */
public class AppIdNoMessageException extends  ForbiddenException {

    private static final long serialVersionUID = -1608288632291003250L;

    public AppIdNoMessageException() {}

    public AppIdNoMessageException(String message) {
        super(message);
    }

}
