/*
 * Copyright © 2015 - 2017 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.treefinance.saas.grapserver.util;

/**
 * @author guoguoyun
 * @date Created in 2018/10/29下午7:11
 */
public class JudgeUtils {

    /**
     * 判断输入密码是否为纯数字
     */
    public static boolean isPassword(String str) {
        return str.matches("^[0-9]*$");
    }

    /**
     * 判断输入身份证号码是否正确
     */
    public static boolean isIdCard(String str) {
        return str.matches("^[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$");
    }

    /**
     * 简单判断是否为手机号
     */
    public static boolean isCellNumber(String str) {
        return str.matches("^(1)[0-9]{10}$");
    }

    /**
     * 简单判断是否为邮箱地址
     */
    public static boolean isEmail(String str) {
        return str.matches("^[a-z0-9A-Z]+[- |a-z0-9A-Z._]+@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-z]{2,}$");
    }

}