package com.treefinance.saas.grapserver.biz.service;

import com.alibaba.fastjson.JSON;
import com.datatrees.rawdatacentral.api.economic.taobao.EconomicApiForTaoBaoQR;
import com.datatrees.rawdatacentral.domain.plugin.CommonPluginParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.treefinance.saas.grapserver.common.exception.CrawlerBizException;
import com.treefinance.saas.knife.result.SimpleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by haojiahong on 2018/1/11.
 */
@Service
public class EcommerceLoginSimulationService {
    private static final Logger logger = LoggerFactory.getLogger(EcommerceLoginSimulationService.class);

    @Autowired
    private EconomicApiForTaoBaoQR economicApiForTaoBaoQR;

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
        //// TODO: 2018/1/11 如果是登录成功的状态码,需记录登录时间设置超时
        return SimpleResult.successResult(result);
    }
}
