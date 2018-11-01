package com.treefinance.saas.grapserver.common.enums;

import java.util.Objects;

/**
 * @author:guoguoyun
 * @date:Created in 2018/10/30下午2:48
 */

public enum ECode {

    EXCEPTION(200, "服务器内部异常", "EXCEPTION", "具体含义自定义"),

    UNAUTHORIZED_APPIDINVALID(1, "商户未授权", "AppIdInvalidException", "商户未授权,isvalid=false"),
    UNAUTHORIZED_APPIDFORMAT(2, "商户ID不符合格式", "AppIdFormatException", "appId不符合格式"),
    FORBIDDEN_APPIDNOTACTIVE(3, "商户被禁用", "AppIdNotActiveException", "appID被禁用，isActive=false"),
    FORBIDDEN_APPIDNOMESSAGE(4, "商户无可用数据", "AppIdNoMessageException", "appID无信息"),
    FORBIDDEN_UNIQUEIDMAX(5, "商户访问超过阈值", "UniqueidMaxException", "商户访问超过阈值"),
    VALIDATION(6, "传入参数不合法", "ValidationException", "具体含义自定义"),
    CRPTOR_REQUESTDECRYPT(7, "解密请求参数异常", "RequestDecryptException", "解密请求参数"),
    CRPTOR(8, "加解密方法出现异常", "CryptorException", "加解密方法出现异常求参数"),
    CRPTOR_RESPONSEENCRYPT(9, "加密返回参数异常", "ResponseEncryptException", "解密请求参数"),
    BIZ(10, "业务异常", "BizException", "自定义"),
    PARAMSCHECK(11, "参数异常", "ParamsCheckException", "具体含义自定义"),

    UNKNOW(201, "服务器内部异常", "UnknownException", "调用taskcenter异常"),
    TASKTIMEOUT(202, "服务器内部异常", "TaskTimeOutException", "任务超时异常"),
    REQUESTFAILED(203, "服务器内部异常", "RequestFailedException", "请求失败"),
    NOTFOUND(204, "服务器内部异常", "NotFoundException", "转发的URL没有对应服务"),
    TIMEOUT(205, "服务器内部异常", "TimeOutException", "上游服务器没有响应，超时"),
    RESPONSE(206, "服务器内部异常", "ResponseException", "返回数据异常"),
    CRAWLERBIZ(207, "服务器内部异常", "CrawlerBizException", "爬树业务异常，具体含义自定义");

    /**自定义code码*/
    private int code;
    /**返回给第三方的错误信息*/
    private String msg;
    /**对应的异常类*/
    private String exception;
    /**系统内部报错日志信息，某些异常可自定义错误内容*/
    private String description;

    ECode(int code, String msg, String exception, String description) {
        this.code = code;
        this.msg = msg;
        this.exception = exception;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static int getCode(String exception) {
        if (Objects.nonNull(exception)) {
            for (ECode item : ECode.values()) {
                if (exception.equals(item.getException())) {
                    return item.getCode();
                }
            }
        }
        return -1;
    }

    public static ECode from(String exception) {
        try {
            return ECode.valueOf(exception);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("The task type '" + exception + "' is unsupported.", e);
        }
    }

    public static ECode of(String exception) {
        for (ECode item : ECode.values()) {
            if (item.exception.equalsIgnoreCase(exception)) {
                return item;
            }
        }
        return null;
    }

    public static String getDescription(int code) {
        for (ECode item : ECode.values()) {
            if (item.code == code) {
                return item.getDescription();
            }
        }
        return null;
    }

}
