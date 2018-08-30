package com.treefinance.saas.grapserver.biz.service;

import com.alibaba.fastjson.JSON;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.treefinance.saas.assistant.variable.notify.client.VariableMessageHandler;
import com.treefinance.saas.assistant.variable.notify.model.VariableMessage;
import com.treefinance.saas.grapserver.common.enums.EBizType;
import com.treefinance.saas.grapserver.common.utils.DataConverterUtils;
import com.treefinance.saas.grapserver.dao.entity.AppBizLicense;
import com.treefinance.saas.merchant.center.facade.request.common.BaseRequest;
import com.treefinance.saas.merchant.center.facade.request.grapserver.GetAppLicenseByAppIdRequest;
import com.treefinance.saas.merchant.center.facade.result.console.AppBizLicenseResult;
import com.treefinance.saas.merchant.center.facade.result.console.MerchantResult;
import com.treefinance.saas.merchant.center.facade.service.AppBizLicenseFacade;
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
 * Created by luoyihua on 2017/5/10.
 */
@Component
public class AppBizLicenseService implements InitializingBean, VariableMessageHandler {
    /**
     * logger
     */
    private static final Logger logger = LoggerFactory.getLogger(AppBizLicenseService.class);

    @Autowired
    private AppBizLicenseFacade appBizLicenseFacade;

    /**
     * 本地缓存
     */
    private final LoadingCache<String, List<AppBizLicense>> cache = CacheBuilder.newBuilder()
            .refreshAfterWrite(5, TimeUnit.MINUTES)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build(CacheLoader.from(appid -> {
                GetAppLicenseByAppIdRequest getAppLicenseByAppIdRequest = new GetAppLicenseByAppIdRequest();
                getAppLicenseByAppIdRequest.setAppId(appid);
                MerchantResult<List<AppBizLicenseResult>> listMerchantResult = appBizLicenseFacade.queryAppBizLicenseByAppId(getAppLicenseByAppIdRequest);
                List<AppBizLicense> list = DataConverterUtils.convert(listMerchantResult.getData(), AppBizLicense.class);
                if (!listMerchantResult.isSuccess()) {
                    logger.info("load local cache of applicense  false: error message={}", listMerchantResult.getRetMsg());
                    list = Lists.newArrayList();
                }
                logger.info("load local cache of applicense : appid={}, license={}", appid, JSON.toJSONString(list));
                return list;
            }));

    /**
     * 根据appId获取授权
     *
     * @param appId
     * @return
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
     *
     * @return
     */
    public List<AppBizLicense> getAll() {
        List<AppBizLicense> licenses = Lists.newArrayList();
        cache.asMap().values().forEach(list -> licenses.addAll(list));
        return licenses;
    }

    /**
     * 是否显示授权协议
     *
     * @param appId
     * @param type
     * @return
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
     * @param appId
     * @param type
     * @return
     */
    public boolean isShowQuestionaire(String appId, String type) {
        Byte bizType = EBizType.getCode(type);
        if (bizType == null) {
            return false;
        }

        List<AppBizLicense> list = getByAppId(appId);
        if (CollectionUtils.isEmpty(list)) {
            return false;
        }
        Optional<AppBizLicense> optional = list.stream()
                .filter(appBizLicense -> bizType.equals(appBizLicense.getBizType()))
                .filter(appBizLicense -> appBizLicense.getQuestionaireRate() > 0)
                .findFirst();
        if (optional == null || !optional.isPresent()) {
            return false;
        }
        AppBizLicense appBizLicense = optional.get();
        int questionaireRate = appBizLicense.getQuestionaireRate();
        if (questionaireRate >= 100) {
            return true;
        }
        int random = RandomUtils.nextInt(0, 101);
        return questionaireRate >= random;
    }


    /**
     * 是否显示意见反馈
     *
     * @param appId
     * @param type
     * @return
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
        Optional<AppBizLicense> optional = list.stream()
                .filter(appBizLicense -> bizType.equals(appBizLicense.getBizType()))
                .filter(appBizLicense -> appBizLicense.getFeedbackRate() > 0)
                .findFirst();
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


    public static void main(String[] args) {
        System.out.println(RandomUtils.nextBoolean());
    }


    public Map<String, Boolean> isShowQuestionnaireOrFeedback(String appId, String type) {
        Map<String, Boolean> map = Maps.newHashMap();
        boolean questionnaireFlag = this.isShowQuestionaire(appId, type);
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

        BaseRequest request = new BaseRequest();
        MerchantResult<List<AppBizLicenseResult>> listMerchantResult = appBizLicenseFacade.queryAllAppBizLicense(request);
        List<AppBizLicense> licenses = DataConverterUtils.convert(listMerchantResult.getData(), AppBizLicense.class);

        if (CollectionUtils.isEmpty(licenses)) {
            logger.info("加载app授权信息  false: error message={}", listMerchantResult.getRetMsg());
            return;
        }
        logger.info("加载app授权信息: licenses={}", JSON.toJSONString(licenses));
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
        List<AppBizLicense> filterList = list.stream().filter(appBizLicense -> bizType.equals(appBizLicense.getBizType())).collect(Collectors.toList());
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
