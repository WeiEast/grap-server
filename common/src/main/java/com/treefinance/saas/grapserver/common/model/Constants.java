package com.treefinance.saas.grapserver.common.model;

import com.datatrees.common.conf.PropertiesConfiguration;

public interface Constants {

    String PREFIX_KEY = "saas-gateway:";

    int REDIS_KEY_TIMEOUT = PropertiesConfiguration.getInstance().getInt("platform.redisKey.timeout", 600);

    String START_LOGIN = "START_LOGIN";
    String REFRESH_LOGIN_QR_CODE = "REFRESH_LOGIN_QR_CODE";
    String REFRESH_LOGIN_CODE = "REFRESH_LOGIN_CODE";
    String REFRESH_LOGIN_RANDOMPASSWORD = "REFRESH_LOGIN_RANDOMPASSWORD";
    String RETURN_PICTURE_CODE = "RETURN_PICTURE_CODE";


    /**
     * WEB_CONTEXT_ATTRIBUTE
     */
    String WEB_CONTEXT_ATTRIBUTE = "com.treefinance.saas.grapserver.common.model.WebContext";

    /**
     * appId
     */
    String APP_ID = "appid";

    /**
     * 运营商错误提示信息
     */
    String OPERATOR_TASK_FAIL_MSG = "运营商导入失败，请稍后再试。";
    /**
     * 回调失败错误提示信息
     */
    String CALLBACK_FAIL_MSG = "导入失败，请稍后再试。";

    /**
     * 错误信息字段名
     */
    String ERROR_MSG_NAME = "errorMsg";
}