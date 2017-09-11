/**
 * Copyright © 2017 Treefinance All Rights Reserved
 */
package com.treefinance.saas.grapserver.common.exception;

/**
 * Created by chenjh on 2017/6/23.
 * <p>
 * 爬虫接口失败异常
 */
public class CrawlerBizException extends RuntimeException {
    public CrawlerBizException() {
        super();
    }

    public CrawlerBizException(String message) {
        super(message);
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
