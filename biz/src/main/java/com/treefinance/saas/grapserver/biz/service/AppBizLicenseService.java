package com.treefinance.saas.grapserver.biz.service;

import com.alibaba.fastjson.JSON;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.treefinance.saas.assistant.config.listener.handler.ConfigUpdateMessageHandler;
import com.treefinance.saas.assistant.config.model.ConfigUpdateModel;
import com.treefinance.saas.assistant.config.model.enums.ConfigType;
import com.treefinance.saas.grapserver.dao.entity.AppBizLicense;
import com.treefinance.saas.grapserver.dao.entity.AppBizLicenseCriteria;
import com.treefinance.saas.grapserver.dao.mapper.AppBizLicenseMapper;
import com.treefinance.saas.grapserver.common.enums.EBizType;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by luoyihua on 2017/5/10.
 */
@Component
public class AppBizLicenseService implements InitializingBean, ConfigUpdateMessageHandler {
    /**
     * logger
     */
    private static final Logger logger = LoggerFactory.getLogger(AppBizLicenseService.class);

    @Autowired
    private AppBizLicenseMapper appBizLicenseMapper;

    /**
     * 本地缓存
     */
    private final LoadingCache<String, List<AppBizLicense>> cache = CacheBuilder.newBuilder()
            .refreshAfterWrite(5, TimeUnit.MINUTES)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build(CacheLoader.from(appid -> {
                AppBizLicenseCriteria appBizLicenseCriteria = new AppBizLicenseCriteria();
                appBizLicenseCriteria.createCriteria().andAppIdEqualTo(appid);
                List<AppBizLicense> list = appBizLicenseMapper.selectByExample(appBizLicenseCriteria);
                if (list == null) {
                    list = Lists.newArrayList();
                }
                logger.info("load local cache of applicense : appid={}, license={}", appid, JSON.toJSONString(list));
                return list;
            }));

    /**
     * 根据appId获取授权
     *
     * @param appId
     * @return
     */
    public List<AppBizLicense> getByAppId(String appId) {
        List<AppBizLicense> list = Lists.newArrayList();
        if (StringUtils.isEmpty(appId)) {
            return list;
        }
        try {
            list = this.cache.get(appId);
            logger.info("getByAppId: appId={},list={}", appId, JSON.toJSONString(list));
        } catch (ExecutionException e) {
            logger.error("获取appId={}授权信息失败", appId, e);
        }
        return list;
//        AppBizLicenseCriteria appBizLicenseCriteria = new AppBizLicenseCriteria();
//        appBizLicenseCriteria.createCriteria().andAppIdEqualTo(appId);
//        return appBizLicenseMapper.selectByExample(appBizLicenseCriteria);
    }

    /**
     * 查询所有授权
     *
     * @return
     */
    public List<AppBizLicense> getAll() {
        List<AppBizLicense> licenses = Lists.newArrayList();
        cache.asMap().values().forEach(list -> licenses.addAll(list));
        return licenses;
    }

    /**
     * 是否显示授权协议
     *
     * @param appId
     * @param type
     * @return
     */
    public boolean isShowLicense(String appId, String type) {
        Byte bizType = EBizType.getCode(type);
        if (bizType == null) {
            return false;
        }

        List<AppBizLicense> list = getByAppId(appId);
        if (CollectionUtils.isEmpty(list)) {
            return false;
        }
        long count = list.stream().filter(appBizLicense -> Byte.valueOf("1").equals(appBizLicense.getIsShowLicense())
                && bizType.equals(appBizLicense.getBizType())).count();
        return count > 0;
//
//        AppBizLicenseCriteria appBizLicenseCriteria = new AppBizLicenseCriteria();
//        appBizLicenseCriteria.createCriteria()
//                .andAppIdEqualTo(appId)
//                .andBizTypeEqualTo(bizType);
//        List<AppBizLicense> list = appBizLicenseMapper.selectByExample(appBizLicenseCriteria);
//        if (CollectionUtils.isEmpty(list)) {
//            return false;
//        }
//        AppBizLicense license = list.get(0);
//        if (license != null && Byte.valueOf("1").equals(license.getIsShowLicense())) {
//            return true;
//        }
//        return false;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        AppBizLicenseCriteria appBizLicenseCriteria = new AppBizLicenseCriteria();
        appBizLicenseCriteria.createCriteria();
        // 加载所有授权信息
        List<AppBizLicense> licenses = appBizLicenseMapper.selectByExample(appBizLicenseCriteria);
        logger.info("加载app授权信息: licenses={}", JSON.toJSONString(licenses));
        if (CollectionUtils.isEmpty(licenses)) {
            return;
        }
        this.cache.putAll(licenses.stream().collect(Collectors.groupingBy(AppBizLicense::getAppId)));
    }

    @Override
    public List<ConfigType> getConfigType() {
        return Lists.newArrayList(ConfigType.MERCHANT_LICENSE, ConfigType.MERCHANT_BASE);
    }

    @Override
    public void updateConfig(ConfigUpdateModel configUpdateModel) {
        logger.info("收到配置更新消息：config={}", JSON.toJSONString(configUpdateModel));
        String appId = configUpdateModel.getConfigId();
        if (StringUtils.isEmpty(appId)) {
            logger.error("处理配置更新消息失败：configId非法，config={}", JSON.toJSONString(configUpdateModel));
            return;
        }
        this.cache.refresh(appId);
    }
}
