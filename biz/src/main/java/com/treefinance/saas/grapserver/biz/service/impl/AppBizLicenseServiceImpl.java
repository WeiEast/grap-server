/*
 * Copyright © 2015 - 2017 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.treefinance.saas.grapserver.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.treefinance.saas.assistant.variable.notify.client.VariableMessageHandler;
import com.treefinance.saas.assistant.variable.notify.model.VariableMessage;
import com.treefinance.saas.grapserver.biz.domain.BizLicenseInfo;
import com.treefinance.saas.grapserver.biz.service.AppBizLicenseService;
import com.treefinance.saas.grapserver.common.enums.EBizType;
import com.treefinance.saas.grapserver.context.component.AbstractService;
import com.treefinance.saas.grapserver.manager.BizLicenseManager;
import com.treefinance.saas.grapserver.manager.domain.BizLicenseInfoBO;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Jerry
 * @date 2018/12/12 01:39
 */
@Service("appBizLicenseService")
public class AppBizLicenseServiceImpl extends AbstractService implements InitializingBean, VariableMessageHandler, AppBizLicenseService {
    @Autowired
    private BizLicenseManager bizLicenseManager;

    /**
     * 本地缓存
     */
    private final LoadingCache<String, List<BizLicenseInfo>> cache =
        CacheBuilder.newBuilder().refreshAfterWrite(5, TimeUnit.MINUTES).expireAfterWrite(5, TimeUnit.MINUTES).build(new CacheLoader<String, List<BizLicenseInfo>>() {
            @Override
            public List<BizLicenseInfo> load(String appId) {
                List<BizLicenseInfoBO> list = bizLicenseManager.listBizLicenseInfosByAppId(appId);

                return convert(list, BizLicenseInfo.class);
            }
        });

    @Override
    public List<BizLicenseInfo> getByAppId(String appId) {
        if (StringUtils.isEmpty(appId)) {
            return Collections.emptyList();
        }

        List<BizLicenseInfo> list = this.cache.getUnchecked(appId);
        logger.info("getByAppId: appId={},list={}", appId, JSON.toJSONString(list));

        return list;
    }

    @Override
    public boolean isShowLicense(String appId, String type) {
        Byte bizType = EBizType.getCode(type);
        if (bizType == null) {
            return false;
        }

        List<BizLicenseInfo> list = getByAppId(appId);
        if (CollectionUtils.isEmpty(list)) {
            return false;
        }
        long count = list.stream().filter(bizLicenseInfo -> Byte.valueOf("1").equals(bizLicenseInfo.getIsShowLicense()) && bizType.equals(bizLicenseInfo.getBizType())).count();
        return count > 0;
    }

    @Override
    public boolean isShowQuestionnaire(String appId, String type) {
        Byte bizType = EBizType.getCode(type);
        if (bizType == null) {
            return false;
        }

        List<BizLicenseInfo> list = getByAppId(appId);
        if (CollectionUtils.isEmpty(list)) {
            return false;
        }
        Optional<BizLicenseInfo> optional =
            list.stream().filter(bizLicenseInfo -> bizType.equals(bizLicenseInfo.getBizType())).filter(bizLicenseInfo -> bizLicenseInfo.getQuestionaireRate() > 0).findFirst();
        if (optional == null || !optional.isPresent()) {
            return false;
        }
        BizLicenseInfo bizLicenseInfo = optional.get();
        int questionnaireRate = bizLicenseInfo.getQuestionaireRate();
        if (questionnaireRate >= 100) {
            return true;
        }
        int random = RandomUtils.nextInt(0, 101);
        return questionnaireRate >= random;
    }

    @Override
    public boolean isShowFeedback(String appId, String type) {
        Byte bizType = EBizType.getCode(type);
        if (bizType == null) {
            return false;
        }

        List<BizLicenseInfo> list = getByAppId(appId);
        if (CollectionUtils.isEmpty(list)) {
            return false;
        }
        Optional<BizLicenseInfo> optional =
            list.stream().filter(bizLicenseInfo -> bizType.equals(bizLicenseInfo.getBizType())).filter(bizLicenseInfo -> bizLicenseInfo.getFeedbackRate() > 0).findFirst();
        if (optional == null || !optional.isPresent()) {
            return false;
        }
        BizLicenseInfo bizLicenseInfo = optional.get();
        int feedbackRate = bizLicenseInfo.getFeedbackRate();
        if (feedbackRate >= 100) {
            return true;
        }
        int random = RandomUtils.nextInt(0, 101);
        return feedbackRate >= random;
    }

    @Override
    public Map<String, Boolean> isShowQuestionnaireOrFeedback(String appId, String type) {
        Map<String, Boolean> map = Maps.newHashMap();
        boolean questionnaireFlag = this.isShowQuestionnaire(appId, type);
        boolean feedbackFlag = this.isShowFeedback(appId, type);
        if (questionnaireFlag && feedbackFlag) {
            boolean flag = RandomUtils.nextBoolean();
            map.put("questionnaire", flag);
            map.put("feedback", !flag);
        } else {
            map.put("questionnaire", questionnaireFlag);
            map.put("feedback", feedbackFlag);
        }
        return map;
    }

    @Override
    public String getLicenseTemplate(String appId, String type) {
        Byte bizType = EBizType.getCode(type);
        if (bizType == null) {
            return "DEFAULT";
        }

        List<BizLicenseInfo> list = getByAppId(appId);
        if (CollectionUtils.isEmpty(list)) {
            return "DEFAULT";
        }
        List<BizLicenseInfo> filterList = list.stream().filter(bizLicenseInfo -> bizType.equals(bizLicenseInfo.getBizType())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(filterList)) {
            return "DEFAULT";
        }
        return filterList.get(0).getLicenseTemplate();
    }

    @Override
    public void afterPropertiesSet() {
        List<BizLicenseInfoBO> list = bizLicenseManager.listBizLicenseInfos();

        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        this.cache.putAll(list.stream().map(info -> convert(info, BizLicenseInfo.class)).collect(Collectors.groupingBy(BizLicenseInfo::getAppId)));
    }

    @Override
    public String getVariableName() {
        return "merchant-license";
    }

    @Override
    public void handleMessage(VariableMessage variableMessage) {
        logger.info("收到配置更新消息：config={}", JSON.toJSONString(variableMessage));
        String appId = variableMessage.getVariableId();
        if (StringUtils.isEmpty(appId)) {
            logger.error("处理配置更新消息失败：VariableId非法，config={}", JSON.toJSONString(variableMessage));
            return;
        }
        this.cache.refresh(appId);
    }
}
