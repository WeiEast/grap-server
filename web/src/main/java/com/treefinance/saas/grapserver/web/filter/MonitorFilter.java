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

package com.treefinance.saas.grapserver.web.filter;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.assistant.model.HttpMonitorMessage;
import com.treefinance.saas.grapserver.biz.service.MonitorService;
import com.treefinance.saas.grapserver.context.Constants;
import com.treefinance.toolkit.util.http.servlet.ServletRequests;
import com.treefinance.toolkit.util.net.IpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Date;

/**
 * 发送请求的监控消息
 *
 * @author Jerry
 * @date 2018/11/5 20:57
 */
public class MonitorFilter extends AbstractRequestFilter {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private MonitorService monitorService;

    public MonitorFilter(MonitorService monitorService) {
        this.monitorService = monitorService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        logger.info("Received request >> {}, method={}, params={}", request.getRequestURI(), request.getMethod(), JSON.toJSONString(request.getParameterMap()));
        long startTime = System.currentTimeMillis();

        try {
            filterChain.doFilter(request, response);
        } finally {
            long costTime = System.currentTimeMillis() - startTime;
            logger.debug("Request processing cost : {}ms", costTime);
            HttpMonitorMessage message = new HttpMonitorMessage();
            message.setAppId(request.getParameter(Constants.APP_ID));
            message.setHttpCode(response.getStatus());
            message.setCompleteTime(new Date());
            message.setCostTime(costTime);
            message.setRequestIp(ServletRequests.getIP(request));
            message.setRequestUrl(request.getRequestURI());
            message.setServerIp(IpUtils.getLocalHost());
            monitorService.pushHttpMonitorMessage(message);
        }
    }

}
