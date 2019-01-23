package com.treefinance.saas.grapserver.common.enums;

import java.util.Objects;

/**
 * @author:guoguoyun
 * @date:Created in 2019/1/16下午2:12
 */
public enum ETongdunExtraData {

    /**
     * 身份证号码存在助学贷款逾期历史
     */
    IS_IDCARD_LOANOVER("IS_IDCARD_LOANOVER", "isIdentityStudentLoanOverdueHistory","身份证号对应人存在助学贷款逾期历史", (byte)1),
    /**
     * 手机号命中虚假号码库
     */
    IS_HIT_PHONEDUMMY("IS_HIT_PHONEDUMMY", "isHitPhoneDummyList", "手机号命中同盾虚假号码库",(byte)2),
    /**
     * 手机号命中通信小号库
     */
    IS_HIT_PHONESMURF("IS_HIT_PHONESMURF", "isHitPhoneSmurfList","手机号命中同盾通信小号库", (byte)3),

    /**
     * 身份证号码命中法院失信名单
     */
    IS_IDCARD_DISC("IS_IDCARD_DISC", "identityHitDiscourteous", "身份证命中法院失信名单",(byte)4),
    /**
     * 身份证号码命中法院执行名单
     */
    IS_IDCARD_CHANNEL("IS_IDCARD_CHANNEL", "identityHitChannelListing", "身份证命中法院执行名单",(byte)5),




    /**
     * 身份证号码命中贷款逾期名单
     */
    IS_IDCARD_CREDIT("IS_IDCARD_CREDIT", "identityHitCreditList","身份证命中信贷逾期名单", (byte)6),

    /**
     * 身份证号码命中高风险关注名单
     */
    IS_IDCARD_RISK("IS_IDCARD_RISK", "identityHitRiskList", "身份证命中高风险关注名单",(byte)7),





    /**
     * 身份证号码命中法院执行结案名单
     */
    IS_IDCARD_COURTCASE("IS_IDCARD_RISK", "identityHitCourtCasesList", "身份证命中法院执行结案名单",(byte)8),

    /**
     * 手机号码命中高风险关注名单
     */
    MOBILE_HIT_RISK("IS_IDCARD_RISK", "mobileHitRiskList","手机号命中高风险关注名单", (byte)9),



    /**
     * 手机号码命中贷款逾期名单
     */
    MOBILE_HIT_CREDIT("IS_IDCARD_RISK", "mobileHitCreditList", "手机号命中信贷逾期名单",(byte)10),




    /**
     * 身份证号命中犯罪通缉名单
     */
    IS_IDCARD_CRIM("IS_IDCARD_CRIM", "identityHitCrimList", "身份证命中犯罪通缉名单",(byte)11),

    /**
     * 身份证号命中车辆租赁违约名单
     */
    IS_IDCARD_VEHICLE("IS_IDCARD_VEHICLE", "identityHitVehicleLeaseList", "身份证命中车辆租赁违约名单",(byte)12);

    private String name;
    private String text;
    private String value;
    private Byte code;

    ETongdunExtraData(String name, String text,String value, Byte code) {
        this.name = name;
        this.text = text;
        this.value =value;
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

    public String getValue() {
        return value;
    }

    public boolean isOption(String option) {
        return this.getText().equals(option);
    }

    public static Byte getCode(String text) {
        if (Objects.nonNull(text)) {
            for (ETongdunExtraData item : ETongdunExtraData.values()) {
                if (text.equals(item.getText())) {
                    return item.getCode();
                }
            }
        }
        return -1;
    }

    public static String getText(Byte code) {
        if (Objects.nonNull(code)) {
            for (ETongdunExtraData item : ETongdunExtraData.values()) {
                if (code.equals(item.getCode())) {
                    return item.getText();
                }
            }
        }
        return null;
    }

    public static String getValue(Byte code) {
        if (Objects.nonNull(code)) {
            for (ETongdunExtraData item : ETongdunExtraData.values()) {
                if (code.equals(item.getCode())) {
                    return item.getValue();
                }
            }
        }
        return null;
    }

    public static String getName(Byte code) {
        if (Objects.nonNull(code)) {
            for (ETongdunExtraData item : ETongdunExtraData.values()) {
                if (code.equals(item.getCode())) {
                    return item.getName();
                }
            }
        }
        return null;
    }
}
