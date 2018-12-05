package com.treefinance.saas.grapserver.biz.adapter;

import com.treefinance.saas.grapserver.biz.dto.AppBizType;

import java.util.List;

/**
 * @author:guoguoyun
 * @date:Created in 2018/11/26上午11:38
 */
public interface QueryBizTypeAdapter {


     List<AppBizType> queryAppBizTypeByBizType(Byte bizType);

     List<AppBizType> queryAllAppBizType();


}
