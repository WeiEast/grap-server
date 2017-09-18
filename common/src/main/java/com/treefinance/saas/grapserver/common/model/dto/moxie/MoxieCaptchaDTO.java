package com.treefinance.saas.grapserver.common.model.dto.moxie;

import java.io.Serializable;

/**
 * Created by haojiahong on 2017/9/15.
 */
public class MoxieCaptchaDTO implements Serializable {
    private static final long serialVersionUID = -8202304211634485703L;
    private String type;
    private String value;
    private Long waitSeconds;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getWaitSeconds() {
        return waitSeconds;
    }

    public void setWaitSeconds(Long waitSeconds) {
        this.waitSeconds = waitSeconds;
    }
}
