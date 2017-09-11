package com.treefinance.saas.grapserver.common.exception.base;

/**
 * 基于标签
 * Created by yh-treefinance on 2017/9/1.
 */
public class MarkBaseException extends RuntimeException {
    /**
     * 标记
     */
    private String mark;

    /**
     * 错误消息
     */
    private String errorMsg;

    public MarkBaseException(String mark, String errorMsg) {
        super(errorMsg);
        this.mark = mark;
        this.errorMsg = errorMsg;
    }


    public MarkBaseException(String mark, String errorMsg, Throwable throwable) {
        super(errorMsg, throwable);
        this.mark = mark;
        this.errorMsg = errorMsg;
    }

    public String getMark() {
        return mark;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
