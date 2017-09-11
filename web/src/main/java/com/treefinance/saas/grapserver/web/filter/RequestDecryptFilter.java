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

package com.treefinance.saas.grapserver.web.filter;

import com.datatrees.toolkits.util.http.servlet.ServletRequestUtils;
import com.datatrees.toolkits.util.http.servlet.ServletResponseUtils;
import com.datatrees.toolkits.util.json.Jackson;
import com.treefinance.saas.grapserver.common.model.Result;
import com.treefinance.saas.grapserver.biz.config.SystemConfig;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p/>
 *
 * @author Jerry
 * @version 1.0.4.2
 * @since 1.0.1.3 [14:21, 11/21/15]
 */
public class RequestDecryptFilter extends BaseRequestFilter {

  @Override
  RequestResolver getResolver(FilterConfig filterConfig) throws ServletException {
    try {
      WebApplicationContext webApplicationContext =
          WebApplicationContextUtils.findWebApplicationContext(filterConfig.getServletContext());

      if (webApplicationContext == null) {
        throw new ServletException("WebAppConfigurer application context failed to init...");
      }

      String target = filterConfig.getInitParameter("decrypt.target");
      SystemConfig config = webApplicationContext.getBean(SystemConfig.class);
      return new RequestDecryptResolver(target, config);
    } catch (Throwable e) {
      throw new ServletException(e);
    }
  }

  @Override
  void triggerWithError(Exception e, HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    logger.error(
        String.format("@[%s;%s;%s] >> %s", request.getRequestURI(), request.getMethod(),
            ServletRequestUtils.getIP(request), e.getMessage()), e);

    String responseBody = Jackson.toJSONString(new Result<>("请求授权失败！"));
    ServletResponseUtils.responseJson(response, 403, responseBody);
  }

}
