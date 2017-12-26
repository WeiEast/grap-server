package com.treefinance.saas.grapserver.biz.service;

import com.alibaba.fastjson.JSON;
import com.datatrees.rawdatacentral.api.RpcH5EmailService;
import com.datatrees.rawdatacentral.domain.email.EmailParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.treefinance.saas.grapserver.common.exception.CrawlerBizException;
import com.treefinance.saas.knife.result.SimpleResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 邮箱账单模拟登陆
 * Created by haojiahong on 2017/12/26.
 */
@Service
public class EmailLoginSimulationService {

    private static final Logger logger = LoggerFactory.getLogger(EmailLoginSimulationService.class);

    @Autowired
    private RpcH5EmailService rpcH5EmailService;

    public Object login(EmailParam emailParam) {
        HttpResult<Map<String, Object>> result;
        try {
            result = rpcH5EmailService.loginSubmit(emailParam);
        } catch (Exception e) {
            logger.error("邮箱账单:调用爬数邮箱账单登陆异常,emailParam={}", JSON.toJSONString(emailParam), e);
            throw e;
        }
        if (!result.getStatus()) {
            logger.info("邮箱账单:调用爬数邮箱账单登陆失败,emailParam={},result={}",
                    JSON.toJSONString(emailParam), JSON.toJSONString(result));
            if (StringUtils.isNotBlank(result.getMessage())) {
                throw new CrawlerBizException(result.getMessage());
            } else {
                throw new CrawlerBizException("登陆失败,请重试");
            }
        }
        return SimpleResult.successResult(result.getData());
    }
}
