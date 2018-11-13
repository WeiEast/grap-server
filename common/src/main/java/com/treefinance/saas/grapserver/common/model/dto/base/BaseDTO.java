package com.treefinance.saas.grapserver.common.model.dto.base;

import java.io.Serializable;
import java.util.Date;

/**
 * @author yh-treefinance on 2017/7/7.
 */
public class BaseDTO implements Serializable {

    private static final long serialVersionUID = 7062953110657722204L;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 最后更新时间
     */
    private Date lastUpdateTime;

    @Override
    public String toString() {
        return "BaseDTO{" +
                "createTime=" + createTime +
                ", lastUpdateTime=" + lastUpdateTime +
                '}';
    }

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
