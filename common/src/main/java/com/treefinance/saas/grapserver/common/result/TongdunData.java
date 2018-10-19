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

    @Override public String toString() {
        return "TongdunData{" + "id='" + id + '\'' + ", value='" + value + '\'' + ", score='" + score + '\'' + '}';
    }
}
