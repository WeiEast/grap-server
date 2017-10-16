package com.treefinance.saas.grapserver.biz.service;

import com.alibaba.fastjson.JSON;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.treefinance.saas.assistant.config.listener.handler.ConfigUpdateMessageHandler;
import com.treefinance.saas.assistant.config.model.ConfigUpdateModel;
import com.treefinance.saas.assistant.config.model.enums.ConfigType;
import com.treefinance.saas.grapserver.common.model.dto.AppCallbackConfigDTO;
import com.treefinance.saas.grapserver.common.utils.DataConverterUtils;
import com.treefinance.saas.grapserver.dao.entity.AppCallbackBiz;
import com.treefinance.saas.grapserver.dao.entity.AppCallbackBizCriteria;
import com.treefinance.saas.grapserver.dao.entity.AppCallbackConfig;
import com.treefinance.saas.grapserver.dao.entity.AppCallbackConfigCriteria;
import com.treefinance.saas.grapserver.dao.mapper.AppCallbackBizMapper;
import com.treefinance.saas.grapserver.dao.mapper.AppCallbackConfigMapper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by luoyihua on 2017/5/11.
 */
@Service
public class AppCallbackConfigService implements InitializingBean, ConfigUpdateMessageHandler {
    /**
     * logger
     */
    private static final Logger logger = LoggerFactory.getLogger(AppCallbackConfigService.class);

    @Autowired
    private AppCallbackConfigMapper appCallbackConfigMapper;

    @Autowired
    private AppCallbackBizMapper appCallbackBizMapper;

    /**
     * 本地缓存<appId,callbackConfig>
     */
    private final LoadingCache<String, List<AppCallbackConfig>> callbackCache = CacheBuilder.newBuilder()
            .refreshAfterWrite(5, TimeUnit.MINUTES)
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .build(CacheLoader.from(appid -> {
                AppCallbackConfigCriteria appCallbackConfigCriteria = new AppCallbackConfigCriteria();
                appCallbackConfigCriteria.createCriteria().andAppIdEqualTo(appid);
                List<AppCallbackConfig> list = appCallbackConfigMapper.selectByExample(appCallbackConfigCriteria);
                if (list == null) {
                    list = Lists.newArrayList();
                }
                logger.info("load local cache of callback-configs : appid={}, configs={}", appid, JSON.toJSONString(list));
                // 刷新类型
                list.forEach(appCallbackConfig -> this.callbackTypeCache.refresh(appCallbackConfig.getId()));
                return list;
            }));

    /**
     * 本地缓存<callbackId,callbackType>
     */
    private final LoadingCache<Integer, List<AppCallbackBiz>> callbackTypeCache = CacheBuilder.newBuilder()
            .refreshAfterWrite(5, TimeUnit.MINUTES)
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .build(CacheLoader.from(callbackId -> {
                AppCallbackBizCriteria bizCriteria = new AppCallbackBizCriteria();
                bizCriteria.createCriteria().andCallbackIdEqualTo(callbackId);
                List<AppCallbackBiz> list = appCallbackBizMapper.selectByExample(bizCriteria);
                if (list == null) {
                    list = Lists.newArrayList();
                }
                logger.info("load local cache of callback-types : callbackId={}, callbackType={}", callbackId, JSON.toJSONString(list));
                return list;
            }));

    /**
     * 根据appId获取
     *
     * @param appId
     * @return
     */
    public List<AppCallbackConfig> getByAppId(String appId) {
        List<AppCallbackConfig> list = Lists.newArrayList();
        if (StringUtils.isEmpty(appId)) {
            return null;
        }
        try {
            list = this.callbackCache.get(appId);
            logger.info("从本地缓存中获取appId={}的回调配置为list={}", appId, JSON.toJSONString(list));
        } catch (ExecutionException e) {
            logger.error("获取appId={}授权信息失败", appId, e);
        }
        return list;
    }

    /**
     * 根据ID获取
     *
     * @param callbackIds
     * @return
     */
    public List<AppCallbackBiz> getCallbackTypeList(List<Integer> callbackIds) {
        List<AppCallbackBiz> list = Lists.newArrayList();
        if (CollectionUtils.isEmpty(callbackIds)) {
            return list;
        }
        try {
            Map<Integer, List<AppCallbackBiz>> map = this.callbackTypeCache.getAll(callbackIds);
            logger.info("从本地缓存中获取callbackIds={}的回调业务类型数据map={}", JSON.toJSONString(callbackIds), JSON.toJSONString(map));
            if (map != null) {
                map.forEach((callbackId, typeList) -> {
                    if (!CollectionUtils.isEmpty(typeList)) {
                        list.addAll(typeList);
                    }
                });
            }
        } catch (ExecutionException e) {
            logger.error("获取appId={}授权信息失败", callbackIds, e);
        }
        return list;
    }

    /**
     * 获取指定业务类型的回调配置：
     * 如果有配置该业务类型，则使用该业务类型；没有则使用全局配置
     *
     * @param appId
     * @param bizType
     * @return
     */
    public List<AppCallbackConfigDTO> getByAppIdAndBizType(String appId, Byte bizType) {
        // 1.查询所有回调
        List<AppCallbackConfig> list = getByAppId(appId);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        Byte defaultType = Byte.valueOf("0");
        // 2.查询回调类型
        List<Integer> callbackIds = list.stream().map(AppCallbackConfig::getId).collect(Collectors.toList());


        List<AppCallbackBiz> callbackBizs = getCallbackTypeList(callbackIds).stream()
                .filter(appCallbackBiz -> bizType.equals(appCallbackBiz.getBizType()) || defaultType.equals(appCallbackBiz.getBizType()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(callbackBizs)) {
            return null;
        }
        // 3.根据业务类型匹配：如果存在此类型回调则使用，不存在则使用默认
        Map<Byte, List<AppCallbackBiz>> callbackBizMap = callbackBizs.stream().collect(Collectors.groupingBy(AppCallbackBiz::getBizType));
        if (callbackBizMap.containsKey(bizType)) {
            List<Integer> bizCallbackIds = callbackBizMap.get(bizType).stream().map(AppCallbackBiz::getCallbackId).collect(Collectors.toList());
            List<AppCallbackConfig> bizConfigs = list.stream().filter(config -> bizCallbackIds.contains(config.getId())).collect(Collectors.toList());
            logger.info("根据业务类型匹配回调配置结果:bizConfigs={}", JSON.toJSONString(bizConfigs));
            return DataConverterUtils.convert(bizConfigs, AppCallbackConfigDTO.class);
        } else if (callbackBizMap.containsKey(defaultType)) {
            List<Integer> bizCallbackIds = callbackBizMap.get(defaultType).stream().map(AppCallbackBiz::getCallbackId).collect(Collectors.toList());
            List<AppCallbackConfig> defaultConfigs = list.stream().filter(config -> bizCallbackIds.contains(config.getId())).collect(Collectors.toList());
            logger.info("根据业务类型匹配回调配置结果:defaultConfigs={}", JSON.toJSONString(defaultConfigs));
            return DataConverterUtils.convert(defaultConfigs, AppCallbackConfigDTO.class);
        }
        return null;
    }

    @Override
    public List<ConfigType> getConfigType() {
        return Lists.newArrayList(ConfigType.MERCHANT_CALLBACK);
    }

    @Override
    public void updateConfig(ConfigUpdateModel configUpdateModel) {
        logger.info("收到配置更新消息：config={}", JSON.toJSONString(configUpdateModel));
        String appId = configUpdateModel.getConfigId();
        if (StringUtils.isEmpty(appId)) {
            logger.error("处理配置更新消息失败：configId非法，config={}", JSON.toJSONString(configUpdateModel));
            return;
        }
        this.callbackCache.refresh(appId);
//        this.callbackTypeCache.invalidateAll();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 1. 初始化appCallback
        AppCallbackConfigCriteria appCallbackConfigCriteria = new AppCallbackConfigCriteria();
        appCallbackConfigCriteria.createCriteria();
        List<AppCallbackConfig> list = appCallbackConfigMapper.selectByExample(appCallbackConfigCriteria);
        logger.info("加载callback信息: callback={}", JSON.toJSONString(list));
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        this.callbackCache.putAll(list.stream().collect(
                Collectors.groupingBy(AppCallbackConfig::getAppId)));

        // 2. 初始化callBackType
        AppCallbackBizCriteria bizCriteria = new AppCallbackBizCriteria();
        bizCriteria.createCriteria();
        List<AppCallbackBiz> appCallbackBizList = appCallbackBizMapper.selectByExample(bizCriteria);
        logger.info("加载callbackType信息: callbackType={}", JSON.toJSONString(list));
        if (CollectionUtils.isEmpty(appCallbackBizList)) {
            return;
        }
        this.callbackTypeCache.putAll(appCallbackBizList.stream().collect(
                Collectors.groupingBy(AppCallbackBiz::getCallbackId)));
    }
}
