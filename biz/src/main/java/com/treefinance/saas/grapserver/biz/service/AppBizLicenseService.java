package com.treefinance.saas.grapserver.biz.service;

import com.treefinance.saas.grapserver.biz.domain.BizLicenseInfo;

import java.util.List;
import java.util.Map;

/**
 * @author luoyihua on 2017/5/10.
 */

public interface AppBizLicenseService {
    /**
     * 根据appId获取授权
     */
    List<BizLicenseInfo> getByAppId(String appId);

    /**
     * 是否显示授权协议
     *
     * @param appId appId
     * @param type bizType
     * @return boolean
     */
    boolean isShowLicense(String appId, String type);

    /**
     * 是否显示问卷调查表
     *
     * @param appId appId
     * @param type bizType
     * @return boolean
     */
    boolean isShowQuestionnaire(String appId, String type);

    /**
     * 是否显示意见反馈
     *
     * @param appId appId
     * @param type bizType
     * @return boolean
     */
    boolean isShowFeedback(String appId, String type);

    Map<String, Boolean> isShowQuestionnaireOrFeedback(String appId, String type);

    String getLicenseTemplate(String appId, String type);
}
