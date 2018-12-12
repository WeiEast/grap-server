package com.treefinance.saas.grapserver.common.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.io.Serializable;

/**
 * @author guoguoyun
 * @date 2018/10/18下午7:32
 */
@JsonInclude(Include.NON_NULL)
public class TongdunData implements Serializable {

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
     * 规则详情
     */
    private TongdunDetailData detail;

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

    public TongdunDetailData getDetail() {
        return detail;
    }

    public void setDetail(TongdunDetailData detail) {
        this.detail = detail;
    }

    @Override
    public String toString() {
        return "TongdunData{" + "id='" + id + '\'' + ", value='" + value + '\'' + ", score='" + score + '\'' + ", detail=" + detail + '}';
    }
}
