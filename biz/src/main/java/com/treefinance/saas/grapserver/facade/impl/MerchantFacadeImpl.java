package com.treefinance.saas.grapserver.facade.impl;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.grapserver.biz.domain.MerchantBaseInfo;
import com.treefinance.saas.grapserver.context.component.AbstractFacade;
import com.treefinance.saas.grapserver.facade.model.MerchantBaseInfoRO;
import com.treefinance.saas.grapserver.facade.service.MerchantFacade;
import com.treefinance.saas.grapserver.manager.TaskManager;
import com.treefinance.saas.grapserver.manager.domain.TaskBO;
import com.treefinance.saas.knife.common.CommonStateCode;
import com.treefinance.saas.knife.result.Results;
import com.treefinance.saas.knife.result.SaasResult;
import com.treefinance.saas.merchant.facade.request.grapserver.QueryMerchantByAppIdRequest;
import com.treefinance.saas.merchant.facade.result.console.MerchantBaseResult;
import com.treefinance.saas.merchant.facade.result.console.MerchantResult;
import com.treefinance.saas.merchant.facade.service.MerchantBaseInfoFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author haojiahong on 2017/10/20.
 */
@Service("merchantFacade")
public class MerchantFacadeImpl extends AbstractFacade implements MerchantFacade {

    @Autowired
    private TaskManager taskManager;

    @Autowired
    private MerchantBaseInfoFacade merchantBaseInfoFacade;

    @Override
    public SaasResult<MerchantBaseInfoRO> getMerchantBaseInfoByTaskId(Long taskId) {
        if (taskId == null || taskId < 0) {
            logger.error("通过taskId查询商户基本信息传入taskId={}有误", taskId);
            return Results.newFailedResult(CommonStateCode.PARAMETER_LACK);
        }

        TaskBO task = taskManager.getTaskById(taskId);
        QueryMerchantByAppIdRequest queryMerchantByTaskIdRequest = new QueryMerchantByAppIdRequest();
        List<String> stringList = new ArrayList<String>();
        stringList.add(task.getAppId());
        queryMerchantByTaskIdRequest.setAppIds(stringList);
        MerchantResult<List<MerchantBaseResult>> listMerchantResult =
                merchantBaseInfoFacade.queryMerchantBaseListByAppId(queryMerchantByTaskIdRequest);
        List<MerchantBaseInfo> infoList = convert(listMerchantResult.getData(), MerchantBaseInfo.class);
        if (CollectionUtils.isEmpty(infoList)) {
            logger.info("通过taskId查询商户基本信息,未查询到taskId={},appId={}的商户信息", taskId, task.getAppId());
            return Results.newSuccessResult(null);
        }
        MerchantBaseInfo info = infoList.get(0);
        MerchantBaseInfoRO ro = new MerchantBaseInfoRO();
        copyProperties(info, ro);
        ro.setAppId(task.getAppId());
        ro.setUniqueId(task.getUniqueId());
        logger.info("通过taskId={}查询商户基本信息result={}", taskId, JSON.toJSONString(ro));
        return Results.newSuccessResult(ro);
    }

}
