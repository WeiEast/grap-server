package com.treefinance.saas.grapserver.biz.adapter;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.treefinance.saas.grapserver.common.utils.DataConverterUtils;
import com.treefinance.saas.grapserver.biz.dto.AppBizLicense;
import com.treefinance.saas.merchant.facade.request.common.BaseRequest;
import com.treefinance.saas.merchant.facade.request.grapserver.GetAppLicenseByAppIdRequest;
import com.treefinance.saas.merchant.facade.result.console.AppBizLicenseResult;
import com.treefinance.saas.merchant.facade.result.console.MerchantResult;
import com.treefinance.saas.merchant.facade.service.AppBizLicenseFacade;

import javax.annotation.Resource;

/**
 * @author:guoguoyun
 * @date:Created in 2018/11/15下午3:13
 */
@Component
public class QueryAppBizLicenseAdapterImpl implements  QueryAppBizLicenseAdapter{

    private static final Logger logger = LoggerFactory.getLogger(QueryAppBizLicenseAdapterImpl.class);

    @Resource
    private AppBizLicenseFacade appBizLicenseFacade;

    @Override
    public List<AppBizLicense> queryAppBizLicenseByAppId(String appid) {
        GetAppLicenseByAppIdRequest getAppLicenseByAppIdRequest = new GetAppLicenseByAppIdRequest();
        getAppLicenseByAppIdRequest.setAppId(appid);
        MerchantResult<List<AppBizLicenseResult>> listMerchantResult =
            appBizLicenseFacade.queryAppBizLicenseByAppId(getAppLicenseByAppIdRequest);
        List<AppBizLicense> list = DataConverterUtils.convert(listMerchantResult.getData(), AppBizLicense.class);
        if (!listMerchantResult.isSuccess()) {
            logger.info("load local cache of applicense  false: error message={}", listMerchantResult.getRetMsg());
            list = Lists.newArrayList();
        }
        logger.info("load local cache of applicense : appid={}, license={}", appid, JSON.toJSONString(list));
        return list;
    }

    @Override
    public List<AppBizLicense> queryAllAppBizLicense() {
        BaseRequest request = new BaseRequest();
        MerchantResult<List<AppBizLicenseResult>> listMerchantResult =
            appBizLicenseFacade.queryAllAppBizLicense(request);
        List<AppBizLicense> licenses = DataConverterUtils.convert(listMerchantResult.getData(), AppBizLicense.class);
        if (!listMerchantResult.isSuccess()) {
            logger.info("load local cache of applicense  false: error message={}", listMerchantResult.getRetMsg());
        }
        logger.info("load local cache of applicense :  license={}" , JSON.toJSONString(licenses));
        return licenses;
    }
}
