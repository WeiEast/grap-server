package com.treefinance.saas.grapserver.biz.service;

import com.alibaba.fastjson.JSON;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.treefinance.saas.assistant.variable.notify.client.VariableMessageHandler;
import com.treefinance.saas.assistant.variable.notify.model.VariableMessage;
import com.treefinance.saas.grapserver.biz.adapter.QueryAppBizLicenseAdapter;
import com.treefinance.saas.grapserver.common.enums.EBizType;
import com.treefinance.saas.grapserver.dao.entity.AppBizLicense;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author luoyihua on 2017/5/10.
 */
@Component
public class AppBizLicenseService implements InitializingBean, VariableMessageHandler {

    /**
     * logger
     */
    private static final Logger logger = LoggerFactory.getLogger(AppBizLicenseService.class);

    @Autowired
    private QueryAppBizLicenseAdapter queryAppBizLicenseAdapter;

    /**
     * 本地缓存
     */
    private final LoadingCache<String, List<AppBizLicense>> cache =
        CacheBuilder.newBuilder().refreshAfterWrite(5, TimeUnit.MINUTES).expireAfterWrite(5, TimeUnit.MINUTES)
            .build(CacheLoader.from(appid -> queryAppBizLicenseAdapter.queryAppBizLicenseByAppId(appid)));

    /**
     * 根据appId获取授权
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
    }

    /**
     * 查询所有授权
     */
    public List<AppBizLicense> getAll() {
        List<AppBizLicense> licenses = Lists.newArrayList();
        cache.asMap().values().forEach(licenses::addAll);
        return licenses;
    }

    /**
     * 是否显示授权协议
     *
     * @param appId appId
     * @param type bizType
     * @return boolean
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
    }

    /**
     * 是否显示问卷调查表
     *
     * @param appId appId
     * @param type bizType
     * @return boolean
     */
    public boolean isShowQuestionnaire(String appId, String type) {
        Byte bizType = EBizType.getCode(type);
        if (bizType == null) {
            return false;
        }

        List<AppBizLicense> list = getByAppId(appId);
        if (CollectionUtils.isEmpty(list)) {
            return false;
        }
        Optional<AppBizLicense> optional =
            list.stream().filter(appBizLicense -> bizType.equals(appBizLicense.getBizType()))
                .filter(appBizLicense -> appBizLicense.getQuestionaireRate() > 0).findFirst();
        if (optional == null || !optional.isPresent()) {
            return false;
        }
        AppBizLicense appBizLicense = optional.get();
        int questionnaireRate = appBizLicense.getQuestionaireRate();
        if (questionnaireRate >= 100) {
            return true;
        }
        int random = RandomUtils.nextInt(0, 101);
        return questionnaireRate >= random;
    }

    /**
     * 是否显示意见反馈
     *
     * @param appId appId
     * @param type bizType
     * @return boolean
     */
    public boolean isShowFeedback(String appId, String type) {
        Byte bizType = EBizType.getCode(type);
        if (bizType == null) {
            return false;
        }

        List<AppBizLicense> list = getByAppId(appId);
        if (CollectionUtils.isEmpty(list)) {
            return false;
        }
        Optional<AppBizLicense> optional =
            list.stream().filter(appBizLicense -> bizType.equals(appBizLicense.getBizType()))
                .filter(appBizLicense -> appBizLicense.getFeedbackRate() > 0).findFirst();
        if (optional == null || !optional.isPresent()) {
            return false;
        }
        AppBizLicense appBizLicense = optional.get();
        int feedbackRate = appBizLicense.getFeedbackRate();
        if (feedbackRate >= 100) {
            return true;
        }
        int random = RandomUtils.nextInt(0, 101);
        return feedbackRate >= random;
    }

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
    public void afterPropertiesSet() throws Exception {
        List<AppBizLicense> licenses = queryAppBizLicenseAdapter.queryAllAppBizLicense();
        if (CollectionUtils.isEmpty(licenses)) {
            return;
        }
        this.cache.putAll(licenses.stream().collect(Collectors.groupingBy(AppBizLicense::getAppId)));
    }

    @Override
    public String getVariableName() {
        return "merchant-license";
    }

    public String getLicenseTemplate(String appId, String type) {
        Byte bizType = EBizType.getCode(type);
        if (bizType == null) {
            return "DEFAULT";
        }

        List<AppBizLicense> list = getByAppId(appId);
        if (CollectionUtils.isEmpty(list)) {
            return "DEFAULT";
        }
        List<AppBizLicense> filterList = list.stream()
            .filter(appBizLicense -> bizType.equals(appBizLicense.getBizType())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(filterList)) {
            return "DEFAULT";
        }
        return filterList.get(0).getLicenseTemplate();
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
