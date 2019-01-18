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
     * 7天内设备或身份证或手机号申请次数过多
     */
    MDIP_DEVICE_IDCARD_MOBILE__1W("MDIP_DEVICE_IDCARD_MOBILE__1W", "identityOrDeviceOrPhoneApply7DCnt", (byte)6),

    /**
     * 1个月内设备或身份证或手机号申请次数过多
     */
    MDIP_DEVICE_IDCARD_MOBILE__1M("MDIP_DEVICE_IDCARD_MOBILE__1M", "identityOrDeviceOrPhoneApply1MCnt", (byte)7),

    /**
     * 1天内身份证关联设备数
     */
    MD_IDCARD_1D("MD_IDCARD_1W", "identityAssociatedDevice1DCntCopy", (byte)8),

    /**
     * 1个月内身份证关联设备数
     */
    MD_IDCARD_1M("MD_IDCARD_1M", "identityAssociatedDevice1MCntCopy", (byte)9),

    /**
     * 7天内身份证关联设备数
     */
    MI_DEVICE_1W("MI_DEVICE_1W", "identityAssociatedDevice7DCntCopy", (byte)10),


    /**
     * 3个月内手机关联身份证数
     */
    MI_MOBILE_3W("MI_MOBILE_3W", "phoneAssociatedIdentity3MCntCopy", (byte)11),


    /**
     * 3个月内身份证关联邮箱数/3个月身份证关联多个申请信息
     */
    ME_IDCARD_3M("MMP_IDCARD_3M", "identityAssociatedMail3MCntCopy", (byte)12),
    /**
     * 3个月内身份证关联手机数//3个月身份证关联多个申请信息
     */
    MM_IDCARD_3M("MMP_IDCARD_3M", "identityAssociatedPhone3MCntCopy", (byte)13);

    /**
     * 枚举类名字
     */
    private String name;
    /**
     * 返回给调用方的字段
     */
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
