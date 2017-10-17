package com.treefinance.saas.grapserver.web.controller;

import com.treefinance.saas.grapserver.biz.service.DemoService;
import com.treefinance.saas.knife.result.Results;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by haojiahong on 2017/10/17.
 */
@RestController
@RequestMapping(value = {"/grap/h5/demo"})
public class DemoController {
    private static final Logger logger = LoggerFactory.getLogger(DemoController.class);

    @Autowired
    private DemoService demoService;

    @RequestMapping(value = "/fund/data", method = {RequestMethod.POST})
    public Object createTask(@RequestParam("appid") String appId,
                             @RequestParam("params") String params,
                             HttpServletRequest request) {
        if (StringUtils.isBlank(appId) || StringUtils.isBlank(params)) {
            throw new IllegalArgumentException("Parameter is incorrect.");
        }
        Object result = demoService.getFundData(appId, params);
        return Results.newSuccessResult(result);


    }


}
