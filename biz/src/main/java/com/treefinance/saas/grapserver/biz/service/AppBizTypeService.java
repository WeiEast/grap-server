package com.treefinance.saas.grapserver.biz.service;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.treefinance.saas.grapserver.biz.common.QueryBizTypeConverter;
import com.treefinance.saas.grapserver.common.utils.DataConverterUtils;
import com.treefinance.saas.grapserver.dao.entity.AppBizType;
import com.treefinance.saas.merchant.center.facade.request.common.BaseRequest;
import com.treefinance.saas.merchant.center.facade.result.console.AppBizTypeResult;
import com.treefinance.saas.merchant.center.facade.result.console.MerchantResult;
import com.treefinance.saas.merchant.center.facade.service.AppBizTypeFacade;

/**
 * @author yh-treefinance on 2017/8/2.
 */
@Service
public class AppBizTypeService implements InitializingBean {

    /**
     * logger
     */
    private static final Logger logger = LoggerFactory.getLogger(AppBizTypeService.class);

    @Resource
    private AppBizTypeFacade appBizTypeFacade;

    /**
     * 本地缓存
     */
    private final LoadingCache<Byte, AppBizType> cache = CacheBuilder.newBuilder()
            .refreshAfterWrite(5, TimeUnit.MINUTES)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build(CacheLoader.from(bizType -> QueryBizTypeConverter.queryAppBizTypeByBizType(bizType).get(0)));

    /**
     * 获取类型
     */
    public AppBizType getAppBizType(Byte bizType) {
        try {
            return cache.get(bizType);
        } catch (ExecutionException e) {
            logger.error("getAppBizType error :  bizType=" + bizType, e);
        }
        return null;
    }

    /**
     * 获取所有类型
     */
    public List<AppBizType> getAllAppBizType() {
        return Lists.newArrayList(cache.asMap().values());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        BaseRequest getAppBizTypeRequest = new BaseRequest();
        MerchantResult<List<AppBizTypeResult>>  merchantResult = appBizTypeFacade.queryAllAppBizType(getAppBizTypeRequest);
        List<AppBizType> list = DataConverterUtils.convert(merchantResult.getData(),AppBizType.class);
        this.cache.putAll(list.stream().collect(Collectors.toMap(AppBizType::getBizType, appBizType -> appBizType)));
        logger.info("加载业务类型数据：list={}", JSON.toJSONString(list));
    }

}
