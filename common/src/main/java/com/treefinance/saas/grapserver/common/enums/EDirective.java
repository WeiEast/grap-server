package com.treefinance.saas.grapserver.common.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by luoyihua on 2017/5/11.
 */
public enum EDirective {
    REQUIRE_SMS("require_sms", (byte) 1),
    REQUIRE_PICTURE("require_picture", (byte) 2),
    REQUIRE_QR("require_qr", (byte) 3),
    GRAB_URL("grab_url", (byte) 3),
    CALLBACK_FAIL("callback_fail", (byte) 3),
    TASK_SUCCESS("task_success", (byte) 3),
    TASK_FAIL("task_fail", (byte) 3),
    TASK_CANCEL("task_cancel", (byte) 3);

    private Byte code;
    private String text;

    private EDirective(String text, Byte code) {
        this.code = code;
        this.text = text;
    }

    public Byte getCode() {
        return code;
    }

    public void setCode(Byte code) {
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public static Byte getCode(String text) {
        if (StringUtils.isNotEmpty(text)) {
            for (EDirective item : EDirective.values()) {
                if (text.equals(item.getText())) {
                    return item.getCode();
                }
            }
        }
        return -1;
    }

    public static EDirective directiveOf(String text) {
        if (StringUtils.isNotEmpty(text)) {
            for (EDirective item : EDirective.values()) {
                if (text.equalsIgnoreCase(item.getText())) {
                    return item;
                }
            }
        }
        return null;
    }
}
