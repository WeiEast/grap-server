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

package com.treefinance.saas.grapserver.web.advice;

import com.treefinance.saas.grapserver.exception.TaskTimeOutException;
import com.treefinance.saas.grapserver.exception.BadServiceException;
import com.treefinance.saas.grapserver.exception.ForbiddenException;
import com.treefinance.saas.grapserver.exception.IllegalRequestParameterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.WebUtils;

import javax.validation.ValidationException;

/**
 * @author Jerry
 * @date 2019-01-24 16:00
 */
public abstract class BaseResponseEntityExceptionHandler<T> extends ResponseEntityExceptionHandler {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @ExceptionHandler({ValidationException.class, IllegalRequestParameterException.class})
    public ResponseEntity<Object> handleValidationException(Exception ex, WebRequest request) {
        return handleExceptionInternal(ex, buildBody(ex, request), null, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = ForbiddenException.class)
    public ResponseEntity<Object> handleForbiddenException(Exception ex, WebRequest request) {
        return handleExceptionInternal(ex, buildBody(ex, request), null, HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(value = TaskTimeOutException.class)
    public ResponseEntity<Object> handleTimeoutException(TaskTimeOutException ex, WebRequest request) {
        return handleExceptionInternal(ex, buildBody(ex, request), null, HttpStatus.GATEWAY_TIMEOUT, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllException(Exception ex, WebRequest request) {
        return handleExceptionInternal(ex, buildBody(ex, request), null, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        this.printLog(ex, request);

        if (body != null) {
            return super.handleExceptionInternal(ex, body, headers, status, request);
        }

        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
        }

        Object result = buildBody(ex, request);

        return new ResponseEntity<>(result, headers, status);
    }

    /**
     * 创建返回内容体
     * 
     * @param ex 异常
     * @param request 请求
     * @return 返回实体
     */
    protected abstract T buildBody(Exception ex, WebRequest request);

    private void printLog(Exception ex, WebRequest request) {
        if (ex instanceof BadServiceException) {
            logger.error("request: {}, errorCode: {}, exception: {}", request, ((BadServiceException)ex).getErrorCode(), ex.getMessage(), ex);
        } else {
            logger.error("request: {}, exception: {}", request, ex.getMessage(), ex);
        }
    }
}
