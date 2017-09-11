package com.treefinance.saas.grapserver.biz.service;

import com.treefinance.saas.grapserver.biz.config.DiamondConfig;
import com.treefinance.saas.grapserver.dao.entity.AppBizLicense;
import com.treefinance.saas.grapserver.dao.entity.AppLicense;
import com.treefinance.saas.grapserver.common.exception.AppIdUncheckException;
import com.treefinance.saas.grapserver.common.exception.ForbiddenException;
import com.treefinance.saas.grapserver.common.enums.EBizType;
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
    private AppLicenseServiceImpl appLicenseService;
    @Autowired
    private AppBizLicenseService appBizLicenseService;
    @Autowired
    private DiamondConfig diamondConfig;

    public void verifyCreateTask(String appId, EBizType bizType) throws ForbiddenException {

        String pattern = "^" + diamondConfig.getAppIdEnvironmentPrefix() + "_" + "[0-9a-zA-Z]{16}";
        String oldPattern = "[0-9a-zA-Z]{16}";//老商户是16位字符的appId
        boolean isMatchOld = Pattern.matches(oldPattern, appId);
        boolean isMatch = Pattern.matches(pattern, appId);
        if (!isMatch && !isMatchOld) {
            throw new AppIdUncheckException("appId=" + appId + " is illegal in this environment=" + diamondConfig.getAppIdEnvironmentPrefix());
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
                    if (appBizLicense.getBizType() == bizType.getCode()) {
                        hasCreateTaskAuth = true;
                        break;
                    }
                }
            }
        }
        if (!hasCreateTaskAuth) {
            throw new ForbiddenException("Can not find license for app '" + appId + "' bizType '" + bizType + "'.");
        }
    }
}
