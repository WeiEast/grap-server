package com.treefinance.saas.grapserver.biz.service;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.treefinance.saas.grapserver.context.config.DiamondConfig;
import com.treefinance.saas.grapserver.common.exception.BizException;
import com.treefinance.saas.grapserver.common.model.vo.task.AppH5TipsVO;
import com.treefinance.saas.grapserver.manager.ColorConfigManager;
import com.treefinance.saas.grapserver.manager.domain.ColorConfigBO;
import com.treefinance.saas.grapserver.exception.IllegalBizDataException;
import com.treefinance.saas.merchant.facade.request.grapserver.GetAppH5TipsRequest;
import com.treefinance.saas.merchant.facade.result.console.AppH5TipsResult;
import com.treefinance.saas.merchant.facade.result.console.MerchantResult;
import com.treefinance.saas.merchant.facade.service.AppH5TipsFacade;
import com.treefinance.toolkit.util.json.Jackson;
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
    private ColorConfigManager colorConfigManager;
    @Autowired
    private AppH5TipsFacade appH5TipsFacade;

    public Map<String, String> getColorConfig(String appId, String style) {
        ColorConfigBO config = colorConfigManager.queryAppColorConfig(appId, style);
        if (config == null) {
            logger.debug("读取默认配色信息，appId={}，", appId);
            config = getDefaultColorConfig();
        }

        if (config == null) {
            throw new IllegalBizDataException("Can not find any app's color config!");
        }

        Map<String, String> map = Maps.newHashMap();
        map.put("main", config.getMain());
        map.put("assist", config.getAssist());
        map.put("assistError", config.getAssistError());
        map.put("backBtnAndFontColor", config.getBackBtnAndFontColor());
        map.put("background", config.getBackground());
        map.put("btnDisabled", config.getBtnDisabled());
        map.put("scheduleError", config.getScheduleError());

        return map;
    }

    private ColorConfigBO getDefaultColorConfig() {
        Map<String, String> defaultSetting = Jackson.parseMap(diamondConfig.getDefaultMerchantColorConfig(), String.class, String.class);

        ColorConfigBO defaultConfig = new ColorConfigBO();
        defaultConfig.setStyle("default");
        defaultConfig.setMain(defaultSetting.get("main"));
        defaultConfig.setAssist(defaultSetting.get("assist"));
        defaultConfig.setAssistError(defaultSetting.get("assistError"));
        defaultConfig.setBackBtnAndFontColor(defaultSetting.get("backBtnAndFontColor"));
        defaultConfig.setBackground(defaultSetting.get("background"));
        defaultConfig.setBtnDisabled(defaultSetting.get("btnDisabled"));
        defaultConfig.setScheduleError(defaultSetting.get("scheduleError"));

        return defaultConfig;
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
            logger.info("获取商户消息提示配置:调用商户中心获取消息提示配置失败,request={},result={}", JSON.toJSONString(rpcRequest),
                JSON.toJSONString(rpcResult));
            throw new BizException(rpcResult.getRetMsg(), rpcResult.getRetCode());
        }
        List<AppH5TipsResult> rpcData = rpcResult.getData();
        if (CollectionUtils.isEmpty(rpcData)) {
            return null;
        }
        AppH5TipsVO result = new AppH5TipsVO();
        Map<String, List<AppH5TipsResult>> map =
            rpcData.stream().collect(Collectors.groupingBy(data -> data.getTipsType().toString()));
        // 如果包含弹框提示,则只显示弹框提示
        if (CollectionUtils.isNotEmpty(map.get("1"))) {
            List<AppH5TipsResult> dialogList = map.get("1");
            result.setTipsType((byte)1);
            List<String> contentList = dialogList.stream().map(AppH5TipsResult::getTipsContent).collect(Collectors.toList());
            String content = Joiner.on(" ").join(contentList);
            result.setTipsContent(content);
            return result;
        } else {
            List<AppH5TipsResult> scrollList = map.get("0");
            result.setTipsType((byte)0);
            List<String> contentList = scrollList.stream().map(AppH5TipsResult::getTipsContent).collect(Collectors.toList());
            String content = Joiner.on(" ").join(contentList);
            result.setTipsContent(content);
            return result;
        }
    }
}
