package com.treefinance.saas.grapserver.common.model.vo;

import java.io.Serializable;

/**
 * @author yh-treefinance on 2018/7/3.
 */
public class AppQuestionnaireResultRequest implements Serializable {

    private static final long serialVersionUID = -3044345595052488113L;

    private String appId;

    private Long questionnaireId;

    private String questionnaireDesc;

    private String detailIds;

    @Override
    public String toString() {
        return "AppQuestionnaireResultRequest{" +
                "appId='" + appId + '\'' +
                ", questionnaireId=" + questionnaireId +
                ", questionnaireDesc='" + questionnaireDesc + '\'' +
                ", detailIds='" + detailIds + '\'' +
                '}';
    }

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
