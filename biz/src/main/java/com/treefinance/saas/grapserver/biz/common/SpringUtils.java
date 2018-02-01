package com.treefinance.saas.grapserver.biz.common;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Buddha Bless , No Bug !
 *
 * @author haojiahong
 * @date 2018/2/1
 */
@Component
public class SpringUtils {

    private static ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringUtils.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 根据Bean名称获取实例
     *
     * @param name Bean注册名称
     * @return bean实例
     * @throws org.springframework.beans.BeansException
     */
    public static Object getBean(String name) throws BeansException {
        return applicationContext.getBean(name);
    }
}
