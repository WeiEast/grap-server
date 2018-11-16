package com.treefinance.saas.grapserver.biz.common;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.grapserver.common.utils.DataConverterUtils;
import com.treefinance.saas.grapserver.dao.entity.AppBizType;
import com.treefinance.saas.merchant.center.facade.request.grapserver.GetAppBizTypeRequest;
import com.treefinance.saas.merchant.center.facade.result.console.AppBizTypeResult;
import com.treefinance.saas.merchant.center.facade.result.console.MerchantResult;
import com.treefinance.saas.merchant.center.facade.service.AppBizTypeFacade;

/**
 * @author guoguoyun
 * @date Created in 2018/11/15下午2:52
 */
@Component
public class QueryBizTypeConverter {

    private static final Logger logger = LoggerFactory.getLogger(QueryBizTypeConverter.class);

    @Autowired
    private static AppBizTypeFacade appBizTypeFacade;

    public static List<AppBizType> queryAppBizTypeByBizType(Byte bizType) {
        GetAppBizTypeRequest getAppBizTypeRequest = new GetAppBizTypeRequest();
        getAppBizTypeRequest.setBizType(bizType);
        MerchantResult<List<AppBizTypeResult>> merchantResult =
            appBizTypeFacade.queryAppBizTypeByBizType(getAppBizTypeRequest);
        List<AppBizType> list = DataConverterUtils.convert(merchantResult.getData(), AppBizType.class);
        if (merchantResult.isSuccess()) {
            logger.info("load local cache of appbiztype : appid={},data={}", bizType, JSON.toJSONString(list));

        } else {
            logger.info("load local cache of appbiztype false：error message {}", merchantResult.getRetMsg());
        }
        return list;
    }

}
