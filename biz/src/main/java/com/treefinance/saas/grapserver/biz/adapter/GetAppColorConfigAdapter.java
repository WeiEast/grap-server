package com.treefinance.saas.grapserver.biz.adapter;

import com.treefinance.saas.merchant.facade.result.console.MerchantResult;
import com.treefinance.saas.merchant.facade.result.grapsever.AppColorConfigResult;

/**
 * @author:guoguoyun
 * @date:Created in 2018/11/26上午11:31
 */
public interface GetAppColorConfigAdapter {

     MerchantResult<AppColorConfigResult> queryAppColorConfig(String appid, String style);
}
