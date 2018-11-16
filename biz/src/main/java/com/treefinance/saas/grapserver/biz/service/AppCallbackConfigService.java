package com.treefinance.saas.grapserver.biz.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.treefinance.saas.assistant.variable.notify.client.VariableMessageHandler;
import com.treefinance.saas.assistant.variable.notify.model.VariableMessage;
import com.treefinance.saas.grapserver.biz.common.QueryAppCallBackBizConverter;
import com.treefinance.saas.grapserver.biz.common.QueryAppCallbackConfigConverter;
import com.treefinance.saas.grapserver.common.model.dto.AppCallbackConfigDTO;
import com.treefinance.saas.grapserver.common.utils.DataConverterUtils;
import com.treefinance.saas.grapserver.dao.entity.AppCallbackBiz;
import com.treefinance.saas.grapserver.dao.entity.AppCallbackConfig;
import com.treefinance.saas.grapserver.facade.enums.EDataType;
import com.treefinance.saas.merchant.center.facade.request.common.BaseRequest;
import com.treefinance.saas.merchant.center.facade.result.console.MerchantResult;
import com.treefinance.saas.merchant.center.facade.result.grapsever.AppCallbackBizResult;
import com.treefinance.saas.merchant.center.facade.result.grapsever.AppCallbackResult;
import com.treefinance.saas.merchant.center.facade.service.AppCallBackBizFacade;
import com.treefinance.saas.merchant.center.facade.service.AppCallbackConfigFacade;

/**
 * @author luoyihua on 2017/5/11.
 */
@Service
public class AppCallbackConfigService implements InitializingBean, VariableMessageHandler {

    /**
     * logger
     */
    private static final Logger logger = LoggerFactory.getLogger(AppCallbackConfigService.class);

    @Autowired
    private AppCallbackConfigFacade appCallbackConfigFacade;

    @Autowired
    private AppCallBackBizFacade appCallBackBizFacade;

    /**
     * 本地缓存<appId,callbackConfig>
     */
    private final LoadingCache<String, List<AppCallbackConfig>> callbackCache = CacheBuilder.newBuilder()
            .refreshAfterWrite(5, TimeUnit.MINUTES)
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .build(CacheLoader.from(appid -> {
                List<AppCallbackConfig> list = QueryAppCallbackConfigConverter.queryAppCallBackConfigByAppId(appid);
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
            .build(CacheLoader.from(QueryAppCallBackBizConverter::queryAppCallBackByCallbackId));

    /**
     * 根据appId获取
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
     */
    public List<AppCallbackBiz> getCallbackTypeList(List<Integer> callbackIds) {
        List<AppCallbackBiz> list = Lists.newArrayList();
        if (CollectionUtils.isEmpty(callbackIds)) {
            return list;
        }
        try {
            Map<Integer, List<AppCallbackBiz>> map = this.callbackTypeCache.getAll(callbackIds);
            logger.info("从本地缓存中获取callbackIds={}的回调业务类型数据map={}", JSON.toJSONString(callbackIds),
                JSON.toJSONString(map));
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
     * 获取指定业务类型的回调配置： 如果有配置该业务类型，则使用该业务类型；没有则使用全局配置
     */
    public List<AppCallbackConfigDTO> getByAppIdAndBizType(String appId, Byte bizType, EDataType dataType) {
        // 1.查询所有回调
        List<AppCallbackConfig> list = getByAppId(appId);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        Byte defaultType = Byte.valueOf("0");
        // 2.查询回调类型
        List<Integer> callbackIds =
            list.stream().filter(config -> config != null && dataType.getType().equals(config.getDataType()))
                .map(AppCallbackConfig::getId).collect(Collectors.toList());

        List<AppCallbackBiz> callbackBizs = getCallbackTypeList(callbackIds).stream()
            .filter(appCallbackBiz -> bizType.equals(appCallbackBiz.getBizType())
                    || defaultType.equals(appCallbackBiz.getBizType()))
            .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(callbackBizs)) {
            return null;
        }
        // 3.根据业务类型匹配：如果存在此类型回调则使用，不存在则使用默认
        Map<Byte, List<AppCallbackBiz>> callbackBizMap =
            callbackBizs.stream().collect(Collectors.groupingBy(AppCallbackBiz::getBizType));
        if (callbackBizMap.containsKey(bizType)) {
            return listAppCallbackConfigDTOS(list, bizType, callbackBizMap);
        } else if (callbackBizMap.containsKey(defaultType)) {
            return listAppCallbackConfigDTOS(list, defaultType, callbackBizMap);
        }
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 1. 初始化appCallback
        BaseRequest request = new BaseRequest();
        MerchantResult<List<AppCallbackResult>> listMerchantResult =
            appCallbackConfigFacade.queryAllAppCallBackConfig(request);
        List<AppCallbackConfig> list =
            DataConverterUtils.convert(listMerchantResult.getData(), AppCallbackConfig.class);

        logger.info("加载callback信息: callback={}", JSON.toJSONString(list));
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        this.callbackCache.putAll(list.stream().collect(Collectors.groupingBy(AppCallbackConfig::getAppId)));
        // 2. 初始化callBackType
        BaseRequest baseRequest = new BaseRequest();
        MerchantResult<List<AppCallbackBizResult>> listMerchantResult1 =
            appCallBackBizFacade.queryAllAppCallBack(baseRequest);
        List<AppCallbackBiz> appCallbackBizList =
            DataConverterUtils.convert(listMerchantResult1.getData(), AppCallbackBiz.class);

        logger.info("加载callbackType信息: callbackType={}", JSON.toJSONString(appCallbackBizList));
        if (CollectionUtils.isEmpty(appCallbackBizList)) {
            return;
        }
        this.callbackTypeCache
            .putAll(appCallbackBizList.stream().collect(Collectors.groupingBy(AppCallbackBiz::getCallbackId)));
    }

    @Override
    public String getVariableName() {
        return "merchant-callback";
    }

    @Override
    public void handleMessage(VariableMessage variableMessage) {
        logger.info("收到配置更新消息：config={}", JSON.toJSONString(variableMessage));
        String appId = variableMessage.getVariableId();
        if (StringUtils.isEmpty(appId)) {
            logger.error("处理配置更新消息失败：VariableId非法，config={}", JSON.toJSONString(variableMessage));
            return;
        }
        this.callbackCache.refresh(appId);
    }

    private List<AppCallbackConfigDTO> listAppCallbackConfigDTOS(
            List<AppCallbackConfig> list, Byte bizType, Map<Byte, List<AppCallbackBiz>> callbackBizMap) {
        List<Integer> bizCallbackIds =
                callbackBizMap.get(bizType).stream().map(AppCallbackBiz::getCallbackId).collect(Collectors.toList());
        List<AppCallbackConfig> defaultConfigs =
                list.stream().filter(config -> bizCallbackIds.contains(config.getId())).collect(Collectors.toList());
        logger.info("根据业务类型匹配回调配置结果:defaultConfigs={}", JSON.toJSONString(defaultConfigs));
        return DataConverterUtils.convert(defaultConfigs, AppCallbackConfigDTO.class);
    }

}
