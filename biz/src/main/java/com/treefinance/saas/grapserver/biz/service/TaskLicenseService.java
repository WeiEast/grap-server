package com.treefinance.saas.grapserver.biz.service;

import com.treefinance.saas.grapserver.biz.config.DiamondConfig;
import com.treefinance.saas.grapserver.common.enums.EBizType;
import com.treefinance.saas.grapserver.common.exception.AppIdUncheckException;
import com.treefinance.saas.grapserver.common.exception.ForbiddenException;
import com.treefinance.saas.grapserver.common.exception.base.MarkBaseException;
import com.treefinance.saas.grapserver.dao.entity.AppBizLicense;
import com.treefinance.saas.grapserver.dao.entity.AppLicense;
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
