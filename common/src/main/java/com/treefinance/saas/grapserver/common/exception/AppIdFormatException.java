package com.treefinance.saas.grapserver.common.exception;

/**
 * @author guoguoyun
 * @date Created in 2018/10/31下午3:45
 */
public class AppIdFormatException extends UnAuthorizedException {

    private static final long serialVersionUID = 4880524448147674197L;

    public AppIdFormatException() {
        super();
    }

    public AppIdFormatException(String message) {
        super(message);
    }

}
