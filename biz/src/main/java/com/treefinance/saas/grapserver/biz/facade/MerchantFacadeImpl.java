package com.treefinance.saas.grapserver.biz.facade;

import com.treefinance.saas.grapserver.facade.model.MerchantBaseInfoRO;
import com.treefinance.saas.grapserver.facade.service.MerchantFacade;
import com.treefinance.saas.knife.result.SaasResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by haojiahong on 2017/10/20.
 */
@Service("merchantFacade")
public class MerchantFacadeImpl implements MerchantFacade {
    private static final Logger logger = LoggerFactory.getLogger(MerchantFacade.class);

    @Override
    public SaasResult<MerchantBaseInfoRO> getMerchantBaseInfoByTaskId(Long taskId) {
        if (taskId == null || taskId < 0) {
            logger.error("通过taskId查询商户基本信息传入");
        }
        return null;
    }
}
