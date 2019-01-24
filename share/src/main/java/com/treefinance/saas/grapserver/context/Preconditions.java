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

package com.treefinance.saas.grapserver.context;

import com.treefinance.toolkit.util.Assert;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Jerry
 * @date 2018/12/13 19:54
 */
public final class Preconditions {

    private Preconditions() {}

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
            throw new IllegalArgumentException(message);
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
        if (object == null) {
            throw new IllegalArgumentException("必需参数[" + name + "]缺失！");
        }
    }

    /**
     * check if {@code text} is not blank.
     *
     * @see Assert#notBlank(String, String)
     */
    public static void notBlank(final String name, final String text) {
        if (StringUtils.isBlank(text)) {
            throw new IllegalArgumentException("参数[" + name + "]不正确！");
        }
    }

    /**
     * check if {@code text} is not null or not empty.
     *
     * @see Assert#notEmpty(String, String)
     */
    public static void notEmpty(final String name, final String text) {
        if (StringUtils.isEmpty(text)) {
            throw new IllegalArgumentException("参数[" + name + "]不能为空！");
        }
    }
}
