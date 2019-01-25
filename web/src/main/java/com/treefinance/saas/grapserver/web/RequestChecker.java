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

import com.treefinance.saas.grapserver.exception.IllegalRequestParameterException;
import com.treefinance.toolkit.util.Assert;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Jerry
 * @date 2018/12/13 19:54
 */
public final class RequestChecker {

    private RequestChecker() {}

    /**
     * Assert a boolean expression, throwing {@code IllegalArgumentException} if the test result is {@code false}.
     *
     * <pre class="code">
     * Assert.isTrue(i &gt; 0, &quot;The value must be greater than zero&quot;);
     * </pre>
     *
     * @param condition a boolean expression
     * @param message the exception message to use if the assertion fails
     * @throws IllegalArgumentException if expression is {@code false}
     */
    public static void isTrue(final boolean condition, final String message) {
        if (!condition) {
            throw new IllegalRequestParameterException(message);
        }
    }

    public static void isFalse(final boolean condition, final String message) {
        isTrue(!condition, message);
    }

    /**
     * check if {@code object} is not null.
     *
     * @see Assert#notNull(Object, String)
     */
    public static void notNull(final String name, final Object object) {
        checkNotNull(object, "请求参数[" + name + "]缺失！");
    }

    /**
     * check if {@code text} is not blank.
     *
     * @see Assert#notBlank(String, String)
     */
    public static void notBlank(final String name, final String text) {
        checkNotBlank(text, "请求参数[" + name + "]不正确！");
    }

    /**
     * check if {@code text} is not null or not empty.
     *
     * @see Assert#notEmpty(String, String)
     */
    public static void notEmpty(final String name, final String text) {
        checkNotEmpty(text, "请求参数[" + name + "]不能为空！");
    }

    /**
     * check if {@code object} is not null.
     *
     * @see Assert#notNull(Object, String)
     */
    public static void checkNotNull(final Object object, final String msg) {
        if (object == null) {
            throw new IllegalRequestParameterException(msg);
        }
    }

    /**
     * check if {@code text} is not blank.
     *
     * @see Assert#notBlank(String, String)
     */
    public static void checkNotBlank(final String text, final String msg) {
        if (StringUtils.isBlank(text)) {
            throw new IllegalRequestParameterException(msg);
        }
    }

    /**
     * check if {@code text} is not null or not empty.
     *
     * @see Assert#notEmpty(String, String)
     */
    public static void checkNotEmpty(final String text, final String msg) {
        if (StringUtils.isEmpty(text)) {
            throw new IllegalRequestParameterException(msg);
        }
    }
}
