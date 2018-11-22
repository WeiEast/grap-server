package com.treefinance.saas.grapserver.biz.common;

import java.util.List;

import javax.annotation.Resource;

import com.treefinance.saas.merchant.facade.request.common.BaseRequest;
import com.treefinance.saas.merchant.facade.request.grapserver.GetAppCallBackConfigByIdRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.treefinance.saas.grapserver.common.utils.DataConverterUtils;
import com.treefinance.saas.grapserver.dao.entity.AppCallbackConfig;
import com.treefinance.saas.merchant.facade.result.console.MerchantResult;
import com.treefinance.saas.merchant.facade.result.grapsever.AppCallbackResult;
import com.treefinance.saas.merchant.facade.service.AppCallbackConfigFacade;

/**
 * @author:guoguoyun
 * @date:Created in 2018/11/15下午3:22
 */
@Component
public class QueryAppCallbackConfigConverter {
    private static final Logger logger = LoggerFactory.getLogger(QueryAppCallbackConfigConverter.class);

    @Resource
    private AppCallbackConfigFacade appCallbackConfigFacade;

    public List<AppCallbackConfig> queryAppCallBackConfigByAppId(String appid) {

        GetAppCallBackConfigByIdRequest getAppCallBackConfigByIdRequest = new GetAppCallBackConfigByIdRequest();
        getAppCallBackConfigByIdRequest.setAppId(appid);
        MerchantResult<List<AppCallbackResult>> listMerchantResult =
            appCallbackConfigFacade.queryAppCallBackConfigByAppId(getAppCallBackConfigByIdRequest);
        List<AppCallbackConfig> list =
            DataConverterUtils.convert(listMerchantResult.getData(), AppCallbackConfig.class);

        if (!listMerchantResult.isSuccess()) {
            logger.info("load local cache of callback-configs  false: error message={}",
                listMerchantResult.getRetMsg());
            list = Lists.newArrayList();
        }
        logger.info("load local cache of callback-configs : appid={}, license={}", appid, JSON.toJSONString(list));
        return list;

    }

    public List<AppCallbackConfig> queryAllAppCallBackConfig() {
        BaseRequest request = new BaseRequest();
        MerchantResult<List<AppCallbackResult>> listMerchantResult =
            appCallbackConfigFacade.queryAllAppCallBackConfig(request);
        List<AppCallbackConfig> list =
            DataConverterUtils.convert(listMerchantResult.getData(), AppCallbackConfig.class);
        return list;
    }

}
