package com.treefinance.saas.grapserver.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Good Luck Bro , No Bug !
 *
 * @author haojiahong
 * @date 2018/5/28
 */
@Configuration
public class ThreadPoolConfig {

    @Bean
    public ThreadPoolTaskExecutor threadPoolExecutor() {
        int processNumber = Runtime.getRuntime().availableProcessors();
        //线程池维护线程的最少数量
        int corePoolSize = processNumber * 2;
        //线程池维护线程的最大数量
        int maxPoolSize = processNumber * 10;
        //缓存队列
        int queueCapacity = processNumber * 50;
        //允许的空闲时间
        int keepAliveSeconds = 60;

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("grap-server-thread-pool-");
        executor.setKeepAliveSeconds(keepAliveSeconds);
        return executor;
    }

}
