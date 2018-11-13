package com.treefinance.saas.grapserver.common.exception;

/**
 * @author guoguoyun
 * @date Created in 2018/10/31下午5:32
 */
public class UniqueidMaxException extends ForbiddenException {

    private static final long serialVersionUID = 7205439859428301817L;

    public UniqueidMaxException(String message) {
        super(message);
    }

    public UniqueidMaxException() {
        super();
    }

}
