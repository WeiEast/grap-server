package com.treefinance.saas.grapserver.common.enums;

import java.util.Objects;

/**
 * @author:guoguoyun
 * @date:Created in 2018/10/30下午2:48
 */

public enum ECode {
    //参数传值异常
    UNAUTHORIZED_APPIDINVALID(1, "商户未授权", "saas.invalid-parameter", "商户未授权,isvalid=false"),
    UNAUTHORIZED_APPIDFORMAT(2, "商户ID不符合格式", "saas.invalid-parameter", "appId不符合格式"),
    FORBIDDEN_APPIDNOTACTIVE(3, "商户被禁用", "saas.invalid-parameter", "appID被禁用，isActive=false"),
    FORBIDDEN_APPIDNOMESSAGE(4, "商户无可用数据", "saas.invalid-parameter", "appID无信息"),
    FORBIDDEN_UNIQUEIDMAX(5, "商户访问超过阈值", "saas.invalid-parameter", "商户访问超过阈值"),
    VALIDATION(6, "传入参数不合法", "saas.invalid-parameter", "具体含义自定义"),
    CRPTOR_REQUESTDECRYPT(7, "解密请求参数异常", "saas.invalid-parameter", "解密请求参数"),
    CRPTOR(8, "加解密方法出现异常", "saas.invalid-parameter", "加解密方法出现异常求参数"),
    CRPTOR_RESPONSEENCRYPT(9, "加密返回参数异常", "saas.invalid-parameter", "解密请求参数"),
    PARAMSCHECK(10, "参数异常", "saas.invalid-parameter", "具体含义自定义"),






    //服务器内部处理异常
    EXCEPTION(200, "服务器内部异常", "saas.system-exception", "具体含义自定义"),
    UNKNOW(201, "服务器内部异常", "saas.system-exception", "调用taskcenter异常"),
    TASKTIMEOUT(202, "服务器内部异常", "saas.system-exception", "任务超时异常"),
    REQUESTFAILED(203, "服务器内部异常", "saas.system-exception", "请求失败"),
    NOTFOUND(204, "服务器内部异常", "saas.system-exception", "转发的URL没有对应服务"),
    TIMEOUT(205, "服务器内部异常", "saas.system-exception", "上游服务器没有响应，超时"),
    RESPONSE(206, "服务器内部异常", "saas.system-exception", "返回数据异常"),
    CRAWLERBIZ(207, "服务器内部异常", "saas.system-exception", "爬树业务异常，具体含义自定义"),
    BIZ(208, "业务异常", "saas.system-exception", "自定义");


    /**自定义code码*/
    private int code;
    /**返回给第三方的错误信息*/
    private String msg;
    /**所属的异常类型code*/
    private String sub_code;
    /**系统内部报错日志信息，某些异常可自定义错误内容*/
    private String description;

    ECode(int code, String msg, String sub_code, String description) {
        this.code = code;
        this.msg = msg;
        this.sub_code = sub_code;
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
        return sub_code;
    }

    public void setException(String exception) {
        this.sub_code = exception;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static int getCode(String sub_code) {
        if (Objects.nonNull(sub_code)) {
            for (ECode item : ECode.values()) {
                if (sub_code.equals(item.getException())) {
                    return item.getCode();
                }
            }
        }
        return -1;
    }

    public static ECode from(String sub_code) {
        try {
            return ECode.valueOf(sub_code);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("The task type '" + sub_code + "' is unsupported.", e);
        }
    }

    public static ECode of(String sub_code) {
        for (ECode item : ECode.values()) {
            if (item.sub_code.equalsIgnoreCase(sub_code)) {
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
