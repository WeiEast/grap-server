package com.treefinance.saas.grapserver.biz.service;

import com.alibaba.fastjson.JSON;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.treefinance.saas.assistant.variable.notify.client.VariableMessageHandler;
import com.treefinance.saas.assistant.variable.notify.model.VariableMessage;
import com.treefinance.saas.grapserver.biz.domain.CallbackBizTypeInfo;
import com.treefinance.saas.grapserver.biz.domain.CallbackConfig;
import com.treefinance.saas.grapserver.context.component.AbstractService;
import com.treefinance.saas.grapserver.facade.enums.EDataType;
import com.treefinance.saas.grapserver.manager.CallbackBizManager;
import com.treefinance.saas.grapserver.manager.CallbackConfigManager;
import com.treefinance.saas.grapserver.manager.domain.CallbackBizInfoBO;
import com.treefinance.saas.grapserver.manager.domain.CallbackConfigBO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author luoyihua on 2017/5/11.
 */
@Service
public class AppCallbackConfigService extends AbstractService implements InitializingBean, VariableMessageHandler {

    @Autowired
    private CallbackConfigManager callbackConfigManager;
    @Autowired
    private CallbackBizManager callbackBizManager;

    /**
     * 本地缓存<appId,callbackConfig>
     */
    private final LoadingCache<String, List<CallbackConfig>> callbackCache =
        CacheBuilder.newBuilder().refreshAfterWrite(5, TimeUnit.MINUTES).expireAfterAccess(5, TimeUnit.MINUTES).build(new CacheLoader<String, List<CallbackConfig>>() {
            @Override
            public List<CallbackConfig> load(String appId) {
                List<CallbackConfigBO> list = callbackConfigManager.listCallbackConfigsByAppId(appId);

                // 刷新类型
                list.forEach(config -> callbackTypeCache.refresh(config.getId()));

                return convert(list, CallbackConfig.class);
            }
        });

    /**
     * 本地缓存<callbackId,bizTypeInfo>
     */
    private final LoadingCache<Integer, List<CallbackBizTypeInfo>> callbackTypeCache =
        CacheBuilder.newBuilder().refreshAfterWrite(5, TimeUnit.MINUTES).expireAfterAccess(5, TimeUnit.MINUTES).build(new CacheLoader<Integer, List<CallbackBizTypeInfo>>() {
            @Override
            public List<CallbackBizTypeInfo> load(Integer callbackId) {
                List<CallbackBizInfoBO> list = callbackBizManager.listCallBackBizInfosByCallbackId(callbackId);

                return convert(list, CallbackBizTypeInfo.class);
            }
        });

    /**
     * 根据appId获取
     */
    private List<CallbackConfig> getConfigsByAppId(String appId) {
        if (StringUtils.isEmpty(appId)) {
            return Collections.emptyList();
        }

        List<CallbackConfig> list = this.callbackCache.getUnchecked(appId);
        logger.info("从本地缓存中获取appId={}的回调配置为list={}", appId, JSON.toJSONString(list));

        return list;
    }

    private Map<Integer, List<CallbackBizTypeInfo>> getCallbackBizTypeMap(List<Integer> callbackIds) {
        Map<Integer, List<CallbackBizTypeInfo>> map;
        try {
            map = this.callbackTypeCache.getAll(callbackIds);
        } catch (ExecutionException e) {
            throw new UncheckedExecutionException(e);
        }
        logger.info("从本地缓存中获取callbackIds={}的回调业务类型数据map={}", JSON.toJSONString(callbackIds), JSON.toJSONString(map));
        return map;
    }

    /**
     * 获取指定业务类型的回调配置： 如果有配置该业务类型，则使用该业务类型；没有则使用全局配置
     */
    public List<CallbackConfig> queryCallbackConfigsByAppIdAndBizType(String appId, Byte bizType, EDataType dataType) {
        // 1.查询所有回调
        List<CallbackConfig> list = getConfigsByAppId(appId);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }

        // 2.查询回调类型
        List<Integer> callbackIds =
            list.stream().filter(config -> config != null && dataType.getType().equals(config.getDataType())).map(CallbackConfig::getId).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(callbackIds)) {
            return Collections.emptyList();
        }

        Byte defaultType = Byte.valueOf("0");

        // 3.根据业务类型匹配：如果存在此类型回调则使用，不存在则使用默认
        Map<Byte, List<Integer>> callbackBizTypeMap = getBizTypeToCallbackIdMapping(callbackIds, bizType, defaultType);

        callbackIds = callbackBizTypeMap.get(bizType);
        if (CollectionUtils.isEmpty(callbackIds)) {
            callbackIds = callbackBizTypeMap.get(defaultType);
            if (CollectionUtils.isEmpty(callbackIds)) {
                return Collections.emptyList();
            }
        }

        List<Integer> expectedIds = callbackIds;

        List<CallbackConfig> configs = list.stream().filter(config -> expectedIds.contains(config.getId())).collect(Collectors.toList());
        logger.info("根据业务类型匹配回调配置结果, configs={}", JSON.toJSONString(configs));
        return configs;
    }

    private Map<Byte, List<Integer>> getBizTypeToCallbackIdMapping(List<Integer> callbackIds, Byte bizType, Byte defaultType) {
        return getCallbackBizTypeMap(callbackIds).entrySet().stream().flatMap(entry -> entry.getValue().stream())
            .filter(info -> bizType.equals(info.getBizType()) || defaultType.equals(info.getBizType()))
            .collect(Collectors.groupingBy(CallbackBizTypeInfo::getBizType, Collectors.mapping(CallbackBizTypeInfo::getCallbackId, Collectors.toList())));
    }

    @Override
    public void afterPropertiesSet() {
        // 1. 初始化appCallback
        List<CallbackConfigBO> configs = callbackConfigManager.listCallbackConfigs();
        logger.info("加载callback信息: callback={}", JSON.toJSONString(configs));
        if (CollectionUtils.isEmpty(configs)) {
            return;
        }
        Map<String, List<CallbackConfig>> configMap =
            configs.stream().map(config -> convert(config, CallbackConfig.class)).collect(Collectors.groupingBy(CallbackConfig::getAppId));
        this.callbackCache.putAll(configMap);

        // 2. 初始化callBackType
        List<CallbackBizInfoBO> bizTypeInfos = callbackBizManager.listCallBackBizInfos();

        logger.info("加载callbackType信息: callbackType={}", JSON.toJSONString(bizTypeInfos));
        if (CollectionUtils.isEmpty(bizTypeInfos)) {
            return;
        }

        Map<Integer, List<CallbackBizTypeInfo>> bizTypeMap =
            bizTypeInfos.stream().map(biz -> convert(biz, CallbackBizTypeInfo.class)).collect(Collectors.groupingBy(CallbackBizTypeInfo::getCallbackId));
        this.callbackTypeCache.putAll(bizTypeMap);
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
            logger.warn("处理配置更新消息失败：VariableId非法，config={}", JSON.toJSONString(variableMessage));
            return;
        }
        this.callbackCache.refresh(appId);
    }

}
