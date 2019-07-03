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

package com.treefinance.saas.grapserver.web;

import com.treefinance.saas.grapserver.biz.service.MonitorService;
import com.treefinance.saas.grapserver.biz.service.TaskAliveService;
import com.treefinance.saas.grapserver.context.config.DiamondConfig;
import com.treefinance.saas.grapserver.web.filter.ExclusiveFilter;
import com.treefinance.saas.grapserver.web.filter.MonitorFilter;
import com.treefinance.saas.grapserver.web.filter.RequestAdaptFilter;
import com.treefinance.saas.grapserver.web.filter.TaskAliveFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @author Jerry
 * @since 17:53 28/04/2017
 */
@Configuration
public class FilterRegistry {

    @Bean
    public FilterRegistrationBean corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin(CorsConfiguration.ALL);
        config.addAllowedHeader(CorsConfiguration.ALL);
        config.addAllowedMethod(CorsConfiguration.ALL);
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
        bean.setOrder(FilterRegistrationBean.REQUEST_WRAPPER_FILTER_MAX_ORDER - 9);
        return bean;
    }

    @Bean
    public FilterRegistrationBean requestAdaptFilter() {
        ExclusiveFilter filter = new RequestAdaptFilter();
        filter.addExcludeUrlPatterns("/moxie/webhook/**");

        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(filter);
        registration.setName("requestAdaptFilter");
        registration.addUrlPatterns("/*");
        registration.setOrder(FilterRegistrationBean.REQUEST_WRAPPER_FILTER_MAX_ORDER + 1);
        return registration;
    }

    @Bean
    public FilterRegistrationBean monitorFilter(MonitorService monitorService) {
        ExclusiveFilter filter = new MonitorFilter(monitorService);
        filter.addExcludeUrlPatterns("/moxie/webhook/**");

        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(filter);
        registration.setName("monitorFilter");
        registration.addUrlPatterns("/*");
        registration.setOrder(FilterRegistrationBean.REQUEST_WRAPPER_FILTER_MAX_ORDER + 2);
        return registration;
    }

//    @Bean
//    public FilterRegistrationBean taskAliveFilter(DiamondConfig diamondConfig,
//                                                  TaskAliveService taskAliveService) {
//        TaskAliveFilter taskAliveFilter = new TaskAliveFilter(diamondConfig, taskAliveService);
//        taskAliveFilter.addExcludeUrlPatterns("/moxie/webhook/**", "/**/start/**/", "/grap/*/data");
//        FilterRegistrationBean registration = new FilterRegistrationBean();
//        registration.setFilter(taskAliveFilter);
//        registration.setName("taskAliveFilter");
//        registration.addUrlPatterns("/*");
//        registration.setOrder(FilterRegistrationBean.REQUEST_WRAPPER_FILTER_MAX_ORDER + 3);
//        return registration;
//    }

}
