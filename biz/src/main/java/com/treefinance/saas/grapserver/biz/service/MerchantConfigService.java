package com.treefinance.saas.grapserver.biz.service;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.treefinance.saas.grapserver.biz.config.DiamondConfig;
import com.treefinance.saas.grapserver.common.exception.BizException;
import com.treefinance.saas.grapserver.common.model.vo.task.AppH5TipsVO;
import com.treefinance.saas.grapserver.common.utils.DataConverterUtils;
import com.treefinance.saas.grapserver.dao.entity.AppColorConfig;
import com.treefinance.saas.merchant.center.facade.request.grapserver.GetAppColorConfigRequest;
import com.treefinance.saas.merchant.center.facade.request.grapserver.GetAppH5TipsRequest;
import com.treefinance.saas.merchant.center.facade.result.console.AppH5TipsResult;
import com.treefinance.saas.merchant.center.facade.result.console.MerchantResult;
import com.treefinance.saas.merchant.center.facade.result.grapsever.AppColorConfigResult;
import com.treefinance.saas.merchant.center.facade.service.AppColorConfigFacade;
import com.treefinance.saas.merchant.center.facade.service.AppH5TipsFacade;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author by luoyihua on 2017/5/5.
 */
@Service
public class MerchantConfigService {

    private static final Logger logger = LoggerFactory.getLogger(MerchantConfigService.class);

    @Autowired
    private DiamondConfig diamondConfig;
    @Autowired
    private AppColorConfigFacade appColorConfigFacade;
    @Autowired
    private AppH5TipsFacade appH5TipsFacade;

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
                logger.debug("{},读取默认配色appid={}，", merchantColorConfigResult.getRetMsg(), appId);
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

    public Object getTips(String appId, Byte bizType) {
        GetAppH5TipsRequest rpcRequest = new GetAppH5TipsRequest();
        rpcRequest.setAppId(appId);
        rpcRequest.setBizType(bizType);
        MerchantResult<List<AppH5TipsResult>> rpcResult;
        try {
            rpcResult = appH5TipsFacade.queryAppH5Tips(rpcRequest);
        } catch (Exception e) {
            logger.error("获取商户消息提示配置:调用商户中心获取消息提示配置异常,request={}", JSON.toJSONString(rpcRequest), e);
            throw e;
        }
        if (!rpcResult.isSuccess()) {
            logger.info("获取商户消息提示配置:调用商户中心获取消息提示配置失败,request={},result={}",
                    JSON.toJSONString(rpcRequest), JSON.toJSONString(rpcResult));
            throw new BizException(rpcResult.getRetMsg(), rpcResult.getRetCode());
        }
        List<AppH5TipsResult> rpcData = rpcResult.getData();
        if (CollectionUtils.isEmpty(rpcData)) {
            return null;
        }
        AppH5TipsVO result = new AppH5TipsVO();
        Map<String, List<AppH5TipsResult>> map = rpcData.stream().collect(Collectors.groupingBy(data -> data.getTipsType().toString()));
        //如果包含弹框提示,则只显示弹框提示
        if (CollectionUtils.isNotEmpty(map.get("1"))) {
            List<AppH5TipsResult> dialogList = map.get("1");
            result.setTipsType((byte) 1);
            List<String> contentList = dialogList.stream().map(AppH5TipsResult::getTipsContent).collect(Collectors.toList());
            String content = Joiner.on(" ").join(contentList);
            result.setTipsContent(content);
            return result;
        } else {
            List<AppH5TipsResult> scrollList = map.get("0");
            result.setTipsType((byte) 0);
            List<String> contentList = scrollList.stream().map(AppH5TipsResult::getTipsContent).collect(Collectors.toList());
            String content = Joiner.on(" ").join(contentList);
            result.setTipsContent(content);
            return result;
        }
    }
}
