package com.treefinance.saas.grapserver.biz.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.LinkedHashSet;

/**
 * @author haojiahong
 * @date 2017/11/2
 */
public abstract class AbstractBusinessProcessor<Request, Result> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    protected LinkedHashSet<BaseBusinessComponent<Request>> components;

    public Result doService(final Request param) {
        long beforeMillis = System.currentTimeMillis();
        Result result;
        try {
            this.executeComponent(param);
            result = doBusiness(param);
            this.afterProcess(param);
        } catch (RuntimeException e) {
            long afterMillis = System.currentTimeMillis();
            doException(param, e);
            log.info("{},ERROR,耗时{}毫秒", this.getClass().getSimpleName(), (afterMillis - beforeMillis), e);
            throw e;
        }
        long afterMillis = System.currentTimeMillis();
        log.info("{},SUCCESS,耗时{}毫秒", this.getClass().getSimpleName(), (afterMillis - beforeMillis));
        return result;
    }

    protected abstract Result doBusiness(final Request request);

    protected abstract void doException(final Request request, RuntimeException e);

    protected abstract void afterProcess(final Request result);

    protected void executeComponent(Request request) {
        if (CollectionUtils.isEmpty(components)) {
            return;
        }

        for (BaseBusinessComponent<Request> component : components) {
            component.execute(request);
        }
    }

    public void setComponents(LinkedHashSet<BaseBusinessComponent<Request>> components) {
        this.components = components;
    }
}
