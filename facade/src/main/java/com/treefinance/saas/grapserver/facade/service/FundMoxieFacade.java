package com.treefinance.saas.grapserver.facade.service;

import com.treefinance.saas.knife.result.SaasResult;

/**
 * @author haojiahong
 * @date 2018/9/28
 */
public interface FundMoxieFacade {

    SaasResult<String> queryFundsEx(String moxieTaskId);
}
