package com.treefinance.saas.grapserver.common.enums.moxie;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by haojiahong on 2017/9/15.
 */
public enum EMoxieDirective {

    LOGIN_SUCCESS("login_success", (byte) 1),
    LOGIN_FAIL("login_fail", (byte) 1),
    TASK_SUCCESS("task_success", (byte) 2),
    TASK_FAIL("task_fail", (byte) 2),
    TASK_CANCEL("task_cancel", (byte) 2);


    private String text;
    private Byte stepCode;

    EMoxieDirective(String text, Byte stepCode) {
        this.text = text;
        this.stepCode = stepCode;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Byte getStepCode() {
        return stepCode;
    }

    public void setStepCode(Byte stepCode) {
        this.stepCode = stepCode;
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
