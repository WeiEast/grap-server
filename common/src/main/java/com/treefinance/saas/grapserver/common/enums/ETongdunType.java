package com.treefinance.saas.grapserver.common.enums;

import java.util.Objects;

/**
 * @author:guoguoyun
 * @date:Created in 2018/10/25下午1:49
 */
public enum ETongdunType {

    EMAIL("EMAIL", "借款人邮箱详情", "借款人邮箱匹配借款事件的借款人邮箱关联的合作方详情",(byte)1),
    ID_CARD("ID_CARD", "借款人身份证详情","借款人身份证匹配借款事件的借款人身份证关联的合作方详情", (byte)2),
    MOBILE("MOBILE", "借款人手机详情","借款人手机匹配借款事件的借款人手机关联的合作方详情",(byte)3);


    private String name;
    private String text;
    private String secondtext;
    private Byte code;


    ETongdunType(String name, String text,String secondtext, Byte code) {
        this.name = name;
        this.text = text;
        this.secondtext = secondtext;
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

    public String getSecondtext() {
        return secondtext;
    }

    public void setSecondtext(String secondtext) {
        this.secondtext = secondtext;
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
                if (code.equals(item.getCode())) {
                    return item.getText();
                }
            }
        }
        return null;
    }

    public static String getSecondtext(Byte code) {
        if (Objects.nonNull(code)) {
            for (ETongdunType item : ETongdunType.values()) {
                if (code.equals(item.getCode())) {
                    return item.getSecondtext();
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
