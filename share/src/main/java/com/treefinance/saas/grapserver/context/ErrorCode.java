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

/**
 * @author Jerry
 * @date 2018/12/13 19:53
 */
public enum ErrorCode {

    /**
     * 内部服务异常，默认异常
     */
    INTERNAL_SERVER_ERROR("internal_server_error", "内部服务异常!"),
    /**
     * 请求参数不合法
     */
    INVALID_REQUEST_PARAMETER("invalid_parameter", "请求参数不合法!"),
    /**
     * 非法参数
     */
    ILLEGAL_ARGUMENT("illegal_argument", "非法参数!"),
    /**
     * 业务数据异常
     */
    ILLEGAL_BUSINESS_DATA("illegal_business_data", "异常业务数据!"),
    /**
     * 找不到服务地址的路由信息
     */
    SERVICE_ADDRESS_NOT_FOUND("service_address_not_found", "找不到服务地址!"),
    /**
     * 匹配不到对应的请求服务
     */
    SERVICE_NOT_FOUND("service_not_found", "找不到服务!"),
    /**
     * 未授权验证通过
     */
    UNAUTHORIZED("unauthorized", "授权验证失败!"),
    /**
     * 禁止访问资源
     */
    FORBIDDEN("forbidden", "禁止访问!"),
    /**
     * 数据加解密异常
     */
    CRYPTO_ERROR("crypto_error", "数据加解密异常!");

    private String code;
    private String desc;

    ErrorCode(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
