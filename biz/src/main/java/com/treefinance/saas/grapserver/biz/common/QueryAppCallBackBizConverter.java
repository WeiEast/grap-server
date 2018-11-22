package com.treefinance.saas.grapserver.biz.common;

import java.util.List;

import javax.annotation.Resource;

import com.treefinance.saas.merchant.facade.request.common.BaseRequest;
import com.treefinance.saas.merchant.facade.request.grapserver.GetAppCallBackBizByCallbackIdRequest;
import com.treefinance.saas.merchant.facade.service.AppCallBackBizFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.treefinance.saas.grapserver.common.utils.DataConverterUtils;
import com.treefinance.saas.grapserver.dao.entity.AppCallbackBiz;
import com.treefinance.saas.merchant.facade.result.console.MerchantResult;
import com.treefinance.saas.merchant.facade.result.grapsever.AppCallbackBizResult;

/**
 * @author:guoguoyun
 * @date:Created in 2018/11/15下午3:31
 */
@Component
public class QueryAppCallBackBizConverter {
    private static final Logger logger = LoggerFactory.getLogger(QueryAppCallBackBizConverter.class);

    @Resource
    private AppCallBackBizFacade appCallBackBizFacade;

    public  List<AppCallbackBiz> queryAppCallBackByCallbackId(Integer callbackId) {
        GetAppCallBackBizByCallbackIdRequest getAppCallBackBizByCallbackIdRequest =
            new GetAppCallBackBizByCallbackIdRequest();
        getAppCallBackBizByCallbackIdRequest.setCallbackId(callbackId);
        MerchantResult<List<AppCallbackBizResult>> listMerchantResult =
            appCallBackBizFacade.queryAppCallBackByCallbackId(getAppCallBackBizByCallbackIdRequest);
        List<AppCallbackBiz> list = DataConverterUtils.convert(listMerchantResult.getData(), AppCallbackBiz.class);
        if (!listMerchantResult.isSuccess()) {
            logger.info("load local cache of callback-types  false: error message={}", listMerchantResult.getRetMsg());
            list = Lists.newArrayList();
        }
        logger.info("load local cache of callback-types : callbackId={}, callbackType={}", callbackId,
            JSON.toJSONString(list));
        return list;

    }
    
    public  List<AppCallbackBiz> queryAllAppCallBack(){
        BaseRequest baseRequest = new BaseRequest();
        MerchantResult<List<AppCallbackBizResult>> listMerchantResult = appCallBackBizFacade.queryAllAppCallBack(baseRequest);
        List<AppCallbackBiz> appCallbackBizList = DataConverterUtils.convert(listMerchantResult.getData(), AppCallbackBiz.class);
        return appCallbackBizList;
    }

}
