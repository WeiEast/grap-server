package com.treefinance.saas.grapserver.biz.service;

import com.treefinance.saas.grapserver.biz.domain.CallbackConfig;
import com.treefinance.saas.grapserver.facade.enums.EDataType;

import java.util.List;

/**
 * @author luoyihua on 2017/5/11.
 */

public interface AppCallbackConfigService {

    /**
     * 获取指定业务类型的回调配置： 如果有配置该业务类型，则使用该业务类型；没有则使用全局配置
     */
    List<CallbackConfig> queryCallbackConfigsByAppIdAndBizType(String appId, Byte bizType, EDataType dataType);
}
