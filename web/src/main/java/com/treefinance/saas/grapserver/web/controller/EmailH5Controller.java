package com.treefinance.saas.grapserver.web.controller;

import com.alibaba.fastjson.JSON;
import com.datatrees.rawdatacentral.domain.email.EmailParam;
import com.treefinance.saas.grapserver.biz.service.EmailLoginSimulationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by haojiahong on 2017/12/26.
 */
@RestController
@RequestMapping(value = {"/h5/email", "/grap/h5/email",})
public class EmailH5Controller {
    private static final Logger logger = LoggerFactory.getLogger(EmailH5Controller.class);

    @Autowired
    private EmailLoginSimulationService emailLoginSimulationService;

    /**
     * 登陆
     *
     * @param emailParam
     * @return
     */
    @RequestMapping(value = "/login/submit", method = {RequestMethod.POST})
    public Object login(EmailParam emailParam) {
        logger.info("邮箱账单:登陆,传入参数,emailParam={}", JSON.toJSONString(emailParam));
        if (emailParam == null || emailParam.getTaskId() == null) {
            logger.error("邮箱账单:登陆,参数缺失,taskId必传,emailParam={}", JSON.toJSONString(emailParam));
            throw new IllegalArgumentException("邮箱账单:登陆,参数缺失,taskId必传");
        }
        Object result = emailLoginSimulationService.login(emailParam);
        logger.info("邮箱账单:登陆,返回结果,emailParam={},result={}", JSON.toJSONString(emailParam), JSON.toJSONString(result));
        return result;
    }

}
