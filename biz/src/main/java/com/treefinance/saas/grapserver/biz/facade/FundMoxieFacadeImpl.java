package com.treefinance.saas.grapserver.biz.facade;

import com.treefinance.saas.grapserver.biz.service.moxie.FundMoxieService;
import com.treefinance.saas.grapserver.facade.service.FundMoxieFacade;
import com.treefinance.saas.knife.result.Results;
import com.treefinance.saas.knife.result.SaasResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author haojiahong
 * @date 2018/9/28
 */
@Component("fundMoxieFacade")
public class FundMoxieFacadeImpl implements FundMoxieFacade {

    @Autowired
    private FundMoxieService fundMoxieService;

    @Override
    public SaasResult<String> queryFundsEx(String moxieTaskId) {
        String result = fundMoxieService.queryFundsEx(moxieTaskId);
        return Results.newSuccessResult(result);
    }
}
