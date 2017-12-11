package com.treefinance.saas.grapserver.biz.service;

import com.alibaba.fastjson.JSON;
import com.datatrees.rawdatacentral.api.RpcEducationService;
import com.datatrees.rawdatacentral.domain.education.EducationParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.treefinance.saas.grapserver.common.exception.CrawlerBizException;
import com.treefinance.saas.knife.result.SimpleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 学信网
 * Created by haojiahong on 2017/12/11.
 */
@Service
public class ChsiLoginSimulationService {
    private static final Logger logger = LoggerFactory.getLogger(ChsiLoginSimulationService.class);

    @Autowired
    private RpcEducationService rpcEducationService;


    public Object loginInit(EducationParam educationParam) {
        HttpResult<Map<String, Object>> result;
        try {
            result = rpcEducationService.loginInit(educationParam);
        } catch (Exception e) {
            logger.error("学信网:调用爬数登陆初始化异常,param={}", JSON.toJSONString(educationParam), e);
            throw e;
        }
        if (!result.getStatus()) {
            logger.info("学信网:调用爬数登陆初始化失败,param={},result={}",
                    JSON.toJSONString(educationParam), JSON.toJSONString(result));
            throw new CrawlerBizException(result.getMessage());
        }
        return SimpleResult.successResult(result.getData());
    }

    public Object loginSubmit(EducationParam param) {
        return null;
    }
}
