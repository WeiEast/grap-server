/**
 * Copyright © 2017 Treefinance All Rights Reserved
 */
package com.treefinance.saas.grapserver.common.exception;

/**
 * @author chenjh on 2017/6/23.
 * <p>
 * 爬虫接口失败异常
 */
public class CrawlerBizException extends BizException {

    private static final long serialVersionUID = -8261835387737450392L;

    public CrawlerBizException() {
        super();
    }

    public CrawlerBizException(String message) {
        super(message);
    }

    public CrawlerBizException(int code, String message) {
        super(message, code);
    }

    public CrawlerBizException(String message, Throwable cause) {
        super(message, cause);
    }

    public CrawlerBizException(Throwable cause) {
        super(cause);
    }

    protected CrawlerBizException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
