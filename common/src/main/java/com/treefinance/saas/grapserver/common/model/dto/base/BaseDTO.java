package com.treefinance.saas.grapserver.common.model.dto.base;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by yh-treefinance on 2017/7/7.
 */
public class BaseDTO implements Serializable {
    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 最后更新时间
     */
    private Date lastUpdateTime;

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}
