package com.treefinance.saas.grapserver.common.enums;

/**
 * 业务类型枚举
 */
public enum BizTypeEnum {
    EMAIL, ECOMMERCE, OPERATOR;

    public static Byte valueOfType(BizTypeEnum typeEnum) {
        if (typeEnum == null) {
            return null;
        }
        // 业务类型，1:账单，2：电商，3:运营商
        switch (typeEnum) {
            case EMAIL:
                return (byte) 1;
            case ECOMMERCE:
                return (byte) 2;
            case OPERATOR:
                return (byte) 3;
        }
        return null;
    }
}
