package com.treefinance.saas.grapserver.common.exception;

/**
 * 请求失败异常
 * @author yh-treefinance on 2017/5/18.
 */
public class RequestFailedException extends RuntimeException {

    private static final long serialVersionUID = -290315695168000010L;

    /**
     * 请求地址
     */
    private String requestUrl;

    /**
     * http 状态码
     */
    private int statusCode;

    /**
     * 返回结果
     */
    private String result;

    public RequestFailedException() {
        super();
    }

    /**
     * 异常创建
     */
    public RequestFailedException(String requestUrl, int statusCode, String result) {
        super("httpCode = " + statusCode + ",result=" + result);
        this.requestUrl = requestUrl;
        this.statusCode = statusCode;
        this.result = result;
    }

    /**
     * 异常创建
     */
    public RequestFailedException(String requestUrl, int statusCode, String result, Throwable cause) {
        super("httpCode = " + statusCode + ",result=" + result, cause);
        this.requestUrl = requestUrl;
        this.statusCode = statusCode;
        this.result = result;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getResult() {
        return result;
    }

}