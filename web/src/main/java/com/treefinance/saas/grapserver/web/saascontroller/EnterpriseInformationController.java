/*
 * Copyright © 2015 - 2017 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.treefinance.saas.grapserver.web.saascontroller;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.assistant.exception.FailureInSendingToMQException;
import com.treefinance.saas.grapserver.biz.service.EnterpriseInformationService;
import com.treefinance.saas.grapserver.biz.service.TaskService;
import com.treefinance.saas.grapserver.common.enums.EBizType;
import com.treefinance.saas.grapserver.common.result.SaasResult;
import com.treefinance.saas.grapserver.web.RequestChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author guimeichao
 * @date 2018/12/29
 */
@RestController
@RequestMapping(value = {"/enterprise", "/grap/enterprise"})
public class EnterpriseInformationController {

    private static final Logger logger = LoggerFactory.getLogger(EnterpriseInformationController.class);

    @Autowired
    private EnterpriseInformationService enterpriseInformationService;
    @Autowired
    private TaskService taskService;

    /**
     * 获取工商信息
     *
     * @param appid
     * @param uniqueId
     * @param extra
     * @return
     */
    @RequestMapping(value = "/process", method = {RequestMethod.POST})
    public Object process(@RequestParam String appid, @RequestParam("uniqueId") String uniqueId, @RequestParam("extra") String extra) throws
        FailureInSendingToMQException {
        logger.info("工商信息:获取工商信息,传入参数,appid={},uniqueId={},extra={}", appid, uniqueId, extra);
        RequestChecker.notEmpty("appid", appid);
        RequestChecker.notEmpty("uniqueId", uniqueId);
        RequestChecker.notEmpty("extra", extra);

        Long taskId = taskService.createTask(appid, uniqueId, EBizType.ENTERPRISE);
        return enterpriseInformationService.startCrawler(taskId, extra);
    }

    @RequestMapping(value = "/pynerStart", method = RequestMethod.POST)
    public Object createTask(@RequestParam String appid, @RequestParam("uniqueId") String uniqueId, @RequestParam("platform") String platform,
        @RequestParam("extra") String extra) {
        RequestChecker.notEmpty("appid", appid);
        RequestChecker.notEmpty("uniqueId", uniqueId);
        RequestChecker.notEmpty("platform", platform);
        RequestChecker.notEmpty("extra", extra);

        Map<String, Object> map = JSON.parseObject(extra);
        String enterpriseName = (String)map.get("business");
        boolean flag = enterpriseInformationService.isStartCrawler(enterpriseName);
        if (!flag) {
            SaasResult<Object> saasResult = (SaasResult<Object>)enterpriseInformationService.getResult(enterpriseName);
            saasResult.setCode(3);
            return saasResult;
        }

        Long taskId = taskService.createTask(appid, uniqueId, EBizType.ENTERPRISE);
        return enterpriseInformationService.startPynerCrawler(taskId, platform, extra);
    }

    @RequestMapping(value = "/getEnterpriseData", method = RequestMethod.POST)
    public Object getEnterpriseData(@RequestParam String appid, @RequestParam("uniqueId") String uniqueId, @RequestParam("saasid") Long saasid,
        @RequestParam(value = "enterpriseName") String enterpriseName) {
        RequestChecker.notEmpty("appid", appid);
        RequestChecker.notEmpty("uniqueId", uniqueId);
        RequestChecker.notNull("saasid", saasid);
        RequestChecker.notEmpty("enterpriseName", enterpriseName);

        taskService.validateTask(appid, uniqueId, EBizType.ENTERPRISE);
        return enterpriseInformationService.getEnterpriseData(appid, uniqueId, saasid, enterpriseName);
    }
}
