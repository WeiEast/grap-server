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

import com.datatrees.toolkits.util.io.Streams;
import com.datatrees.toolkits.util.json.Jackson;
import com.fasterxml.jackson.databind.JsonNode;
import com.treefinance.saas.grapserver.web.request.RequestParamsParser;
import com.treefinance.saas.grapserver.web.request.ByteServletInputStream;
import com.treefinance.saas.grapserver.web.request.WrappedHttpServletRequest;
import org.springframework.http.MediaType;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

/**
 * <p/>
 *
 * @author Jerry
 * @version 1.0.1.4
 * @since 1.0.1.4 [17:17, 11/26/15]
 */
class RequestJsonBodyResolver extends RequestResolver {

  @Override
  public boolean isSupport(HttpServletRequest request) {
    return request.getContentType().contains(MediaType.APPLICATION_JSON_VALUE);
  }

  @Override
  public HttpServletRequest resolve(HttpServletRequest request) throws ServletException,
      IOException {
    byte[] content = Streams.readToByteArray(request.getInputStream());
    Map<String, String[]> parameters = parsedJSONBody(content);
    return new WrappedHttpServletRequest<>(request, parameters, new ByteServletInputStream(content));
  }

  private Map<String, String[]> parsedJSONBody(byte[] content) {
    if (content == null)
      return null;

    JsonNode data = Jackson.parse(content);

    return RequestParamsParser.parse(data);
  }

}
