package com.treefinance.saas.grapserver.common.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by luoyihua on 2017/4/26.
 */
public enum ETaskLogResult {
    SUCCESS("SUCCESS", (byte) 1),
    FAIL("FAIL", (byte) 0);

    private Byte code;
    private String text;

    private ETaskLogResult(String text, Byte code) {
        this.code = code;
        this.text = text;
    }

    public Byte getCode() {
        return code;
    }

    public void setCode(Byte code) {
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public static Byte getCode(String text) {
        if (StringUtils.isNotEmpty(text)) {
            for (ETaskLogResult item : ETaskLogResult.values()) {
                if (text.equals(item.getText())) {
                    return item.getCode();
                }
            }
        }
        return -1;
    }
}
