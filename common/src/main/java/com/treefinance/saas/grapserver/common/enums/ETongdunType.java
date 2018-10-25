package com.treefinance.saas.grapserver.common.enums;

import java.util.Objects;

/**
 * @author:guoguoyun
 * @date:Created in 2018/10/25下午1:49
 */
public enum ETongdunType {

    EMAIL("EMAIL", "借款人邮箱详情", (byte)1),
    ID_CARD("ID_CARD", "借款人身份证详情", (byte)2),
    MOBILE("MOBILE", "借款人手机详情", (byte)3);


    private String name;
    private String text;
    private Byte code;


    ETongdunType(String name, String text, Byte code) {
        this.name = name;
        this.text = text;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Byte getCode() {
        return code;
    }

    public void setCode(Byte code) {
        this.code = code;
    }

    public static Byte getCode(String text) {
        if (Objects.nonNull(text)) {
            for (ETongdunType item : ETongdunType.values()) {
                if (text.equals(item.getText())) {
                    return item.getCode();
                }
            }
        }
        return -1;
    }

    public static String getText(Byte code) {
        if (Objects.nonNull(code)) {
            for (ETongdunType item : ETongdunType.values()) {
                if (code.equals(item.getText())) {
                    return item.getText();
                }
            }
        }
        return null;
    }

    public static String getName(Byte code) {
        if (Objects.nonNull(code)) {
            for (ETongdunType item : ETongdunType.values()) {
                if (code.equals(item.getCode())) {
                    return item.getName();
                }
            }
        }
        return null;
    }
}
