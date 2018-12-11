/*
 * Copyright © 2015 - 2017 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.treefinance.saas.grapserver.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.treefinance.saas.grapserver.biz.domain.AppBizType;
import com.treefinance.saas.grapserver.biz.service.AppBizTypeService;
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
 * @author Jerry
 * @date 2018/12/12 01:43
 */
@Service("appBizTypeService")
public class AppBizTypeServiceImpl extends AbstractService implements InitializingBean,
    AppBizTypeService {
    @Resource
    private BizTypeManager bizTypeManager;

    /**
     * 本地缓存<bizType, {@link AppBizType}>
     */
    private final LoadingCache<Byte, AppBizType> cache =
        CacheBuilder.newBuilder().refreshAfterWrite(5, TimeUnit.MINUTES).expireAfterWrite(5, TimeUnit.MINUTES).build(new CacheLoader<Byte, AppBizType>() {
            @Override
            public AppBizType load(Byte bizType) {
                BizTypeInfoBO info = bizTypeManager.getBizTypeInfoByBizType(bizType);

                return convert(info, AppBizType.class);
            }
        });

    @Override
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
