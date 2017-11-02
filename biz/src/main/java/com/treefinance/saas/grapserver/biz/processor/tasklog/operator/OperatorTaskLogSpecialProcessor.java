package com.treefinance.saas.grapserver.biz.processor.tasklog.operator;

import com.treefinance.saas.grapserver.biz.processor.AbstractBusinessProcessor;

/**
 * Created by haojiahong on 2017/11/2.
 */
public class OperatorTaskLogSpecialProcessor extends AbstractBusinessProcessor<OperatorTaskLogSpecialRequest, Void> {


    @Override
    protected Void doBusiness(OperatorTaskLogSpecialRequest operatorTaskLogSpecialRequest) {
        return null;
    }

    @Override
    protected void doException(OperatorTaskLogSpecialRequest operatorTaskLogSpecialRequest, RuntimeException e) {

    }

    @Override
    protected void afterProcess(OperatorTaskLogSpecialRequest result) {

    }
}
