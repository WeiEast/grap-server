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
import com.google.common.collect.ImmutableMap;
import com.treefinance.saas.grapserver.exception.BizException;
import com.treefinance.saas.grapserver.exception.ForbiddenException;
import com.treefinance.saas.grapserver.exception.IllegalRequestParameterException;
import com.treefinance.saas.grapserver.exception.InformException;
import com.treefinance.saas.grapserver.web.ResponseCode;
import com.treefinance.saas.knife.result.SimpleResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import javax.validation.ValidationException;

import java.util.Map;

/**
 * @author <A HREF="mailto:yaojun@datatrees.com.cn">Jun Yao</A>
 * @version 1.0
 * @since 2017年3月06日 上午10:12:41
 */
@ControllerAdvice("com.treefinance.saas.grapserver.web.controller")
public class GlobalExceptionAdvice extends BaseResponseEntityExceptionHandler<Object> {

    @ExceptionHandler(BizException.class)
    public ResponseEntity<Object> handleBizException(WebRequest request, Exception ex) {
        return handleExceptionInternal(ex, buildBody(ex, request), null, HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected Object buildBody(Exception ex, WebRequest request) {
        SimpleResult<Map<String, Object>> result = new SimpleResult<>();
        result.setSuccess(false);
        if (ex instanceof ValidationException || ex instanceof IllegalRequestParameterException || ex instanceof ServletRequestBindingException) {
            result.setErrorMsg("非法参数！");
        } else if (ex instanceof ForbiddenException) {
            result.setData(ImmutableMap.of("mark", 0));
            result.setErrorMsg(StringUtils.defaultString(ex.getMessage(), ResponseCode.FORBIDDEN.getMsg()));
        } else if (ex instanceof InformException) {
            result.setErrorMsg(ex.getMessage());
        } else if (ex instanceof BizException) {
            result.setErrorMsg(ex.getMessage());
            Map<String, Object> map = JSON.parseObject(JSON.toJSONString(result));
            map.put("errorCode", ((BizException) ex).getCode());
            map.putIfAbsent("errorMsg", "系统忙，请稍后重试");
            return map;
        } else {
            // 暂时
            result.setErrorMsg("系统忙，请稍后重试");
        }
        logger.error("Exception handler result={}", JSON.toJSONString(result), ex);

        return result;
    }

}
