package com.treefinance.saas.grapserver.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Good Luck Bro , No Bug !
 *
 * @author haojiahong
 * @date 2018/5/28
 */
@Configuration
public class ScheduleConfig implements SchedulingConfigurer {


    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(scheduleExecutor());
    }

    @Bean
    public Executor scheduleExecutor() {

        //线程池维护线程的最少数量
        int corePoolSize = 10;
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(corePoolSize);
        return scheduledThreadPoolExecutor;
    }

}
