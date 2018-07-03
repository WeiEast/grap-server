package com.treefinance.saas.grapserver.common.model.vo;

import java.io.Serializable;

/**
 * Created by yh-treefinance on 2018/7/3.
 */
public class AppQuestionnaireResultRequest implements Serializable {
    private String appId;
    private Long questionnaireId;
    private String questionnaireDesc;

    private String detailIds;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }
    
    public Long getQuestionnaireId() {
        return questionnaireId;
    }

    public void setQuestionnaireId(Long questionnaireId) {
        this.questionnaireId = questionnaireId;
    }

    public String getQuestionnaireDesc() {
        return questionnaireDesc;
    }

    public void setQuestionnaireDesc(String questionnaireDesc) {
        this.questionnaireDesc = questionnaireDesc;
    }

    public String getDetailIds() {
        return detailIds;
    }

    public void setDetailIds(String detailIds) {
        this.detailIds = detailIds;
    }
}
