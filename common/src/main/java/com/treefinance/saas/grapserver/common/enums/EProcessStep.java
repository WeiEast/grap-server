package com.treefinance.saas.grapserver.common.enums;

/**
 * 业务流程步骤
 * Created by yh-treefinance on 2018/1/31.
 */
public enum EProcessStep {
    CREATE("create", "创建任务"),
    CONFIRM_LOGIN("confirm-login", "确认登录"),
    LOGIN("login", "登录"),
    CRAWL("crawl", "抓取"),
    PROCESS("process", "洗数"),
    CALLBACK("callback", "回调");

    /**
     * 步骤名称
     */
    private String name;

    /**
     * 步骤编码
     */
    private String code;

    EProcessStep(String code, String name) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }
}
