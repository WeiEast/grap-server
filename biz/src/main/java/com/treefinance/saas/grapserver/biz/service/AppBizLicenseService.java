package com.treefinance.saas.grapserver.biz.service;

import com.alibaba.fastjson.JSON;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.treefinance.saas.assistant.variable.notify.client.VariableMessageHandler;
import com.treefinance.saas.assistant.variable.notify.model.VariableMessage;
import com.treefinance.saas.grapserver.biz.domain.BizLicenseInfo;
import com.treefinance.saas.grapserver.common.enums.EBizType;
import com.treefinance.saas.grapserver.context.component.AbstractService;
import com.treefinance.saas.grapserver.manager.BizLicenseManager;
import com.treefinance.saas.grapserver.manager.domain.BizLicenseInfoBO;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author luoyihua on 2017/5/10.
 */
@Component
public class AppBizLicenseService extends AbstractService implements InitializingBean, VariableMessageHandler {

    @Autowired
    private BizLicenseManager bizLicenseManager;

    /**
     * 本地缓存
     */
    private final LoadingCache<String, List<BizLicenseInfo>> cache =
        CacheBuilder.newBuilder().refreshAfterWrite(5, TimeUnit.MINUTES).expireAfterWrite(5, TimeUnit.MINUTES)
            .build(new CacheLoader<String, List<BizLicenseInfo>>() {
                @Override
                public List<BizLicenseInfo> load(String appId) {
                    List<BizLicenseInfoBO> list = bizLicenseManager.listBizLicenseInfosByAppId(appId);

                    return convert(list, BizLicenseInfo.class);
                }
            });

    /**
     * 根据appId获取授权
     */
    public List<BizLicenseInfo> getByAppId(String appId) {
        if (StringUtils.isEmpty(appId)) {
            return Collections.emptyList();
        }

        List<BizLicenseInfo> list = this.cache.getUnchecked(appId);
        logger.info("getByAppId: appId={},list={}", appId, JSON.toJSONString(list));

        return list;
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

        List<BizLicenseInfo> list = getByAppId(appId);
        if (CollectionUtils.isEmpty(list)) {
            return false;
        }
        long count = list.stream().filter(bizLicenseInfo -> Byte.valueOf("1").equals(bizLicenseInfo.getIsShowLicense()) && bizType.equals(bizLicenseInfo.getBizType())).count();
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
