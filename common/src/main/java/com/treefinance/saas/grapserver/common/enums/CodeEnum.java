package com.treefinance.saas.grapserver.common.enums;

import java.util.Objects;

/**
 * @author 张琰佳
 * @since 2:24 PM 2019/1/23
 */
public enum CodeEnum {
    /**
     * 运营商
     */
    OPERATOR_START("10100101","300001"),
    OPERATOR_CHOOSE_LIST("10100102","300501"),
    OPERATOR_LIST_SURE("10100103","300601"),
    OPERATOR_LIST_CANCEL("10100104","300602"),
    OPERATOR_MOBILE_SURE("10100105","300502"),
    OPERATOR_SELECT_AUTHORIZATION("10100106","300506"),
    OPERATOR_CANCEL_AUTHORIZATION("10100107","300507"),
    OPERATOR_LOOK_AUTHORIZATION("10100108","300503"),
    OPERATOR_EXIT_AUTHORIZATION("10100109","300301"),
    OPERATOR_EXIT_AUTHORIZATION_PAGE("10100110","300504"),
    OPERATOR_MAINTAIN("10100111","300505"),
    OPERATOR_FIND_PWD("10100201","300702"),
    OPERATOR_NEXT("10100202","300701"),
    OPERATOR_LAST("10100203","300703"),
    OPERATOR_FAQ("10100204","300704"),
    OPERATOR_FEEDBACK("10100205","300705"),

    /**
     * 电商
     */
    ECOMMERCE_CHOOSE_TAOBAO("30100101","100101"),
    ECOMMERCE_CHOOSE_("30100102","100102"),
    ECOMMERCE_CANCEL_AUTHORIZATION("30100104","100301"),
    ECOMMERCE_LIST_CANCEL("30100105","100103"),
    ECOMMERCE_THIRDPARTY_LOGIN_CANCEL("30100201","100401"),
    ECOMMERCE_AUTHORIZATION_QRCODE("30100301","100801"),
    ECOMMERCE_AUTHORIZATION_("30100302","100803"),
    ECOMMERCE_AUTHORIZATION_USERAGREEMENT("30100303","100802"),
    ECOMMERCE_AUTHORIZATION_FAQ("30100305","100804"),
    ECOMMERCE_AUTHORIZATION_FEEDBACK("30100306","100805");











    private String key;

    private String value;

    CodeEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static String getName(String key) {
        if (Objects.nonNull(key)) {
            for (CodeEnum item : CodeEnum.values()) {
                if (key.equals(item.getKey())) {
                    return item.getValue();
                }
            }
        }
        return null;
    }
}
