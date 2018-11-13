package com.treefinance.saas.grapserver.common.exception;

/**
 * @author guoguoyun
 * @date Created in 2018/10/31下午5:31
 */
public class AppIdNotActiveException  extends ForbiddenException {

    private static final long serialVersionUID = 8299016601220035642L;

    public AppIdNotActiveException() {}

    public AppIdNotActiveException(String message) {
        super(message);
    }

}
