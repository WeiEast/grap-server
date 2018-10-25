package com.treefinance.saas.grapserver.common.enums;

import java.util.Objects;

/**
 * @author:guoguoyun
 * @date:Created in 2018/10/25上午9:54
 */
public enum ETongdunDetailData {

    LevelA("LevelA", "一般消费分期平台", (byte)1),
    LevelB("LevelB", "互联网金融门户", (byte)2),
    LevelC("LevelC", "银行个人业务", (byte)3),
    LevelD("LevelD", "融资租赁", (byte)4),
    LevelE("LevelE", "财产保险", (byte)5),
    LevelF("LevelF", "担保", (byte)6),
    LevelG("LevelG", "大数据金融", (byte)7),
    LevelH("LevelH", "银行消费金融公司", (byte)8),
    LevelI("LevelI", "直销银行", (byte)9),
    LevelJ("LevelJ", "信用卡中心", (byte)10),
    LevelK("LevelK", "网上银行", (byte)11),
    LevelL("LevelL", "小额贷款公司", (byte)12),
    LevelM("LevelM", "P2P网贷", (byte)13),
    LevelN("LevelN", "大型消费金融公司", (byte)14),
    LevelO("LevelO", "银行小微贷款", (byte)15),
    LevelP("LevelP", "厂商汽车金融", (byte)16),
    LevelZ("LevelZ", "NULL", (byte)17);



    private String name;
    private String text;
    private Byte code;

    ETongdunDetailData(String name, String text, Byte code) {
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
            for (ETongdunDetailData item : ETongdunDetailData.values()) {
                if (text.equals(item.getText())) {
                    return item.getCode();
                }
            }
        }
        return -1;
    }

    public static String getText(Byte code) {
        if (Objects.nonNull(code)) {
            for (ETongdunDetailData item : ETongdunDetailData.values()) {
                if (code.equals(item.getText())) {
                    return item.getText();
                }
            }
        }
        return null;
    }

    public static String getName(Byte code) {
        if (Objects.nonNull(code)) {
            for (ETongdunDetailData item : ETongdunDetailData.values()) {
                if (code.equals(item.getCode())) {
                    return item.getName();
                }
            }
        }
        return null;
    }

}
