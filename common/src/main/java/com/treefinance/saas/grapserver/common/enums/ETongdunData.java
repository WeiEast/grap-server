package com.treefinance.saas.grapserver.common.enums;

import java.util.Objects;

/**
 * @author:guoguoyun
 * @date:Created in 2018/10/18下午7:12
 */
public enum ETongdunData {
    /**
     * 12个月内申请借贷的平台数
     */
    MP_LOAN_12M("MP_LOAN_12M", "loanApply12MCntCopy", (byte)1),
    /**
     * 6个月内申请借贷的平台数
     */
    MP_LOAN_6M("MP_LOAN_6M", "loanApply6MCntCopy", (byte)2),
    /**
     * 3个月内申请借贷的平台数
     */
    MP_LOAN_3M("MP_LOAN_3M", "loanApply3MCntCopy", (byte)3),
    /**
     * 1个月内申请借贷的平台数
     */
    MP_LOAN_1M("MP_LOAN_1M", "loanApply1MCntCopy", (byte)4),
    /**
     * 7天内申请借贷的平台数
     */
    MD_LOAN_1W("MD_LOAN_1W", "loanApply7DCntCopy", (byte)5),


    /**
     * 1个月内身份证关联设备数
     */
    MD_IDCARD_1M("MD_IDCARD_1M", "identityAssociatedDevice1MCntCopy", (byte)6),
    /**
     * 7天内身份证关联设备数
     */
    MD_IDCARD_1W("MD_IDCARD_1W", "deviceAssociatedIdentity7DCntCopy", (byte)7),
    /**
     * 7天内设备关联手机数
     */
    MM_DEVICE_1W("MM_DEVICE_1W", "deviceAssociatedPhone7DCntCopy", (byte)8),
    /**
     * 7天内设备关联身份证数
     */
    MI_DEVICE_1W("MI_DEVICE_1W", "identityAssociatedDevice7DCntCopy", (byte)9),
    /**
     * 3个月内手机关联身份证数
     */
    MI_MOBILE_3W("MI_MOBILE_3W", "phoneAssociatedIdentity3MCntCopy", (byte)10),
    /**
     * 3个月内身份证关联邮箱数
     */
    ME_IDCARD_3M("ME_IDCARD_3M", "identityAssociatedMail3MCntCopy", (byte)11),
    /**
     * 3个月内身份证关联手机数
     */
    MM_IDCARD_3M("MM_IDCARD_3M", "identityAssociatedPhone3MCntCopy", (byte)12);


    private String name;
    private String text;
    private Byte code;

    ETongdunData(String name, String text, Byte code) {
        this.name = name;
        this.text = text;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public Byte getCode() {
        return code;
    }

    public boolean isOption(String option) {
        return this.getText().equals(option);
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
                if (code.equals(item.getCode())) {
                    return item.getText();
                }
            }
        }
        return null;
    }
    public static ETongdunData getETonddunData(Byte code) {
        if (Objects.nonNull(code)) {
            for (ETongdunData item : ETongdunData.values()) {
                if (code.equals(item.getCode())) {
                    return item;
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
