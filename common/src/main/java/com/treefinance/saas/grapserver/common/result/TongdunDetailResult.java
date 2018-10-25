package com.treefinance.saas.grapserver.common.result;

import java.util.List;
import java.util.Map;

/**
 * @author:guoguoyun
 * @date:Created in 2018/10/25上午11:13
 */
public class TongdunDetailResult {

    /**
     * 对应规则 {@link com.treefinance.saas.grapserver.common.enums.ETongdunData#name}
     */
    private String id;


    /**
     * 对应规则的数值
     */
    private String value;


    /**
     * 对应具体详细的结果
     */
    private Map<String,Map>  details;

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

    public Map<String, Map> getDetails() {
        return details;
    }

    public void setDetails(Map<String, Map> details) {
        this.details = details;
    }

    @Override public String toString() {
        return "TongdunDetailResult{" + "id='" + id + '\'' + ", value='" + value + '\'' + ", details=" + details + '}';
    }
}
