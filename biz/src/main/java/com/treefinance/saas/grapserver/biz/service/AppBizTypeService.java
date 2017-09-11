package com.treefinance.saas.grapserver.biz.service;

import com.alibaba.fastjson.JSON;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.treefinance.saas.grapserver.dao.entity.AppBizType;
import com.treefinance.saas.grapserver.dao.entity.AppBizTypeCriteria;
import com.treefinance.saas.grapserver.dao.mapper.AppBizTypeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by yh-treefinance on 2017/8/2.
 */
@Service
public class AppBizTypeService implements InitializingBean {
    /**
     * logger
     */
    private static final Logger logger = LoggerFactory.getLogger(AppBizTypeService.class);

    @Autowired
    private AppBizTypeMapper appBizTypeMapper;

    /**
     * 本地缓存
     */
    private final LoadingCache<Byte, AppBizType> cache = CacheBuilder.newBuilder()
            .refreshAfterWrite(5, TimeUnit.MINUTES)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build(CacheLoader.from(bizType -> {
                AppBizTypeCriteria criteria = new AppBizTypeCriteria();
                criteria.createCriteria().andBizTypeEqualTo(bizType);
                List<AppBizType> list = appBizTypeMapper.selectByExample(criteria);
                logger.info("load local cache of appbiztype : appid={},data={}", bizType, JSON.toJSONString(list));
                return list.get(0);
            }));

    /**
     * 获取类型
     *
     * @param bizType
     * @return
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
     *
     * @return
     */
    public List<AppBizType> getAllAppBizType() {
        return Lists.newArrayList(cache.asMap().values());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        AppBizTypeCriteria criteria = new AppBizTypeCriteria();
        criteria.createCriteria();
        List<AppBizType> list = appBizTypeMapper.selectByExample(criteria);
        this.cache.putAll(list.stream().collect(Collectors.toMap(AppBizType::getBizType, appBizType -> appBizType)));
        logger.info("加载业务类型数据：list={}", JSON.toJSONString(list));
    }
}
