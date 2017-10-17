package com.treefinance.saas.grapserver.biz.service;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.grapserver.biz.common.CallbackSecureHandler;
import com.treefinance.saas.grapserver.common.utils.RemoteDataDownloadUtils;
import com.treefinance.saas.grapserver.dao.entity.AppLicense;
import com.treefinance.saas.knife.common.CommonStateCode;
import com.treefinance.saas.knife.result.Results;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;

/**
 * H5界面演示系统业务处理类
 * Created by haojiahong on 2017/10/17.
 */
@Service
public class DemoService {

    private static final Logger logger = LoggerFactory.getLogger(DemoService.class);

    @Autowired
    private CallbackSecureHandler callbackSecureHandler;
    @Autowired
    private AppLicenseService appLicenseService;

    public Object getFundData(String appId, String params) {

        AppLicense appLicense = appLicenseService.getAppLicense(appId);
        if (appLicense == null) {
            logger.error("公积金获取demo数据:通过appId={}查询密钥信息空", appId);
            return Results.newFailedResult(CommonStateCode.FAILURE);
        }
        try {
            params = URLDecoder.decode(params, "utf-8");
            params = callbackSecureHandler.decrypt(params, appLicense.getServerPrivateKey());
            logger.info("公积金获取demo数据:解密后的数据url为{}", JSON.toJSONString(params));
        } catch (Exception e) {
            logger.error("decryptRSAData failed", e);
            return Results.newFailedResult(e.getMessage(), CommonStateCode.FAILURE);
        }
        // oss 下载数据
        String data;
        try {
            byte[] result = RemoteDataDownloadUtils.download(params, byte[].class);
            // 数据体默认使用商户密钥加密
            data = callbackSecureHandler.decryptByAES(result, appLicense.getDataSecretKey());
        } catch (Exception e) {
            logger.error("downloadData failed", e);
            return Results.newFailedResult(e.getMessage(), CommonStateCode.FAILURE);
        }


        return null;
    }
}
