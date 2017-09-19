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

import com.google.common.collect.Lists;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 *
 * @author Jerry
 * @version 1.0.3
 * @since 1.0.1.4 [19:18, 11/26/15]
 */
public abstract class AbstractRequestFilter extends OncePerRequestFilter {

    private AntPathMatcher pathMatcher = new AntPathMatcher();
    private List<String> excludeUrlPatterns = Lists.newArrayList("/moxie/webhook/**");

    @Override
    protected void initFilterBean() throws ServletException {
        FilterConfig filterConfig = super.getFilterConfig();

        if (filterConfig != null) {
            initFilterBean(filterConfig);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        for (String excludeUrl : excludeUrlPatterns) {
            boolean result = pathMatcher.match(excludeUrl, request.getServletPath());
            if (result) {
                return true;
            }
        }
        return false;
    }

    protected abstract void initFilterBean(FilterConfig filterConfig) throws ServletException;

    public List<String> getExcludeUrlPatterns() {
        return excludeUrlPatterns;
    }

    public void addExcludeUrlPatterns(List<String> excludeUrlPatterns) {
        if (CollectionUtils.isEmpty(excludeUrlPatterns)) {
            excludeUrlPatterns = Lists.newArrayList();
        }
        excludeUrlPatterns.addAll(excludeUrlPatterns);
    }
}
