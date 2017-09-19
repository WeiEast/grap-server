package com.treefinance.saas.grapserver.common.enums.moxie;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by haojiahong on 2017/9/15.
 */
public enum EMoxieDirective {

    LOGIN_SUCCESS("login_success"),
    LOGIN_FAIL("login_fail"),
    TASK_SUCCESS("task_success"),
    TASK_FAIL("task_fail"),
    TASK_CANCEL("task_cancel");

    private String text;

    EMoxieDirective(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public static EMoxieDirective directiveOf(String text) {
        if (StringUtils.isNotEmpty(text)) {
            for (EMoxieDirective item : EMoxieDirective.values()) {
                if (text.equalsIgnoreCase(item.getText())) {
                    return item;
                }
            }
        }
        return null;
    }
}
