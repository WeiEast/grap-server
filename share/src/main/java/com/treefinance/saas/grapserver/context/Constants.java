/*
 * Copyright © 2015 - 2017 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.treefinance.saas.grapserver.context;

import com.treefinance.b2b.saas.context.conf.PropertiesConfiguration;

/**
 * @author hanif
 */
public final class Constants {

    public static final int REDIS_KEY_TIMEOUT = PropertiesConfiguration.getInstance().getInt("platform.redisKey" + ".timeout", 600);

    public static final String START_LOGIN = "START_LOGIN";
    public static final String REFRESH_LOGIN_QR_CODE = "REFRESH_LOGIN_QR_CODE";
    public static final String REFRESH_LOGIN_CODE = "REFRESH_LOGIN_CODE";
    public static final String REFRESH_LOGIN_RANDOMPASSWORD = "REFRESH_LOGIN_RANDOMPASSWORD";
    public static final String RETURN_PICTURE_CODE = "RETURN_PICTURE_CODE";


    /**
     * WEB_CONTEXT_ATTRIBUTE
     */
    public static final String WEB_CONTEXT_ATTRIBUTE = "com.treefinance.saas.grapserver.context.WebContext";

    /**
     * appId
     */
    public static final String APP_ID = "appid";

    /**
     * 运营商错误提示信息
     */
    public static final String OPERATOR_TASK_FAIL_MSG = "运营商导入失败，请稍后再试。";
    /**
     * 学信网错误提示
     */
    public static final String DIPLOMA_TASK_FAIL_MSG = "学历信息导入失败，请稍后再试。";
    /**
     * 回调失败错误提示信息
     */
    public static final String CALLBACK_FAIL_MSG = "导入失败，请稍后再试。";
    /**
     * redis锁获取失败错误提示信息
     */
    public static final String REDIS_LOCK_ERROR_MSG = "请求频繁,请稍后再试";

    /**
     * 错误信息字段名
     */
    public static final String ERROR_MSG_NAME = "errorMsg";

}