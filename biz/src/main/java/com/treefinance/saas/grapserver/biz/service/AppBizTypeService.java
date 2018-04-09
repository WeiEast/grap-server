package com.treefinance.saas.grapserver.biz.service;

import com.alibaba.fastjson.JSON;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.treefinance.saas.grapserver.common.utils.DataConverterUtils;
import com.treefinance.saas.grapserver.dao.entity.AppBizLicense;
import com.treefinance.saas.grapserver.dao.entity.AppBizType;
import com.treefinance.saas.grapserver.dao.entity.AppBizTypeCriteria;
import com.treefinance.saas.grapserver.dao.mapper.AppBizTypeMapper;
import com.treefinance.saas.merchant.center.facade.request.grapserver.GetAppBizTypeRequest;
import com.treefinance.saas.merchant.center.facade.result.console.AppBizLicenseResult;
import com.treefinance.saas.merchant.center.facade.result.console.AppBizTypeResult;
import com.treefinance.saas.merchant.center.facade.result.console.MerchantResult;
import com.treefinance.saas.merchant.center.facade.service.AppBizTypeFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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



    @Resource
    private AppBizTypeFacade appBizTypeFacade;

    /**
     * 本地缓存
     */
    private final LoadingCache<Byte, AppBizType> cache = CacheBuilder.newBuilder()
            .refreshAfterWrite(5, TimeUnit.MINUTES)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build(CacheLoader.from(bizType -> {

                GetAppBizTypeRequest getAppBizTypeRequest = new GetAppBizTypeRequest();
                getAppBizTypeRequest.setBizType(bizType);
                MerchantResult<List<AppBizTypeResult>>  merchantResult = appBizTypeFacade.selectAppBizTypeByBizType(getAppBizTypeRequest);
                List<AppBizType> list = DataConverterUtils.convert(merchantResult.getData(),AppBizType.class);
                if(merchantResult.isSuccess())
                {
                    logger.info("load local cache of appbiztype : appid={},data={}", bizType, JSON.toJSONString(list));

                }
                else{
                    logger.info("load local cache of appbiztype false：error message {}",merchantResult.getRetMsg());
                }

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

        GetAppBizTypeRequest getAppBizTypeRequest = new GetAppBizTypeRequest();

        MerchantResult<List<AppBizTypeResult>>  merchantResult = appBizTypeFacade.selectAppBizTypeByBizType(getAppBizTypeRequest);
        List<AppBizType> list = DataConverterUtils.convert(merchantResult.getData(),AppBizType.class);

        this.cache.putAll(list.stream().collect(Collectors.toMap(AppBizType::getBizType, appBizType -> appBizType)));
        logger.info("加载业务类型数据：list={}", JSON.toJSONString(list));
    }
}
