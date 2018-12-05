package com.treefinance.saas.grapserver.biz.adapter;

import com.treefinance.saas.grapserver.dao.entity.AppCallbackConfig;

import java.util.List;

/**
 * @author:guoguoyun
 * @date:Created in 2018/11/26上午11:38
 */
public interface QueryAppCallbackConfigAdapter {

    List<AppCallbackConfig> queryAppCallBackConfigByAppId(String appid);

    List<AppCallbackConfig> queryAllAppCallBackConfig();

}
