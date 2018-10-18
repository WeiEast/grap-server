package com.treefinance.saas.grapserver.common.enums;

import java.util.Objects;

/**
 * @author:guoguoyun
 * @date:Created in 2018/10/18下午7:12
 */
public enum ETongdunData {
    MP_LOAN_12M("MP_LOAN_12M","loanApply12MCntCopy", (byte)1), MP_LOAN_6M("MP_LOAN_6M","loanApply6MCntCopy", (byte)2),
    MP_LOAN_3M("MP_LOAN_3M","loanApply3MCntCopy", (byte)3), MP_LOAN_1M("MP_LOAN_1M","loanApply1MCntCopy", (byte)4),
    MD_LOAN_1W("MD_LOAN_1W","loanApply7DCntCopy", (byte)5), MD_IDCARD_1M("MD_IDCARD_1M","identityAssociatedDevice1MCntCopy", (byte)6),
    MD_IDCARD_1W("MD_IDCARD_1W","deviceAssociatedIdentity7DCntCopy", (byte)7), MM_DEVICE_1W("MM_DEVICE_1W","deviceAssociatedPhone7DCntCopy", (byte)8),
    MI_DEVICE_1W("MI_DEVICE_1W","identityAssociatedDevice7DCntCopy", (byte)9),
    MI_MOBILE_3W("MI_MOBILE_3W","phoneAssociatedIdentity3MCntCopy", (byte)10),
    ME_IDCARD_3M("ME_IDCARD_3M","identityAssociatedMail3MCntCopy", (byte)11),
    MM_IDCARD_3M("MM_IDCARD_3M","identityAssociatedPhone3MCntCopy", (byte)12);


    private String name;
    private String text;
    private Byte code;

    ETongdunData(String name,String text, Byte code) {
        this.name= name;
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
            for (ETongdunData item : ETongdunData.values()) {
                if (text.equals(item.getText())) {
                    return item.getCode();
                }
            }
        }
        return -1;
    }
    public static String getText(Byte code) {
        if (Objects.nonNull(code)) {
            for (ETongdunData item : ETongdunData.values()) {
                if (code.equals(item.getText())) {
                    return item.getText();
                }
            }
        }
        return null;
    }

    public static String getName(Byte code) {
        if (Objects.nonNull(code)) {
            for (ETongdunData item : ETongdunData.values()) {
                if (code.equals(item.getCode())) {
                    return item.getName();
                }
            }
        }
        return null;
    }

}
