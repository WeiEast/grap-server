package com.treefinance.saas.grapserver.facade.enums;

/**
 * Created by yh-treefinance on 2017/12/19.
 */
public enum EGrapStatus {
    SUCCESS("001", "抓取成功"),
    FAIL("002", "抓取失败"),
    RESULT_EMPTY("003", "抓取结果为空"),
    CANCEL("004", "任务取消"),;

    private String code;
    private String name;

    EGrapStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
