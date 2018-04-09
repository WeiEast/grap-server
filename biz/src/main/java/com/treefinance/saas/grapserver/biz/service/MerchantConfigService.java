package com.treefinance.saas.grapserver.biz.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.treefinance.saas.grapserver.biz.config.DiamondConfig;

import com.treefinance.saas.grapserver.common.utils.DataConverterUtils;
import com.treefinance.saas.grapserver.dao.entity.AppColorConfig;
import com.treefinance.saas.merchant.center.facade.request.grapserver.GetAppColorConfigRequest;
import com.treefinance.saas.merchant.center.facade.result.console.MerchantResult;
import com.treefinance.saas.merchant.center.facade.result.grapsever.AppColorConfigResult;
import com.treefinance.saas.merchant.center.facade.service.AppColorConfigFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by luoyihua on 2017/5/5.
 */
@Service
public class MerchantConfigService {
    private static final Logger logger = LoggerFactory.getLogger(AppColorConfigService.class);

    @Autowired
    private DiamondConfig diamondConfig;


    @Autowired
    private AppColorConfigFacade appColorConfigFacade;

    @SuppressWarnings("rawtypes")
    public Map getColorConfig(String appId, String style) {

        GetAppColorConfigRequest getAppColorConfigRequest = new GetAppColorConfigRequest();
        getAppColorConfigRequest.setAppId(appId);
        getAppColorConfigRequest.setStyle(style);
        MerchantResult<AppColorConfigResult> merchantColorConfigResult = appColorConfigFacade.queryAppColorConfig(getAppColorConfigRequest);

        Map<String, Object> map = Maps.newHashMap();
        if (merchantColorConfigResult.isSuccess()) {
            AppColorConfig merchantColorConfig = DataConverterUtils.convert(merchantColorConfigResult.getData(), AppColorConfig.class);
            map.put("main", merchantColorConfig.getMain());
            map.put("assist", merchantColorConfig.getAssist());
            map.put("assistError", merchantColorConfig.getAssistError());
            map.put("backBtnAndFontColor", merchantColorConfig.getBackBtnAndFontColor());
            map.put("background", merchantColorConfig.getBackground());
            map.put("btnDisabled", merchantColorConfig.getBtnDisabled());
            map.put("scheduleError", merchantColorConfig.getScheduleError());
        } else {
            try {
                logger.debug("{},读取默认配色appid={}，",merchantColorConfigResult.getRetMsg(), appId);
                ObjectMapper objectMapper = new ObjectMapper();
                Map cardInfoMap = objectMapper.readValue(diamondConfig.getDefaultMerchantColorConfig(), Map.class);
                map.put("main", cardInfoMap.get("main"));
                map.put("assist", cardInfoMap.get("assist"));
                map.put("assistError", cardInfoMap.get("assistError"));
                map.put("backBtnAndFontColor", cardInfoMap.get("backBtnAndFontColor"));
                map.put("background", cardInfoMap.get("background"));
                map.put("btnDisabled", cardInfoMap.get("btnDisabled"));
                map.put("scheduleError", cardInfoMap.get("scheduleError"));
            } catch (Exception ex) {
                logger.error("商户默认配色解析失败={}", ex);
            }
        }
        return map;
    }
}
