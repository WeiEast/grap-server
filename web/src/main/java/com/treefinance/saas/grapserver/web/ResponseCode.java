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

package com.treefinance.saas.grapserver.web;

/**
 * @author Jerry
 * @date 2019-01-01 22:41
 */
public enum ResponseCode {
    /**
     * 内部服务错误
     */
    INTERNAL_SERVER_ERROR(-1, "内部服务异常!"),
    /**
     * 成功
     */
    SUCCESS(0, "success"),
    /**
     * 参数缺失
     */
    ILLEGAL_PARAMETERS(1, "参数不合法!"),
    /**
     * 认证失败(未授权),表示请求没有被认证或者认证不正确
     */
    UNAUTHORIZED(10, "授权失败!"),
    /**
     * 禁止访问资源，表示服务器完成认证过程，但是客户端请求没有权限去访问要求的资源
     */
    FORBIDDEN(11, "禁止访问!"),
    /**
     * 任务超时
     */
    TASK_TIMEOUT(100, "任务超时！");

    private int value;
    private String msg;

    ResponseCode(int value, String msg) {
        this.value = value;
        this.msg = msg;
    }

    public int getValue() {
        return value;
    }

    public String getMsg() {
        return msg;
    }

}
