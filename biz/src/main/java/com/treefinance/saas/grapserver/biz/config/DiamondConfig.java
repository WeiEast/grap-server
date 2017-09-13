package com.treefinance.saas.grapserver.biz.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.diamond.client.extend.annotation.AfterUpdate;
import com.github.diamond.client.extend.annotation.BeforeUpdate;
import com.github.diamond.client.extend.annotation.DAttribute;
import com.github.diamond.client.extend.annotation.DResource;
import com.treefinance.saas.grapserver.common.enums.EBizType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by luoyihua on 2017/5/4.
 */
@Component("diamondConfig")
@Scope
@DResource
public class DiamondConfig {
    private static final Logger logger = LoggerFactory.getLogger(DiamondConfig.class);

    @DAttribute(key = "default.merchant.color.config")
    private String defaultMerchantColorConfig;

    @DAttribute(key = "sdk.title")
    private String sdkTitle;

    @DAttribute(key = "appId.environment.prefix")
    private String appIdEnvironmentPrefix;

    @DAttribute(key = "check.uniqueId.exclude.appId")
    private String excludeAppId;

    @DAttribute(key = "check.uniqueId.count.max")
    private Integer maxCount;

    @DAttribute(key = "moxie.url.fund.get.city-list")
    private String moxieUrlFundGetCityList;

    @BeforeUpdate
    public void before(String key, Object newValue) {
        logger.info(key + " update to " + newValue + " start...");
    }

    @AfterUpdate
    public void after(String key, Object newValue) {
        logger.info(key + " update to " + newValue + " end...");
    }

    public String getDefaultMerchantColorConfig() {
        return defaultMerchantColorConfig;
    }

    public void setDefaultMerchantColorConfig(String defaultMerchantColorConfig) {
        this.defaultMerchantColorConfig = defaultMerchantColorConfig;
    }

    public String getSdkTitle(EBizType bizType) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map sdkTitleMap = objectMapper.readValue(this.sdkTitle, Map.class);
            return sdkTitleMap.get(bizType.getText()).toString();
        } catch (Exception ex) {
            return "";
        }
    }

    public String getSdkTitle() {
        return this.sdkTitle;
    }

    public void setSdkTitle(String sdkTitle) {
        this.sdkTitle = sdkTitle;
    }

    public String getAppIdEnvironmentPrefix() {
        return appIdEnvironmentPrefix;
    }

    public void setAppIdEnvironmentPrefix(String appIdEnvironmentPrefix) {
        this.appIdEnvironmentPrefix = appIdEnvironmentPrefix;
    }

    public String getExcludeAppId() {
        return excludeAppId;
    }

    public void setExcludeAppId(String excludeAppId) {
        this.excludeAppId = excludeAppId;
    }

    public Integer getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(Integer maxCount) {
        this.maxCount = maxCount;
    }

    public String getMoxieUrlFundGetCityList() {
        return moxieUrlFundGetCityList;
    }

    public void setMoxieUrlFundGetCityList(String moxieUrlFundGetCityList) {
        this.moxieUrlFundGetCityList = moxieUrlFundGetCityList;
    }
}
