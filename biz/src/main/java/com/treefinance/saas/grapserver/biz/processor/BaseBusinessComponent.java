package com.treefinance.saas.grapserver.biz.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author haojiahong
 * @date 2017/11/2
 */
public abstract class BaseBusinessComponent<Request> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public void execute(Request request) {
        long startTime = System.currentTimeMillis();

        doBusiness(request);

        long costTime = System.currentTimeMillis() - startTime;
        if (costTime >= 1000) {
            log.info("{} 组件耗时超过1秒。cost {} ms.", getClass().getSimpleName(), costTime);
        }
        log.info("{} 组件耗时,cost {} ms.", getClass().getSimpleName(), costTime);
    }

    /**
     * 不带返回值的doBusiness
     */
    protected abstract void doBusiness(Request request);

}
