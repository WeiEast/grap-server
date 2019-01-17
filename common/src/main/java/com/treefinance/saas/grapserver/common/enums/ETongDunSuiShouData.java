package com.treefinance.saas.grapserver.common.enums;

import java.util.Objects;

/**
 * @author:guoguoyun
 * @date:Created in 2019/1/17下午6:50
 */
public enum ETongDunSuiShouData {

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
     *  1天内设备关联身份证数
     */
    MI_DEVICE_1D("MD_IDCARD_1W", "deviceAssociatedIdentity1DCntCopy", (byte)6),

    /**
     * 1天内设备关联手机号数
     */
    MP_DEVICE_1D("MD_IDCARD_1W", "deviceAssociatedPhone1DCntCopy", (byte)7),

    /**
     * 1天内身份证关联设备数 / 1天内身份证使用过多设备进行申请
     */
    MD_IDCARD_1D("MD_IDCARD_1W", "identityAssociatedDevice1DCntCopy", (byte)8),

    /**
     * 1个月内身份证关联设备数/1个月内身份证使用过多设备进行申请
     */
    MD_IDCARD_1M("MD_IDCARD_1M", "identityAssociatedDevice1MCntCopy", (byte)9),
    /**
     * 7天内设备关联身份证数
     */
    MM_IDCARD_1W("MD_IDCARD_1W", "deviceAssociatedIdentity7DCntCopy", (byte)10),
    /**
     * 7天内设备关联手机数
     */
    MM_DEVICE_1W("MM_DEVICE_1W", "deviceAssociatedPhone7DCntCopy", (byte)11),
    /**
     * 7天内设备关联身份证数
     */
    MI_DEVICE_1W("MI_DEVICE_1W", "identityAssociatedDevice7DCntCopy", (byte)12),
    /**
     * 3个月内手机关联身份证数
     */
    MI_MOBILE_3W("MI_MOBILE_3W", "phoneAssociatedIdentity3MCntCopy", (byte)13),
    /**
     * 3个月内身份证关联邮箱数
     */
    ME_IDCARD_3M("ME_IDCARD_3M", "identityAssociatedMail3MCntCopy", (byte)14),
    /**
     * 3个月内身份证关联手机数
     */
    MM_IDCARD_3M("MM_IDCARD_3M", "identityAssociatedPhone3MCntCopy", (byte)15);

    private String name;
    private String text;
    private Byte code;

    ETongDunSuiShouData(String name, String text, Byte code) {
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
            for (ETongDunSuiShouData item : ETongDunSuiShouData.values()) {
                if (text.equals(item.getText())) {
                    return item.getCode();
                }
            }
        }
        return -1;
    }

    public static String getText(Byte code) {
        if (Objects.nonNull(code)) {
            for (ETongDunSuiShouData item : ETongDunSuiShouData.values()) {
                if (code.equals(item.getCode())) {
                    return item.getText();
                }
            }
        }
        return null;
    }

    public static ETongDunSuiShouData getETonddunData(Byte code) {
        if (Objects.nonNull(code)) {
            for (ETongDunSuiShouData item : ETongDunSuiShouData.values()) {
                if (code.equals(item.getCode())) {
                    return item;
                }
            }
        }
        return null;
    }

    public static String getName(Byte code) {
        if (Objects.nonNull(code)) {
            for (ETongDunSuiShouData item : ETongDunSuiShouData.values()) {
                if (code.equals(item.getCode())) {
                    return item.getName();
                }
            }
        }
        return null;
    }

}
