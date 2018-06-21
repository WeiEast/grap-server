package com.treefinance.saas.grapserver.facade.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Good Luck Bro , No Bug !
 * 需要实时统计的任务环节
 *
 * @author haojiahong
 * @date 2018/6/19
 */
public enum ETaskStatLink {

    TASK_CREATE("任务创建", "任务创建", "task_log"),
    LOGIN_SUCCESS("登陆成功", "登陆成功", "task_log"),
    CRAWL_SUCCESS("抓取成功", "抓取成功", "task_log"),
    DATA_SAVE_SUCCESS("数据保存成功", "洗数成功", "task_log"),
    CALLBACK_SUCCESS("回调通知成功", "回调通知成功", "task_log"),
    TASK_SUCCESS("任务成功", "任务成功", "task_log");


    private String code;
    private String desc;
    private String source;

    ETaskStatLink(String code, String desc, String source) {
        this.code = code;
        this.desc = desc;
        this.source = source;
    }

    public static ETaskStatLink getItemByCode(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        for (ETaskStatLink item : ETaskStatLink.values()) {
            if (StringUtils.equalsIgnoreCase(item.getCode(), code)) {
                return item;
            }
        }
        return null;
    }


    public static List<String> getCodeListBySource(String source) {
        if (StringUtils.isBlank(source)) {
            return null;
        }
        List<String> codeList = new ArrayList<>();
        for (ETaskStatLink item : ETaskStatLink.values()) {
            if (StringUtils.equalsIgnoreCase(item.getSource(), source)) {
                codeList.add(item.getCode());
            }
        }
        return codeList;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
