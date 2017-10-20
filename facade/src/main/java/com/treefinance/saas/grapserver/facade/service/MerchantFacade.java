package com.treefinance.saas.grapserver.facade.service;

import com.treefinance.saas.grapserver.facade.model.MerchantBaseInfoRO;
import com.treefinance.saas.knife.result.SaasResult;

/**
 * Created by haojiahong on 2017/10/20.
 */
public interface MerchantFacade {

    /**
     * 根据taskId查询商户基本信息
     *
     * @param taskId
     * @return
     */
    SaasResult<MerchantBaseInfoRO> getMerchantBaseInfoByTaskId(Long taskId);

}
