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

import com.treefinance.saas.grapserver.web.filter.OperatorH5RequestFilter;
import com.treefinance.saas.grapserver.web.filter.RequestDecryptFilter;
import com.treefinance.saas.grapserver.web.filter.WebContextFilter;
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
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
        bean.setOrder(0);
        return bean;
    }

//    @Bean
//    public FilterRegistrationBean webContextFilter() {
//        FilterRegistrationBean registration = new FilterRegistrationBean();
//        registration.setFilter(new WebContextFilter());
//        registration.setName("webContextFilter");
//        registration.addUrlPatterns("/*");
//        registration.setOrder(1);
//        return registration;
//    }

    @Bean
    public FilterRegistrationBean requestDecryptFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new RequestDecryptFilter());
        registration.setName("requestDecryptFilter");
//        registration.addUrlPatterns("/*");
        registration.addUrlPatterns("/ecommerce/*", "/email/*", "/operator/*", "/qrscan/*", "/task/*", "/test/*");
        registration.addInitParameter("decrypt.target", "params");
        registration.setOrder(2);
        return registration;
    }

//    @Bean
//    public FilterRegistrationBean OperatorH5DecryptFilter() {
//        FilterRegistrationBean registration = new FilterRegistrationBean();
//        registration.setFilter(new OperatorH5RequestFilter());
//        registration.setName("operatorH5RequestFilter");
//        registration.addUrlPatterns("/h5/*");
//        registration.addInitParameter("decrypt.target", "params");
//        registration.setOrder(2);
//        return registration;
//    }

}
