package com.treefinance.saas.grapserver.common.model.dto.moxie;

import com.treefinance.saas.grapserver.common.model.dto.DirectiveDTO;

/**
 * @author haojiahong on 2017/9/14.
 */
public class MoxieDirectiveDTO extends DirectiveDTO {

    private static final long serialVersionUID = 6201076878996673264L;

    private String moxieTaskId;

    public String getMoxieTaskId() {
        return moxieTaskId;
    }

    public void setMoxieTaskId(String moxieTaskId) {
        this.moxieTaskId = moxieTaskId;
    }

}
