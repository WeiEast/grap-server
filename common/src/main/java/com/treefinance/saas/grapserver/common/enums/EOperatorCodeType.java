package com.treefinance.saas.grapserver.common.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by luoyihua on 2017/5/8.
 */
public enum EOperatorCodeType {

    SMS("SMS", 0),
    IMG("IMG", 1),
    QR("QR", 2);

    private Integer code;
    private String text;

    private EOperatorCodeType(String text, Integer code) {
        this.code = code;
        this.text = text;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public static Integer getCode(String text) {
        if (StringUtils.isNotEmpty(text)) {
            for (EOperatorCodeType item : EOperatorCodeType.values()) {
                if (text.equals(item.getText())) {
                    return item.getCode();
                }
            }
        }
        return -1;
    }
}
