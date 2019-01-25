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

package com.treefinance.saas.grapserver.context.validate;

import com.treefinance.saas.grapserver.exception.IllegalBusinessDataException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Map;

/**
 * @author Jerry
 * @date 2018/12/13 20:55
 */
public final class BizObjectValidator {

    private BizObjectValidator() {}

    /**
     * Assert that an object is {@code null} .
     *
     * @param object the object to check
     * @param message the exception message to use if the assertion fails
     * @throws IllegalBusinessDataException if the object is not {@code null}
     */
    public static void isNull(final Object object, final String message) {
        if (object != null) {
            throw new IllegalBusinessDataException(message);
        }
    }

    /**
     * Assert that an object is not {@code null} .
     *
     * @param object the object to check
     * @param message the exception message to use if the assertion fails
     * @throws IllegalBusinessDataException if the object is {@code null}
     */
    public static void notNull(final Object object, final String message) {
        if (object == null) {
            throw new IllegalBusinessDataException(message);
        }
    }

    /**
     * Assert that the given String is empty; that is, it must be {@code null} or the empty String.
     *
     * @param text the String to check
     * @param message the exception message to use if the assertion fails
     * @throws IllegalBusinessDataException if the text is empty
     */
    public static void isEmpty(final String text, final String message) {
        if (StringUtils.isNotEmpty(text)) {
            throw new IllegalBusinessDataException(message);
        }
    }

    /**
     * Assert that the given String is not empty; that is, it must not be {@code null} and not the empty String.
     *
     * @param text the String to check
     * @param message the exception message to use if the assertion fails
     * @throws IllegalBusinessDataException if the text is empty
     */
    public static void notEmpty(final String text, final String message) {
        if (StringUtils.isEmpty(text)) {
            throw new IllegalBusinessDataException(message);
        }
    }

    /**
     * Assert that the given String doesn't has valid text content; that is, it must be {@code null} or only contain
     * whitespace character.
     *
     * @param text the String to check
     * @param message the exception message to use if the assertion fails
     * @throws IllegalBusinessDataException if the text does not contain valid text content
     */
    public static void isBlank(final String text, final String message) {
        if (StringUtils.isNotBlank(text)) {
            throw new IllegalBusinessDataException(message);
        }
    }

    /**
     * Assert that the given String has valid text content; that is, it must not be {@code null} and must contain at
     * least one non-whitespace character.
     *
     * @param text the String to check
     * @param message the exception message to use if the assertion fails
     * @throws IllegalBusinessDataException if the text does not contain valid text content
     */
    public static void notBlank(final String text, final String message) {
        if (StringUtils.isBlank(text)) {
            throw new IllegalBusinessDataException(message);
        }
    }

    public static <T> void notEmpty(final Collection<T> collection, final String message) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new IllegalBusinessDataException(message);
        }
    }

    public static <K, V> void notEmpty(final Map<K, V> map, final String message) {
        if (MapUtils.isEmpty(map)) {
            throw new IllegalBusinessDataException(message);
        }
    }
}
