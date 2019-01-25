package com.treefinance.saas.grapserver.common.model;

/**
 * @author:guoguoyun
 * @date:Created in 2019/1/22下午6:13
 */
public class TongdunDetailDTO {
    private String createdDatetime;
    private String lastUpdatedDatetime;
    private String policyName;
    private String policyScore;
    private String ruleHitDetail;
    private String ruleName;
    private String ruleScore;
    private String sequenceId;

    public String getCreatedDatetime() {
        return createdDatetime;
    }

    public void setCreatedDatetime(String createdDatetime) {
        this.createdDatetime = createdDatetime;
    }

    public String getLastUpdatedDatetime() {
        return lastUpdatedDatetime;
    }

    public void setLastUpdatedDatetime(String lastUpdatedDatetime) {
        this.lastUpdatedDatetime = lastUpdatedDatetime;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public String getPolicyScore() {
        return policyScore;
    }

    public void setPolicyScore(String policyScore) {
        this.policyScore = policyScore;
    }

    public String getRuleHitDetail() {
        return ruleHitDetail;
    }

    public void setRuleHitDetail(String ruleHitDetail) {
        this.ruleHitDetail = ruleHitDetail;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRuleScore() {
        return ruleScore;
    }

    public void setRuleScore(String ruleScore) {
        this.ruleScore = ruleScore;
    }

    public String getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(String sequenceId) {
        this.sequenceId = sequenceId;
    }

    @Override public String toString() {
        return "TongdunDetailDTO{" + "createdDatetime='" + createdDatetime + '\'' + ", lastUpdatedDatetime='"
            + lastUpdatedDatetime + '\'' + ", policyName='" + policyName + '\'' + ", policyScore='" + policyScore + '\''
            + ", ruleHitDetail='" + ruleHitDetail + '\'' + ", ruleName='" + ruleName + '\'' + ", ruleScore='"
            + ruleScore + '\'' + ", sequenceId='" + sequenceId + '\'' + '}';
    }
}
