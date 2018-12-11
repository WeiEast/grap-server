package com.treefinance.saas.grapserver.biz.service;

import com.treefinance.saas.grapserver.biz.domain.AppBizType;

/**
 * @author yh-treefinance on 2017/8/2.
 */
public interface AppBizTypeService {

    /**
     * 获取类型
     */
    AppBizType getAppBizType(Byte bizType);
}
