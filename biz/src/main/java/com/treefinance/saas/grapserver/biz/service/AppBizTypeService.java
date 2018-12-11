package com.treefinance.saas.grapserver.biz.service;

import com.alibaba.fastjson.JSON;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.treefinance.saas.grapserver.biz.domain.AppBizType;
import com.treefinance.saas.grapserver.context.component.AbstractService;
import com.treefinance.saas.grapserver.manager.BizTypeManager;
import com.treefinance.saas.grapserver.manager.domain.BizTypeInfoBO;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author yh-treefinance on 2017/8/2.
 */
@Service
public class AppBizTypeService extends AbstractService implements InitializingBean {

    @Resource
    private BizTypeManager bizTypeManager;

    /**
     * 本地缓存<bizType, {@link AppBizType}>
     */
    private final LoadingCache<Byte, AppBizType> cache = CacheBuilder.newBuilder()
            .refreshAfterWrite(5, TimeUnit.MINUTES)
            .expireAfterWrite(5, TimeUnit.MINUTES)
        .build(new CacheLoader<Byte, AppBizType>() {
            @Override
            public AppBizType load(Byte bizType) {
                BizTypeInfoBO info = bizTypeManager.getBizTypeInfoByBizType(bizType);

                return convert(info, AppBizType.class);
            }
        });

    /**
     * 获取类型
     */
    public AppBizType getAppBizType(Byte bizType) {
        return cache.getUnchecked(bizType);
    }

    @Override
    public void afterPropertiesSet() {
        List<BizTypeInfoBO> list = bizTypeManager.listBizTypes();
        this.cache.putAll(list.stream().map(info -> convertStrict(info, AppBizType.class)).collect(Collectors.toMap(AppBizType::getBizType, Function.identity())));
        logger.info("加载业务类型数据：list={}", JSON.toJSONString(list));
    }

}
