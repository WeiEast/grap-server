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

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.treefinance.saas.assistant.model.HttpMonitorMessage;
import com.treefinance.saas.grapserver.biz.config.DiamondConfig;
import com.treefinance.saas.grapserver.biz.service.AppLicenseService;
import com.treefinance.saas.grapserver.biz.service.MonitorService;
import com.treefinance.saas.grapserver.common.exception.AppIdUncheckException;
import com.treefinance.saas.grapserver.common.exception.ForbiddenException;
import com.treefinance.saas.grapserver.common.model.AppLicenseKey;
import com.treefinance.saas.grapserver.common.model.Constants;
import com.treefinance.saas.grapserver.common.model.WebContext;
import com.treefinance.saas.grapserver.dao.entity.AppLicense;
import com.treefinance.saas.grapserver.web.request.WrappedHttpServletRequest;
import com.treefinance.saas.knife.result.SimpleResult;
import com.treefinance.toolkit.util.http.servlet.ServletRequests;
import com.treefinance.toolkit.util.http.servlet.ServletResponses;
import com.treefinance.toolkit.util.json.Jackson;
import com.treefinance.toolkit.util.net.IpUtils;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.regex.Pattern;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jerry
 * @since 14:22 26/04/2017
 */

public class WebContextFilter extends AbstractRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(WebContextFilter.class);
    private MonitorService monitorService;
    private DiamondConfig diamondConfig;
    private AppLicenseService appLicenseService;


    public WebContextFilter(MonitorService monitorService,
                            DiamondConfig diamondConfig,
                            AppLicenseService appLicenseService) {
        this.monitorService = monitorService;
        this.diamondConfig = diamondConfig;
        this.appLicenseService = appLicenseService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        long start = System.currentTimeMillis();
        try {
            request = WrappedHttpServletRequest.wrap(request, null);
            //校验appId
            String appId = request.getParameter(Constants.APP_ID);
            if (appId == null) {
                throw new ForbiddenException("Can not find parameter 'appid' in request.");
            }
            if (StringUtils.isBlank(appId)) {
                throw new ForbiddenException("Invalid appLicenseKey id.");
            }
            String pattern = "^" + diamondConfig.getAppIdEnvironmentPrefix() + "_" + "[0-9a-zA-Z]{16}";
            String oldPattern = "[0-9a-zA-Z]{16}";//老商户是16位字符的appId
            boolean isMatchOld = Pattern.matches(oldPattern, appId);
            boolean isMatch = Pattern.matches(pattern, appId);
            if (!isMatchOld && !isMatch) {
                throw new AppIdUncheckException("appLicenseKey id is illegal in this environment,appIdEnvironmentPrefix="
                        + diamondConfig.getAppIdEnvironmentPrefix());
            }

            String ip = null;
            try {
                ip = ServletRequests.getIP(request);
            } catch (Exception e) {
                logger.error(String
                        .format("@[%s;%s] >> appId: %s", request.getRequestURI(), request.getMethod(), appId), e);
            }

            request.setAttribute(Constants.WEB_CONTEXT_ATTRIBUTE, createWebContext(appId, ip));

            try {
                filterChain.doFilter(request, response);
            } finally {
                request.removeAttribute(Constants.WEB_CONTEXT_ATTRIBUTE);
            }
        } catch (ForbiddenException e) {
            forbidden(request, response, e);
        } catch (AppIdUncheckException e) {
            appIdUncheck(request, response, e);
        } finally {
            logger.info("{} of {} : params={}", request.getMethod(), request.getRequestURL(),
                    JSON.toJSONString(request.getParameterMap()));
            monitorRequest(start, request, response);
        }
    }


    /**
     * appId uncheck
     *
     * @param request
     * @param response
     * @param e
     */
    private void appIdUncheck(HttpServletRequest request, HttpServletResponse response, AppIdUncheckException e) {
        logger.error(String.format("@[%s;%s;%s] >> %s", request.getRequestURI(), request.getMethod(),
                ServletRequests.getIP(request), e.getMessage()));

        Map map = Maps.newHashMap();
        map.put("mark", 0);
        SimpleResult result = new SimpleResult(map);
        result.setErrorMsg("appId非法");
        String responseBody = Jackson.toJSONString(result);
        ServletResponses.responseJson(response, 400, responseBody);
    }

    /**
     * create context
     *
     * @param appId
     * @param ip
     * @return
     * @throws ForbiddenException
     */
    private WebContext createWebContext(String appId, String ip) throws ForbiddenException {
        AppLicense license = appLicenseService.getAppLicense(appId);
        if (license == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Can not find license for appLicenseKey '{}'.", appId);
            }
            throw new ForbiddenException("Can not find license for appLicenseKey '" + appId + "'.");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Found license[{}] for appLicenseKey '{}'.", JSON.toJSONString(license), appId);
        }
        AppLicenseKey appLicenseKey = new AppLicenseKey(license.getAppId(), license.getSdkPublicKey(), license.getSdkPrivateKey());
        WebContext context = new WebContext();
        context.setAppLicenseKey(appLicenseKey);
        context.setIp(ip);

        return context;
    }

    /**
     * 用户未授权处理
     *
     * @param request
     * @param response
     * @param e
     */
    private void forbidden(HttpServletRequest request, HttpServletResponse response,
                           ForbiddenException e) {
        logger.error(String.format("@[%s;%s;%s] >> %s", request.getRequestURI(), request.getMethod(),
                ServletRequests.getIP(request), e.getMessage()));

        Map map = Maps.newHashMap();
        map.put("mark", 0);
        SimpleResult result = new SimpleResult(map);
        result.setErrorMsg("用户未授权");
        String responseBody = Jackson.toJSONString(result);
        ServletResponses.responseJson(response, 403, responseBody);
    }


    /**
     * 监控消息
     *
     * @param startTime
     * @param request
     * @param response
     */
    private void monitorRequest(long startTime, HttpServletRequest request, HttpServletResponse response) {
        HttpMonitorMessage message = new HttpMonitorMessage();
        message.setAppId(request.getParameter(Constants.APP_ID));
        message.setHttpCode(response.getStatus());
        message.setCompleteTime(new Date());
        message.setCostTime(System.currentTimeMillis() - startTime);
        message.setRequestIp(IpUtils.getIp(request));
        message.setRequestUrl(request.getRequestURI());
        message.setServerIp(IpUtils.getLocalHost());
        monitorService.pushHttpMonitorMessage(message);
    }
}
