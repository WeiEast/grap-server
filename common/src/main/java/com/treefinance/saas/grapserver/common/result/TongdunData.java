package com.treefinance.saas.grapserver.common.result;

/**
 * @author:guoguoyun
 * @date:Created in 2018/10/18下午7:32
 */
public class TongdunData {

    /**
     * 对应规则 {@link com.treefinance.saas.grapserver.common.enums.ETongdunData#name}
     */
    private String id;
    /**
     * 对应规则的数值
     */
    private String value;
    /**
     * 对应规则的评分
     */
    private String score;
    /**
     *关联借款人邮箱列表
     */
    private String associatedMail;
    /**
     * 关联借款人手机列表
     */
    private String associatedMobile;
    /**
     * 关联身份证列表
     */
    private String associatedIdentity;




    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getAssociatedMail() {
        return associatedMail;
    }

    public void setAssociatedMail(String associatedMail) {
        this.associatedMail = associatedMail;
    }

    public String getAssociatedMobile() {
        return associatedMobile;
    }

    public void setAssociatedMobile(String associatedMobile) {
        this.associatedMobile = associatedMobile;
    }

    public String getAssociatedIdentity() {
        return associatedIdentity;
    }

    public void setAssociatedIdentity(String associatedIdentity) {
        this.associatedIdentity = associatedIdentity;
    }

    @Override public String toString() {
        return "TongdunData{" + "id='" + id + '\'' + ", value='" + value + '\'' + ", score='" + score + '\''
            + ", associatedMail='" + associatedMail + '\'' + ", associatedMobile='" + associatedMobile + '\''
            + ", associatedIdentity='" + associatedIdentity + '\'' + '}';
    }
}
