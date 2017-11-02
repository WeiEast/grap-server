package com.treefinance.saas.grapserver.facade.model.enums;

/**
 * 运营商监控状态枚举
 * Created by haojiahong on 2017/10/27.
 */
public enum ETaskOperatorMonitorStatus {

    CREAT_TASK((byte) 1, "任务创建"),
    COMFIRM_MOBILE((byte) 2, "确认手机号"),
    LOGIN_SUCCESS((byte) 3, "登陆成功"),
    CRAWL_SUCCESS((byte) 4, "抓取成功"),
    PROCESS_SUCCESS((byte) 5, "洗数成功"),
    CALLBACK_SUCCESS((byte) 6, "回调通知成功");


    private Byte status;
    private String name;

    ETaskOperatorMonitorStatus(Byte status, String name) {
        this.status = status;
        this.name = name;
    }

    public Byte getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }
}
