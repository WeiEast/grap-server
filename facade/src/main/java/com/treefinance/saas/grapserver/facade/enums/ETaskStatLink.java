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

    TASK_CREATE("任务创建", "taskCreate", "任务创建", "task_log"),
    LOGIN_SUCCESS("登陆成功", "loginSuccess", "登陆成功", "task_log"),
    CRAWL_SUCCESS("抓取成功", "crawlSuccess", "抓取成功", "task_log"),
    DATA_SAVE_SUCCESS("数据保存成功", "dataSaveSuccess", "洗数成功", "task_log"),
    CALLBACK_SUCCESS("回调通知成功", "callbackSuccess", "回调通知成功", "task_log"),
    TASK_SUCCESS("任务成功", "taskSuccess", "任务成功", "task_log"),

    NO_DATA("无数据", "noData", "无数据", "self-define");


    private String stepCode;
    private String statCode;
    private String desc;
    private String source;

    ETaskStatLink(String stepCode, String statCode, String desc, String source) {
        this.stepCode = stepCode;
        this.statCode = statCode;
        this.desc = desc;
        this.source = source;
    }

    public static String getStepCodeByStatCode(String statCode) {
        if (StringUtils.isBlank(statCode)) {
            return null;
        }
        for (ETaskStatLink item : ETaskStatLink.values()) {
            if (StringUtils.equals(item.getStatCode(), statCode)) {
                return item.getStepCode();
            }
        }
        return null;
    }

    public static String getDescByStatCode(String statCode) {
        if (StringUtils.isBlank(statCode)) {
            return null;
        }
        for (ETaskStatLink item : ETaskStatLink.values()) {
            if (StringUtils.equals(item.getStatCode(), statCode)) {
                return item.getDesc();
            }
        }
        return null;
    }

    public static List<String> getStatCodeListNotSelfDefine() {
        List<String> result = new ArrayList<>();
        for (ETaskStatLink item : ETaskStatLink.values()) {
            if ("self-define".equals(item.getSource())) {
                continue;
            }
            result.add(item.getStatCode());
        }
        return result;
    }


    public static ETaskStatLink getItemByStepCode(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        for (ETaskStatLink item : ETaskStatLink.values()) {
            if (StringUtils.equalsIgnoreCase(item.getStepCode(), code)) {
                return item;
            }
        }
        return null;
    }


    public static List<String> getStepCodeListBySource(String source) {
        if (StringUtils.isBlank(source)) {
            return null;
        }
        List<String> codeList = new ArrayList<>();
        for (ETaskStatLink item : ETaskStatLink.values()) {
            if (StringUtils.equalsIgnoreCase(item.getSource(), source)) {
                codeList.add(item.getStepCode());
            }
        }
        return codeList;
    }

    public String getStepCode() {
        return stepCode;
    }

    public void setStepCode(String stepCode) {
        this.stepCode = stepCode;
    }

    public String getStatCode() {
        return statCode;
    }

    public void setStatCode(String statCode) {
        this.statCode = statCode;
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
