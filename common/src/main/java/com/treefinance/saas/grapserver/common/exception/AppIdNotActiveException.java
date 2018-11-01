package com.treefinance.saas.grapserver.common.exception;

/**
 * @author:guoguoyun
 * @date:Created in 2018/10/31下午5:31
 */
public class AppIdNotActiveException  extends ForbiddenException {
    public AppIdNotActiveException(String message) {
        super(message);
    }

    public AppIdNotActiveException() {
    }
}
