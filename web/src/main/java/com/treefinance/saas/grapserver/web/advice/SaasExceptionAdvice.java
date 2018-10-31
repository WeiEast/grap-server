package com.treefinance.saas.grapserver.web.advice;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.treefinance.saas.grapserver.common.exception.*;
import com.treefinance.saas.grapserver.common.exception.base.MarkBaseException;
import com.treefinance.saas.grapserver.common.result.SaasResult;
import com.treefinance.saas.knife.result.SimpleResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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
 * @author:guoguoyun
 * @date:Created in 2018/10/30上午9:46
 */

@ControllerAdvice("com.treefinance.saas.grapserver.web.saascontroller")
public class SaasExceptionAdvice extends ResponseEntityExceptionHandler {

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

    @ExceptionHandler(value = AppIdFormatException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ResponseEntity<Object> handleAppIdFormatException(WebRequest request, AppIdFormatException ex) {
        handleLog(request, ex);
        return handleExceptionInternal(ex, buildBody(ex), null, HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(value = AppIdInvalidException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ResponseEntity<Object> handleAppIdInvalidException(WebRequest request, AppIdInvalidException ex) {
        handleLog(request, ex);
        return handleExceptionInternal(ex, buildBody(ex), null, HttpStatus.UNAUTHORIZED, request);
    }
    @ExceptionHandler(value = UniqueidMaxException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public ResponseEntity<Object> handleUniqueidMaxException(WebRequest request, UniqueidMaxException ex) {
        handleLog(request, ex);
        return handleExceptionInternal(ex, buildBody(ex), null, HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(value = AppIdNotActiveException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public ResponseEntity<Object> handleAppIdNotActiveException(WebRequest request, MarkBaseException ex) {
        return handleExceptionInternal(ex, buildBody(ex), null, HttpStatus.GATEWAY_TIMEOUT, request);
    }

    @ExceptionHandler(value = AppIdNoMessageException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public ResponseEntity<Object> handleAppIdNoMessageException(WebRequest request, AppIdUncheckException ex) {
        handleLog(request, ex);
        return handleExceptionInternal(ex, buildBody(ex), null, HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResponseEntity<Object> handleAllException(WebRequest request, Exception ex) {
        handleLog(request, ex);
        return handleExceptionInternal(ex, buildBody(ex), null, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(BizException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<Object> handleBizException(WebRequest request, Exception ex) {
        handleLog(request, ex);
        return handleExceptionInternal(ex, buildBody(ex), null, HttpStatus.BAD_REQUEST, request);
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

    private Object buildBody(Exception ex) {
        logger.error("系统异常", ex);
       SaasResult result = new SaasResult();

        if (ex instanceof ForbiddenException) {
            result.setCode(-1);
            result.setMsg("用户未授权");
        } else if (ex instanceof UnAuthorizedException) {
            result.setCode(1);
            result.setMsg(ex.getMessage());
        } else if (ex instanceof MarkBaseException) {
            result.setCode(2);
            result.setMsg("服务器内部异常");
        } else if (ex instanceof MissingServletRequestParameterException) {
            result.setMsg("参数异常");
        } else if (ex instanceof BizException) {
            result.setMsg(ex.getMessage());
            return result;
        } else if (ex instanceof ParamsCheckException) {
            result.setMsg(ex.getMessage());
        }
        if (StringUtils.isEmpty(result.getMsg())) {
            result.setMsg("系统忙，请稍后重试");//暂时
        }
        logger.info("exception handle : ex={},errormessage={} result={}", ex.getClass(), ex.getMessage(),JSON.toJSONString(result));
        return result;
    }

}
