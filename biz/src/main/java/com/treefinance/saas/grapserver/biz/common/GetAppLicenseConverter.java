package com.treefinance.saas.grapserver.biz.common;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.fastjson.JSON;
import com.treefinance.saas.grapserver.dao.entity.AppLicense;
import com.treefinance.saas.merchant.center.facade.request.grapserver.GetAppLicenseRequest;
import com.treefinance.saas.merchant.center.facade.result.console.MerchantResult;
import com.treefinance.saas.merchant.center.facade.result.grapsever.AppLicenseResult;
import com.treefinance.saas.merchant.center.facade.service.AppLicenseFacade;

/**
 * @author:guoguoyun
 * @date:Created in 2018/11/15下午7:32
 */
@Component
public class GetAppLicenseConverter {


    private static final Logger logger = LoggerFactory.getLogger(GetAppLicenseConverter.class);

    @Resource
    private  AppLicenseFacade appLicenseFacade;

    public  AppLicense getAppLicenseByAppId(String appid) {
        GetAppLicenseRequest request = new GetAppLicenseRequest();
        request.setAppId(appid);
        MerchantResult<AppLicenseResult> result;
        try {
            result = appLicenseFacade.getAppLicense(request);
        } catch (RpcException e) {
            logger.error("获取appLicense失败，错误信息：{}", e.getMessage());
            return null;
        }
        if (!result.isSuccess()) {
            return null;
        }
        AppLicenseResult appLicenseResult = result.getData();
        AppLicense appLicense = new AppLicense();

        BeanUtils.copyProperties(appLicenseResult, appLicense);

        logger.info(JSON.toJSONString(appLicense));
        return appLicense;
    }
}
