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

package com.treefinance.saas.grapserver.exception;

import com.treefinance.saas.grapserver.context.ErrorCode;
import org.apache.commons.lang3.StringUtils;

/**
 * Bad Service，不期望的服务异常
 *
 * @author Jerry
 * @date 2018/12/13 19:57
 */
public class BadServiceException extends RuntimeException {
    private ErrorCode errorCode;

    public BadServiceException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public BadServiceException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BadServiceException(ErrorCode errorCode, String message, Throwable cause) {
        super(message + (cause != null ? " - detail: " + cause.toString() : StringUtils.EMPTY), cause);
        this.errorCode = errorCode;
    }

    public BadServiceException(ErrorCode errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        return StringUtils.defaultIfEmpty(super.getMessage(), errorCode.getDesc());
    }
}
