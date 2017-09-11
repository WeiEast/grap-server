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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p/>
 *
 * @author Jerry
 * @version 1.0.3
 * @since 1.0.1.4 [19:18, 11/26/15]
 */
public abstract class BaseRequestFilter extends AbstractRequestFilter {

    final Logger logger = LoggerFactory.getLogger(getClass());

    private RequestResolver resolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        HttpServletRequest processRequest = request;

        if (resolver != null && resolver.isSupport(request)) {
            try {
                processRequest = resolver.resolve(request);
            } catch (Exception e) {
                try {
                    triggerWithError(e, request, response);
                } catch (Exception e1) {
                    throw new ServletException(e1);
                }
                return;
            }
        }

        filterChain.doFilter(processRequest, response);
    }

    void triggerWithError(Exception e, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
    }

    @Override
    protected void initFilterBean(FilterConfig filterConfig) throws ServletException {
        this.resolver = this.getResolver(filterConfig);
    }

    abstract RequestResolver getResolver(FilterConfig filterConfig) throws ServletException;
}
