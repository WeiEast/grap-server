package com.treefinance.saas.grapserver.common.exception;

import com.treefinance.saas.grapserver.common.enums.ECode;

/**
 * @author:guoguoyun
 * @date:Created in 2018/10/31下午3:45
 */
public class AppIdFormatException extends UnAuthorizedException {


    public AppIdFormatException() {
        super();
    }
    public AppIdFormatException(String message) {
        super(message);
    }
}
