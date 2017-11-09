package com.treefinance.saas.grapserver.biz.processor;

import com.treefinance.saas.grapserver.biz.processor.request.OperatorMonitorSpecialRequest;

/**
 * Created by haojiahong on 2017/11/9.
 */
public class OperatorMonitorSpecialProcessor extends AbstractBusinessProcessor<OperatorMonitorSpecialRequest, Void> {

    @Override
    protected Void doBusiness(OperatorMonitorSpecialRequest operatorMonitorSpecialRequest) {
        return null;
    }

    @Override
    protected void doException(OperatorMonitorSpecialRequest operatorMonitorSpecialRequest, RuntimeException e) {

    }

    @Override
    protected void afterProcess(OperatorMonitorSpecialRequest result) {

    }
}
