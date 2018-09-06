package com.treefinance.saas.grapserver.biz.service;

import com.alibaba.fastjson.JSON;
import com.datatrees.spider.ecommerce.api.EconomicApiForTaoBaoQR;
import com.datatrees.spider.share.domain.CommonPluginParam;
import com.datatrees.spider.share.domain.http.HttpResult;
import com.treefinance.saas.grapserver.common.exception.CrawlerBizException;
import com.treefinance.saas.knife.result.SimpleResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by haojiahong on 2018/1/11.
 */
@Service
public class EcommerceLoginSimulationService {
    private static final Logger logger = LoggerFactory.getLogger(EcommerceLoginSimulationService.class);

    @Autowired
    private EconomicApiForTaoBaoQR economicApiForTaoBaoQR;
    @Autowired
    private TaskTimeService taskTimeService;

    /**
     * 获取二维码
     *
     * @param param
     * @return
     */
    public Object refreshQRCode(CommonPluginParam param) {
        HttpResult<Object> result;
        try {
            result = economicApiForTaoBaoQR.refeshQRCode(param);
        } catch (Exception e) {
            logger.error("电商:调用爬数获取二维码异常,param={}", JSON.toJSONString(param), e);
            throw e;
        }
        if (!result.getStatus()) {
            logger.info("电商:调用爬数获取二维码失败,param={},result={}",
                    JSON.toJSONString(param), JSON.toJSONString(result));
            throw new CrawlerBizException(result.getMessage());
        }
        return SimpleResult.successResult(result);
    }

    /**
     * 轮询获取二维码状态
     *
     * @param param
     * @return
     */
    public Object queryQRStatus(CommonPluginParam param) {
        HttpResult<Object> result;
        try {
            result = economicApiForTaoBaoQR.queryQRStatus(param);
        } catch (Exception e) {
            logger.error("电商:调用爬数轮询获取二维码状态异常,param={}", JSON.toJSONString(param), e);
            throw e;
        }
        if (!result.getStatus()) {
            logger.info("电商:调用爬数轮询获取二维码状态失败,param={},result={}",
                    JSON.toJSONString(param), JSON.toJSONString(result));
            throw new CrawlerBizException(result.getMessage());
        }
        if (result.getData() != null) {
            String status = String.valueOf(result.getData());
            if (StringUtils.isNotBlank(status) && StringUtils.equalsIgnoreCase("CONFIRMED", status)) {
                taskTimeService.updateLoginTime(param.getTaskId(), new Date());
            }
        }
        return SimpleResult.successResult(result);
    }
}
