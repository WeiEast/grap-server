package com.treefinance.saas.grapserver.common.enums;

import java.util.Objects;

/**
 * Created by luoyihua on 2017/4/27.
 */
public enum EBizType {
    EMAIL("EMAIL", (byte) 1),
    EMAIL_H5("EMAIL_H5", (byte) 1),
    ECOMMERCE("ECOMMERCE", (byte) 2),
    OPERATOR("OPERATOR", (byte) 3),
    FUND("FUND", (byte) 4),
    DIPLOMA("DIPLOMA", (byte) 7);

    private String text;
    private Byte code;

    EBizType(String text, Byte code) {
        this.text = text;
        this.code = code;
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
        if (Objects.nonNull(text)) {
            for (EBizType item : EBizType.values()) {
                if (text.equals(item.getText())) {
                    return item.getCode();
                }
            }
        }
        return -1;
    }

    public static String getName(Byte code) {
        if (Objects.nonNull(code)) {
            for (EBizType item : EBizType.values()) {
                if (code.equals(item.getCode())) {
                    return
                            item.getText();
                }
            }
        }
        return null;
    }

    public static EBizType from(String name) {
        try {
            return EBizType.valueOf(name);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("The task type '" + name + "' is unsupported.", e);
        }
    }

    public static EBizType of(String name) {
        for (EBizType item : EBizType.values()) {
            if (item.text.equalsIgnoreCase(name)) {
                return item;
            }
        }
        return null;
    }

    /**
     * 根据编码获取
     *
     * @param code
     * @return
     */
    public static EBizType getByCode(Byte code) {
        for (EBizType item : EBizType.values()) {
            if (item != EMAIL_H5 && item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }
}
