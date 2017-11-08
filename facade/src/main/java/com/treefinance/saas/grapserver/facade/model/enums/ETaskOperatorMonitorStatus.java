package com.treefinance.saas.grapserver.facade.model.enums;

/**
 * 运营商监控状态枚举
 * Created by haojiahong on 2017/10/27.
 */
public enum ETaskOperatorMonitorStatus {

    CREATE_TASK((byte) 1, "任务创建"),
    CONFIRM_MOBILE((byte) 2, "确认手机号"),
    START_LOGIN((byte) 3, "开始登陆"),
    LOGIN_SUCCESS((byte) 4, "登陆成功"),
    CRAWL_SUCCESS((byte) 5, "抓取成功"),
    PROCESS_SUCCESS((byte) 6, "洗数成功"),
    CALLBACK_SUCCESS((byte) 7, "回调通知成功");


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

    public static ETaskOperatorMonitorStatus getMonitorStats(Byte status) {
        if (status != null) {
            for (ETaskOperatorMonitorStatus item : ETaskOperatorMonitorStatus.values()) {
                if (item.status.equals(status)) {
                    return item;
                }
            }
        }
        return null;
    }
}
