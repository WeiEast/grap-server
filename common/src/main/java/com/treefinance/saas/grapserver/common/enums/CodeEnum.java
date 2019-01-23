package com.treefinance.saas.grapserver.common.enums;

import java.util.Objects;

/**
 * @author 张琰佳
 * @since 2:24 PM 2019/1/23
 */
public enum CodeEnum {
    ;
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
