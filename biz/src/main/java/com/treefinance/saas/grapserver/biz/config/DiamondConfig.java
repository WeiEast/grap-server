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

    @DAttribute(key = "demo.h5.appIds")
    private String demoH5AppIds;

    @DAttribute(key = "moxie.fund.apiKey")
    private String moxieFundApiKey;

    @DAttribute(key = "moxie.fund.token")
    private String moxieFundToken;

    @DAttribute(key = "moxie.url.fund.get.city-list")
    private String moxieUrlFundGetCityList;

    @DAttribute(key = "moxie.url.fund.get.city-list-ex")
    private String moxieUrlFundGetCityListEx;

    @DAttribute(key = "moxie.url.fund.get.login-elements-ex")
    private String moxieUrlFundGetLoginElementsEx;

    @DAttribute(key = "moxie.url.fund.get.information")
    private String moxieUrlFundGetInformation;

    @DAttribute(key = "moxie.url.fund.post.tasks")
    private String moxieUrlFundPostTasks;

    @DAttribute(key = "moxie.url.fund.get.tasks.status")
    private String moxieUrlFundGetTasksStatus;

    @DAttribute(key = "moxie.url.fund.post.tasks.input")
    private String moxieUrlFundPostTasksInput;

    @DAttribute(key = "moxie.url.fund.get.funds")
    private String moxieUrlFundGetFunds;

    @DAttribute(key = "moxie.url.fund.get.funds-ex")
    private String moxieUrlFundGetFundsEx;

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

    public String getMoxieFundApiKey() {
        return moxieFundApiKey;
    }

    public void setMoxieFundApiKey(String moxieFundApiKey) {
        this.moxieFundApiKey = moxieFundApiKey;
    }

    public String getMoxieFundToken() {
        return moxieFundToken;
    }

    public void setMoxieFundToken(String moxieFundToken) {
        this.moxieFundToken = moxieFundToken;
    }

    public void setMoxieUrlFundGetCityList(String moxieUrlFundGetCityList) {
        this.moxieUrlFundGetCityList = moxieUrlFundGetCityList;
    }

    public String getMoxieUrlFundGetLoginElementsEx() {
        return moxieUrlFundGetLoginElementsEx;
    }

    public void setMoxieUrlFundGetLoginElementsEx(String moxieUrlFundGetLoginElementsEx) {
        this.moxieUrlFundGetLoginElementsEx = moxieUrlFundGetLoginElementsEx;
    }

    public String getMoxieUrlFundGetInformation() {
        return moxieUrlFundGetInformation;
    }

    public void setMoxieUrlFundGetInformation(String moxieUrlFundGetInformation) {
        this.moxieUrlFundGetInformation = moxieUrlFundGetInformation;
    }

    public String getMoxieUrlFundPostTasks() {
        return moxieUrlFundPostTasks;
    }

    public void setMoxieUrlFundPostTasks(String moxieUrlFundPostTasks) {
        this.moxieUrlFundPostTasks = moxieUrlFundPostTasks;
    }

    public String getMoxieUrlFundGetTasksStatus() {
        return moxieUrlFundGetTasksStatus;
    }

    public void setMoxieUrlFundGetTasksStatus(String moxieUrlFundGetTasksStatus) {
        this.moxieUrlFundGetTasksStatus = moxieUrlFundGetTasksStatus;
    }

    public String getMoxieUrlFundPostTasksInput() {
        return moxieUrlFundPostTasksInput;
    }

    public void setMoxieUrlFundPostTasksInput(String moxieUrlFundPostTasksInput) {
        this.moxieUrlFundPostTasksInput = moxieUrlFundPostTasksInput;
    }

    public String getMoxieUrlFundGetCityListEx() {
        return moxieUrlFundGetCityListEx;
    }

    public void setMoxieUrlFundGetCityListEx(String moxieUrlFundGetCityListEx) {
        this.moxieUrlFundGetCityListEx = moxieUrlFundGetCityListEx;
    }

    public String getMoxieUrlFundGetFunds() {
        return moxieUrlFundGetFunds;
    }

    public void setMoxieUrlFundGetFunds(String moxieUrlFundGetFunds) {
        this.moxieUrlFundGetFunds = moxieUrlFundGetFunds;
    }

    public String getMoxieUrlFundGetFundsEx() {
        return moxieUrlFundGetFundsEx;
    }

    public void setMoxieUrlFundGetFundsEx(String moxieUrlFundGetFundsEx) {
        this.moxieUrlFundGetFundsEx = moxieUrlFundGetFundsEx;
    }

    public String getDemoH5AppIds() {
        return demoH5AppIds;
    }

    public void setDemoH5AppIds(String demoH5AppIds) {
        this.demoH5AppIds = demoH5AppIds;
    }
}
