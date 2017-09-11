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
package com.treefinance.saas.grapserver.web.advice;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.treefinance.saas.grapserver.common.model.Result;
import com.treefinance.saas.grapserver.common.exception.AppIdUncheckException;
import com.treefinance.saas.grapserver.common.exception.ForbiddenException;
import com.treefinance.saas.grapserver.common.exception.TaskTimeOutException;
import com.treefinance.saas.grapserver.common.exception.UnknownException;
import com.treefinance.saas.grapserver.common.exception.base.MarkBaseException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ValidationException;
import java.util.Map;

/**
 * @author <A HREF="mailto:yaojun@datatrees.com.cn">Jun Yao</A>
 * @version 1.0
 * @since 2017年3月06日 上午10:12:41
 */
@ControllerAdvice
public class GlobalExceptionAdvice extends ResponseEntityExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionAdvice.class);

    @ExceptionHandler(value = UnknownException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ResponseBody
    public ResponseEntity<Object> handleUnknownException(WebRequest request, UnknownException ex) {
        handleLog(request, ex);
        return handleExceptionInternal(ex, buildBody(ex), null, HttpStatus.SERVICE_UNAVAILABLE, request);
    }

    @ExceptionHandler(value = TaskTimeOutException.class)
    @ResponseStatus(HttpStatus.GATEWAY_TIMEOUT)
    @ResponseBody
    public ResponseEntity<Object> handleTimeoutException(WebRequest request, TaskTimeOutException ex) {
        handleLog(request, ex);
        return handleExceptionInternal(ex, buildBody(ex), null, HttpStatus.GATEWAY_TIMEOUT, request);
    }

    @ExceptionHandler(value = ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public ResponseEntity<Object> handleForbiddenException(WebRequest request, ForbiddenException ex) {
        handleLog(request, ex);
        return handleExceptionInternal(ex, buildBody(ex), null, HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(value = MarkBaseException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public ResponseEntity<Object> handleMarkBaseException(WebRequest request, MarkBaseException ex) {
        return handleExceptionInternal(ex, buildBody(ex), null, HttpStatus.GATEWAY_TIMEOUT, request);
    }

    @ExceptionHandler(value = AppIdUncheckException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<Object> handleAppIdUncheckException(WebRequest request, AppIdUncheckException ex) {
        handleLog(request, ex);
        return handleExceptionInternal(ex, buildBody(ex), null, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResponseEntity<Object> handleAllException(WebRequest request, Exception ex) {
        handleLog(request, ex);
        return handleExceptionInternal(ex, buildBody(ex), null, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<Object> handleValidationException(ValidationException ex, WebRequest request) {
        handleLog(request, ex);
        return handleExceptionInternal(ex, buildBody(ex), null, HttpStatus.BAD_REQUEST, request);
    }

    @Override
    public ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
                                                                       HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleExceptionInternal(ex, buildBody(ex), headers, status, request);
    }

    private void handleLog(WebRequest request, Exception ex) {
        StringBuffer logBuffer = new StringBuffer();
        if (request != null) {
            logBuffer.append("request=" + request);
        }
        if (ex != null) {
            logBuffer.append(",exception:" + ex);
        }
        logger.error(logBuffer.toString(), ex);
    }

    private Result buildBody(Exception ex) {
        Result result = new Result();
        if (ex instanceof ForbiddenException) {
            Map map = Maps.newHashMap();
            map.put("mark", 0);
            result.setData(map);
            result.setErrorMsg("用户未授权");
        } else if (ex instanceof AppIdUncheckException) {
            Map map = Maps.newHashMap();
            map.put("mark", 0);
            result.setData(map);
            result.setErrorMsg("appId非法");
        } else if (ex instanceof MarkBaseException) {
            Map map = Maps.newHashMap();
            map.put("mark", ((MarkBaseException) ex).getMark());
            result.setData(map);
            result.setErrorMsg(((MarkBaseException) ex).getErrorMsg());
        } else if (ex instanceof MissingServletRequestParameterException) {
            result.setErrorMsg("参数异常");
        }
        if (StringUtils.isEmpty(result.getErrorMsg())) {
            result.setErrorMsg("系统忙，请稍后重试");//暂时
        }
        logger.info("exception handle : ex={}, result={}", ex.getClass(), JSON.toJSONString(result));
        return result;
    }

}
