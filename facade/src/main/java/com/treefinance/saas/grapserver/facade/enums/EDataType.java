package com.treefinance.saas.grapserver.facade.enums;

/**
 * Created by yh-treefinance on 2017/9/28.
 */
public enum EDataType {
    MAIN_STREAM((byte) 0, "主数据"),
    DELIVERY_ADDRESS((byte) 1, "收货地址"),
    OPERATOR_FLOW((byte) 2, "运营商流量");

    private Byte type;
    private String name;

    EDataType(Byte type, String name) {
        this.type = type;
        this.name = name;
    }

    public Byte getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    /**
     * type 转化
     *
     * @param type
     * @return
     */
    public static EDataType typeOf(Byte type) {
        for (EDataType dataType : values()) {
            if (dataType.getType().equals(type)) {
                return dataType;
            }
        }
        return null;
    }
}
