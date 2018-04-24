package com.treefinance.saas.grapserver.biz.service;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.treefinance.saas.grapserver.biz.config.DiamondConfig;
import com.treefinance.saas.grapserver.common.enums.EBizType;
import com.treefinance.saas.grapserver.common.exception.AppIdUncheckException;
import com.treefinance.saas.grapserver.common.exception.ForbiddenException;
import com.treefinance.saas.grapserver.common.exception.base.MarkBaseException;
import com.treefinance.saas.grapserver.dao.entity.AppBizLicense;
import com.treefinance.saas.grapserver.dao.entity.AppLicense;
import com.treefinance.saas.merchant.center.facade.request.grapserver.QueryMerchantByAppIdRequest;
import com.treefinance.saas.merchant.center.facade.result.console.MerchantBaseResult;
import com.treefinance.saas.merchant.center.facade.result.console.MerchantResult;
import com.treefinance.saas.merchant.center.facade.service.MerchantBaseInfoFacade;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by luoyihua on 2017/5/10.
 */
@Service
public class TaskLicenseService {
    private static final Logger logger = LoggerFactory.getLogger(TaskLicenseService.class);

    @Autowired
    private AppLicenseService appLicenseService;
    @Autowired
    private AppBizLicenseService appBizLicenseService;
    @Autowired
    private DiamondConfig diamondConfig;
    @Autowired
    private MerchantBaseInfoFacade merchantBaseInfoFacade;

    public void verifyCreateTask(String appId, String uniqueId, EBizType bizType) throws ForbiddenException {

        String pattern = "^" + diamondConfig.getAppIdEnvironmentPrefix() + "_" + "[0-9a-zA-Z]{16}";
        String oldPattern = "[0-9a-zA-Z]{16}";//老商户是16位字符的appId
        boolean isMatchOld = Pattern.matches(oldPattern, appId);
        boolean isMatch = Pattern.matches(pattern, appId);
        if (!isMatch && !isMatchOld) {
            throw new AppIdUncheckException("appId=" + appId + " is illegal in this environment=" + diamondConfig.getAppIdEnvironmentPrefix());
        }

        if (StringUtils.isBlank(uniqueId)) {
            logger.error("创建任务是,uniqueId参数异常,uniqueId={}", uniqueId);
            throw new MarkBaseException("0", "uniqueId参数异常");
        }

        boolean hasCreateTaskAuth = false;
        AppLicense appLicense = appLicenseService.getAppLicense(appId);
        if (appLicense != null) {
            List<AppBizLicense> appBizLicenseList = appBizLicenseService.getByAppId(appId);
            if (!appBizLicenseList.isEmpty()) {
                for (AppBizLicense appBizLicense : appBizLicenseList) {
                    // 仅启用状态授权可用
                    if (!Byte.valueOf("1").equals(appBizLicense.getIsValid())) {
                        continue;
                    }
                    if (appBizLicense.getBizType().equals(bizType.getCode())) {
                        hasCreateTaskAuth = true;
                        break;
                    }
                }
            }
        }
        if (!hasCreateTaskAuth) {
            throw new ForbiddenException("Can not find license for app '" + appId + "' bizType '" + bizType + "'.");
        }

        //商户是否禁用状态
//        QueryMerchantByAppIdRequest request = new QueryMerchantByAppIdRequest();
//        request.setAppIds(Lists.newArrayList(appId));
//        MerchantResult<List<MerchantBaseResult>> rpcResult;
//        try {
//            rpcResult = merchantBaseInfoFacade.queryMerchantBaseListByAppId(request);
//        } catch (Exception e) {
//            logger.error("校验商户是否禁用时,调用商户中心异常,request={}", JSON.toJSONString(request), e);
//            throw e;
//        }
//
//        if (CollectionUtils.isEmpty(rpcResult.getData())) {
//            logger.info("校验商户是否禁用时,调用商户中心未查到商户信息,request={}", JSON.toJSONString(request));
//            throw new ForbiddenException("Can not find app , appId=" + appId);
//        }
//        MerchantBaseResult merchantBaseResult = rpcResult.getData().get(0);
//        if (Optional.fromNullable(merchantBaseResult.getIsActive()).or((byte) 0) == 0) {
//            logger.info("商户被禁用,appId={}", appId);
//            throw new ForbiddenException("app is forbidden , appId=" + appId);
//        }
    }
}
