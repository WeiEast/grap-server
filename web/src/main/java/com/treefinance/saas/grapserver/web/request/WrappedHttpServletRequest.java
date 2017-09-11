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

package com.treefinance.saas.grapserver.web.request;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.*;

/**
 * <p/>
 *
 * @author Jerry
 * @version 1.0.1.4
 * @since 1.0.1.3 [14:27, 11/21/15]
 */
public class WrappedHttpServletRequest<T extends ServletInputStream> extends HttpServletRequestWrapper {
  private Map<String, String[]> parameters;
  private T inputStream;

  public WrappedHttpServletRequest(HttpServletRequest request, Map<String, String[]> parameters) {
    super(request);
    this.parameters = parameters;
  }

  public WrappedHttpServletRequest(HttpServletRequest request, Map<String, String[]> parameters,
                                   T inputStream) {
    super(request);
    this.parameters = parameters;
    this.inputStream = inputStream;
  }

  @Override
  public String getParameter(String name) {
    String[] values = getParameters().get(name);
    if (values != null) {
      return (values.length > 0 ? values[0] : null);
    }
    return super.getParameter(name);
  }

  @Override
  public String[] getParameterValues(String name) {
    String[] values = getParameters().get(name);
    if (values != null) {
      return values;
    }
    return super.getParameterValues(name);
  }

  @Override
  public Enumeration getParameterNames() {
    Map<String, String[]> parameters = getParameters();
    if (parameters.isEmpty()) {
      return super.getParameterNames();
    }

    Set<String> paramNames = new LinkedHashSet<>();
    // noinspection unchecked
    Enumeration<String> paramEnum = super.getParameterNames();
    while (paramEnum.hasMoreElements()) {
      paramNames.add(paramEnum.nextElement());
    }
    paramNames.addAll(parameters.keySet());
    return Collections.enumeration(paramNames);
  }

  @Override
  public Map getParameterMap() {
    Map<String, String[]> parameters = getParameters();
    if (parameters.isEmpty()) {
      return super.getParameterMap();
    }

    Map<String, String[]> paramMap = new LinkedHashMap<>();
    // noinspection unchecked
    paramMap.putAll(super.getParameterMap());
    paramMap.putAll(parameters);
    return paramMap;
  }

  @Override
  public ServletInputStream getInputStream() throws IOException {
    if (this.inputStream != null) {
      return this.inputStream;
    }

    return super.getInputStream();
  }

  public Map<String, String[]> getParameters() {
    if (this.parameters == null) {
      this.parameters = new HashMap<>();
    }
    return this.parameters;
  }

}
