package com.treefinance.saas.grapserver.biz.facade;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.grapserver.common.utils.BeanUtils;
import com.treefinance.saas.grapserver.common.utils.DataConverterUtils;
import com.treefinance.saas.grapserver.biz.dto.MerchantBaseInfo;
import com.treefinance.saas.grapserver.biz.dto.Task;
import com.treefinance.saas.grapserver.facade.model.MerchantBaseInfoRO;
import com.treefinance.saas.grapserver.facade.service.MerchantFacade;
import com.treefinance.saas.knife.common.CommonStateCode;
import com.treefinance.saas.knife.result.Results;
import com.treefinance.saas.knife.result.SaasResult;
import com.treefinance.saas.merchant.facade.request.grapserver.QueryMerchantByAppIdRequest;
import com.treefinance.saas.merchant.facade.result.console.MerchantBaseResult;
import com.treefinance.saas.merchant.facade.result.console.MerchantResult;
import com.treefinance.saas.merchant.facade.service.MerchantBaseInfoFacade;
import com.treefinance.saas.taskcenter.facade.request.TaskRequest;
import com.treefinance.saas.taskcenter.facade.result.TaskRO;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;
import com.treefinance.saas.taskcenter.facade.service.TaskFacade;

/**
 * @author haojiahong on 2017/10/20.
 */
@Service("merchantFacade")
public class MerchantFacadeImpl implements MerchantFacade {

    private static final Logger logger = LoggerFactory.getLogger(MerchantFacade.class);

    @Autowired
    private TaskFacade taskFacade;

    @Autowired
    private MerchantBaseInfoFacade merchantBaseInfoFacade;

    @Override
    public SaasResult<MerchantBaseInfoRO> getMerchantBaseInfoByTaskId(Long taskId) {
        if (taskId == null || taskId < 0) {
            logger.error("通过taskId查询商户基本信息传入taskId={}有误", taskId);
            return Results.newFailedResult(CommonStateCode.PARAMETER_LACK);
        }
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setId(taskId);

        TaskResult<TaskRO> rpcResult = taskFacade.getTaskByPrimaryKey(taskRequest);
        if (!rpcResult.isSuccess()) {
            logger.info("通过taskId查询商户基本信息,未查询到taskId={}的任务信息", taskId);
            return Results.newSuccessResult(null);
        }

        Task task = DataConverterUtils.convert(rpcResult.getData(), Task.class);
        QueryMerchantByAppIdRequest queryMerchantByTaskIdRequest = new QueryMerchantByAppIdRequest();
        List<String> stringList = new ArrayList<String>();
        stringList.add(task.getAppId());
        queryMerchantByTaskIdRequest.setAppIds(stringList);
        MerchantResult<List<MerchantBaseResult>> listMerchantResult =
                merchantBaseInfoFacade.queryMerchantBaseListByAppId(queryMerchantByTaskIdRequest);
        List<MerchantBaseInfo> infoList = DataConverterUtils.convert(listMerchantResult.getData(), MerchantBaseInfo.class);
        if (CollectionUtils.isEmpty(infoList)) {
            logger.info("通过taskId查询商户基本信息,未查询到taskId={},appId={}的商户信息", taskId, task.getAppId());
            return Results.newSuccessResult(null);
        }
        MerchantBaseInfo info = infoList.get(0);
        MerchantBaseInfoRO ro = new MerchantBaseInfoRO();
        BeanUtils.copyProperties(info, ro);
        ro.setAppId(task.getAppId());
        ro.setUniqueId(task.getUniqueId());
        logger.info("通过taskId={}查询商户基本信息result={}", taskId, JSON.toJSONString(ro));
        return Results.newSuccessResult(ro);
    }

}
