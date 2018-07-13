package com.treefinance.saas.grapserver.facade.enums;

/**
 * 任务属性
 * Created by yh-treefinance on 2017/11/1.
 */
public enum ETaskAttribute {
    FUND_MOXIE_TASKID("moxie-taskId", "魔蝎任务Id"),
    FUND_MOXIE_VERIFY_CODE_COUNT("moxieVerifyCodeCount", "魔蝎任务验证码输入次数"),
    OPERATOR_GROUP_CODE("groupCode", "运营商编码"),
    OPERATOR_GROUP_NAME("groupName", "运营商名称"),
    MOBILE("mobile", "手机号"),
    NAME("name", "姓名"),
    ID_CARD("idCard", "身份证号"),
    SOURCE_TYPE("sourceType", "请求来源"),
    LOGIN_TIME("loginTime", "登录时间"),
    ALIVE_TIME("aliveTime", "活跃时间");


    private String attribute;
    private String desc;

    ETaskAttribute(String attribute, String desc) {
        this.desc = desc;
        this.attribute = attribute;
    }

    public String getAttribute() {
        return attribute;
    }

    public String getDesc() {
        return desc;
    }
}
