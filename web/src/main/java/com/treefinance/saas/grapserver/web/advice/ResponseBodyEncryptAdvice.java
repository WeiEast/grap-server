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

import com.treefinance.saas.knife.result.SimpleResult;
import com.treefinance.saas.grapserver.common.exception.ResponseException;
import com.treefinance.saas.grapserver.common.model.WebContext;
import com.treefinance.saas.grapserver.common.utils.WebContextUtils;
import com.treefinance.saas.grapserver.web.auth.ResponseSecureHandler;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Jerry
 * @since 22:18 25/04/2017
 */
@ControllerAdvice("com.treefinance.saas.grapserver")
public class ResponseBodyEncryptAdvice implements ResponseBodyAdvice<SimpleResult> {

  @Override
  public boolean supports(MethodParameter returnType,
      Class<? extends HttpMessageConverter<?>> converterType) {
    if (returnType.hasMethodAnnotation(ResponseEncrypt.class)
        && returnType.getParameterIndex() < 0) {
      Class<?> parameterType = returnType.getParameterType();

      return parameterType != null && SimpleResult.class.isAssignableFrom(parameterType);
    }

    return false;
  }

  @Override
  public SimpleResult beforeBodyWrite(SimpleResult body, MethodParameter returnType,
                                      MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                      ServerHttpRequest request, ServerHttpResponse response) {
    if (request instanceof ServletServerHttpRequest) {
      HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
      WebContext context = WebContextUtils.getWebContext(servletRequest);

      ResponseSecureHandler secureHandler = new ResponseSecureHandler(context.getAppLicenseKey());

      try {
        return secureHandler.encrypt(body);
      } catch (Throwable e) {
        throw new ResponseException("Error encrypting response data ...", e);
      }
    }

    throw new ResponseException(
        "Error encrypting response data. Reason: request is not the instance of 'ServletServerHttpRequest'");
  }
}
