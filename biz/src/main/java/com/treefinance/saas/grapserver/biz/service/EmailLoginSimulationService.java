package com.treefinance.saas.grapserver.biz.service;

import com.alibaba.fastjson.JSON;
import com.datatrees.rawdatacentral.api.CommonPluginApi;
import com.datatrees.rawdatacentral.api.mail.qq.MailServiceApiForQQ;
import com.datatrees.rawdatacentral.domain.plugin.CommonPluginParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.domain.result.ProcessResult;
import com.treefinance.proxy.api.ProxyProvider;
import com.treefinance.saas.grapserver.common.exception.CrawlerBizException;
import com.treefinance.saas.knife.result.Results;
import com.treefinance.saas.knife.result.SimpleResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 邮箱账单模拟登陆
 * Created by haojiahong on 2017/12/26.
 */
@Service
public class EmailLoginSimulationService {

    private static final Logger logger = LoggerFactory.getLogger(EmailLoginSimulationService.class);

    @Autowired
    private MailServiceApiForQQ mailServiceApiForQQ;
    @Autowired
    private CommonPluginApi commonPluginApi;
    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskTimeService taskTimeService;
    @Autowired
    private ProxyProvider proxyProvider;

    /**
     * 登陆(异步)
     *
     * @param param
     * @return
     */
    public Object login(CommonPluginParam param) {
        HttpResult<Object> result;
        try {
            result = mailServiceApiForQQ.login(param);
        } catch (Exception e) {
            logger.error("邮箱账单:调用爬数邮箱账单登陆异常,param={}", JSON.toJSONString(param), e);
            throw e;
        }
        if (!result.getStatus()) {
            logger.info("邮箱账单:调用爬数邮箱账单登陆失败,param={},result={}",
                    JSON.toJSONString(param), JSON.toJSONString(result));
            if (StringUtils.isNotBlank(result.getMessage())) {
                throw new CrawlerBizException(result.getMessage());
            } else {
                throw new CrawlerBizException("登陆失败,请重试");
            }
        }
        if (StringUtils.isNotEmpty(param.getUsername())) {
            taskService.setAccountNo(param.getTaskId(), param.getUsername());
        }
        //// TODO: 2018/1/9 暂时写死qq.com
        taskService.updateWebSite(param.getTaskId(), "qq.com");
        taskTimeService.updateLoginTime(param.getTaskId(), new Date());
        return SimpleResult.successResult(result);
    }

    /**
     * 刷新二维码(异步)
     *
     * @param param
     * @return
     */
    public Object refreshQRCode(CommonPluginParam param) {
        HttpResult<Object> result;
        try {
            result = mailServiceApiForQQ.refeshQRCode(param);
        } catch (Exception e) {
            logger.error("邮箱账单:调用爬数邮箱账单刷新二维码异常,param={}", JSON.toJSONString(param), e);
            throw e;
        }
        if (!result.getStatus()) {
            logger.info("邮箱账单:调用爬数邮箱账单刷新二维码失败,param={},result={}",
                    JSON.toJSONString(param), JSON.toJSONString(result));
            if (StringUtils.isNotBlank(result.getMessage())) {
                throw new CrawlerBizException(result.getMessage());
            }
        }
        return SimpleResult.successResult(result);
    }

    /**
     * 查询二维码状态
     *
     * @param param
     * @return
     */
    public Object queryQRStatus(CommonPluginParam param) {
        HttpResult<Object> result;
        try {
            result = mailServiceApiForQQ.queryQRStatus(param);
        } catch (Exception e) {
            logger.error("邮箱账单:调用爬数邮箱账单查询二维码状态异常,param={}", JSON.toJSONString(param), e);
            throw e;
        }
        if (!result.getStatus()) {
            logger.info("邮箱账单:调用爬数邮箱账单查询二维码状态失败,param={},result={}",
                    JSON.toJSONString(param), JSON.toJSONString(result));
            if (StringUtils.isNotBlank(result.getMessage())) {
                throw new CrawlerBizException(result.getMessage());
            }
        }
        if (result.getData() != null && StringUtils.equals("CONFIRMED", String.valueOf(result.getData()))) {
            taskTimeService.updateLoginTime(param.getTaskId(), new Date());
        }
        return SimpleResult.successResult(result);
    }

    /**
     * 轮询处理状态(通用接口)
     *
     * @param processId
     * @param taskId
     * @return
     */
    public Object processStatus(Long processId, Long taskId) {
        ProcessResult result;
        try {
            result = commonPluginApi.queryProcessResult(processId);
        } catch (Exception e) {
            logger.error("邮箱账单:调用爬数邮箱账单轮询处理状态异常,processId={},taskId={}", processId, taskId, e);
            throw e;
        }
        return SimpleResult.successResult(result);
    }


    /**
     * 是否支持当前ip的省份代理(通用接口)
     *
     * @param param
     * @return
     */
    public Object supportProvinceProxy(CommonPluginParam param) {
        Boolean flag = false;
        try {
            flag = proxyProvider.supportProvinceName(param.getUserIp(), null);
        } catch (Exception e) {
            logger.error("邮箱账单:调用爬数邮箱账单查询是否支持当前IP的省份代理异常,param={}", JSON.toJSONString(param), e);
            throw e;
        }
        return Results.newSuccessResult(flag);
    }

}
