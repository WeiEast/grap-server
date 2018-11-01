package com.treefinance.saas.grapserver.common.exception;

/**
 * @author:guoguoyun
 * @date:Created in 2018/10/31下午5:32
 */
public class AppIdNoMessageException extends  ForbiddenException {

    public AppIdNoMessageException(String message) {
        super(message);
    }

    public AppIdNoMessageException() {
    }
}
