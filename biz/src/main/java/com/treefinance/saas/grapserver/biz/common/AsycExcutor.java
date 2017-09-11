package com.treefinance.saas.grapserver.biz.common;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

/**
 * 异步执行器
 * Created by yh-treefinance on 2017/7/10.
 */
@Component
public class AsycExcutor {
    /**
     * logger
     */
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 异步执行数据
     *
     * @param t
     * @param consumer
     * @param <T>
     */
    @Async
    public <T> void runAsyc(T t, Consumer<T> consumer) {
        if (logger.isDebugEnabled()) {
            logger.debug("{} is running with data={} ", Thread.currentThread().getName(), JSON.toJSONString(t));
        }
        consumer.accept(t);
    }
}
