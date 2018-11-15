package com.treefinance.saas.grapserver.biz.common;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.treefinance.saas.grapserver.common.utils.DataConverterUtils;
import com.treefinance.saas.grapserver.dao.entity.AppBizLicense;
import com.treefinance.saas.merchant.center.facade.request.grapserver.GetAppLicenseByAppIdRequest;
import com.treefinance.saas.merchant.center.facade.result.console.AppBizLicenseResult;
import com.treefinance.saas.merchant.center.facade.result.console.MerchantResult;
import com.treefinance.saas.merchant.center.facade.service.AppBizLicenseFacade;

/**
 * @author:guoguoyun
 * @date:Created in 2018/11/15下午3:13
 */
@Component
public class QueryAppBizLicenseConverter {

    private static final Logger logger = LoggerFactory.getLogger(QueryBizTypeConverter.class);

    @Autowired
    private static AppBizLicenseFacade appBizLicenseFacade;

    public static List<AppBizLicense> queryAppBizLicenseByAppId(String appid) {
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
}
