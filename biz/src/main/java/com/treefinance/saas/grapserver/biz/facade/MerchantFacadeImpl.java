package com.treefinance.saas.grapserver.biz.facade;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.grapserver.common.utils.BeanUtils;
import com.treefinance.saas.grapserver.dao.entity.MerchantBaseInfo;
import com.treefinance.saas.grapserver.dao.entity.MerchantBaseInfoCriteria;
import com.treefinance.saas.grapserver.dao.entity.Task;
import com.treefinance.saas.grapserver.dao.mapper.MerchantBaseInfoMapper;
import com.treefinance.saas.grapserver.dao.mapper.TaskMapper;
import com.treefinance.saas.grapserver.facade.model.MerchantBaseInfoRO;
import com.treefinance.saas.grapserver.facade.service.MerchantFacade;
import com.treefinance.saas.knife.common.CommonStateCode;
import com.treefinance.saas.knife.result.Results;
import com.treefinance.saas.knife.result.SaasResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Created by haojiahong on 2017/10/20.
 */
@Service("merchantFacade")
public class MerchantFacadeImpl implements MerchantFacade {
    private static final Logger logger = LoggerFactory.getLogger(MerchantFacade.class);

    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private MerchantBaseInfoMapper merchantBaseInfoMapper;

    @Override
    public SaasResult<MerchantBaseInfoRO> getMerchantBaseInfoByTaskId(Long taskId) {
        if (taskId == null || taskId < 0) {
            logger.error("通过taskId查询商户基本信息传入taskId={}有误", taskId);
            return Results.newFailedResult(CommonStateCode.PARAMETER_LACK);
        }
        Task task = taskMapper.selectByPrimaryKey(taskId);
        if (task == null) {
            logger.info("通过taskId查询商户基本信息,未查询到taskId={}的任务信息", taskId);
            return Results.newSuccessResult(null);
        }
        MerchantBaseInfoCriteria merchantBaseInfoCriteria = new MerchantBaseInfoCriteria();
        merchantBaseInfoCriteria.createCriteria().andAppIdEqualTo(task.getAppId());
        List<MerchantBaseInfo> infoList = merchantBaseInfoMapper.selectByExample(merchantBaseInfoCriteria);
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
