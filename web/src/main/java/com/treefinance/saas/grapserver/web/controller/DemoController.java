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

/**
 * @author haojiahong on 2017/10/17.
 */
@RestController
@RequestMapping(value = {"/grap/h5/demo"})
public class DemoController {

    @Autowired
    private DemoService demoService;

    @RequestMapping(value = "/fund/user_info", method = {RequestMethod.POST})
    public Object getFundUserInfo(@RequestParam("appid") String appId,
                                  @RequestParam("demoParams") String params) {
        if (StringUtils.isBlank(appId) || StringUtils.isBlank(params)) {
            throw new IllegalArgumentException("Parameter is incorrect.");
        }
        Object result = demoService.getFundUserInfo(appId, params);
        return Results.newSuccessResult(result);
    }

    @RequestMapping(value = "/fund/bill_record/list", method = {RequestMethod.POST})
    public Object getFundBillRecordList(@RequestParam("appid") String appId,
                                        @RequestParam("demoParams") String params,
                                        @RequestParam("pageNum") Integer pageNum) {
        if (StringUtils.isBlank(appId) || StringUtils.isBlank(params) || pageNum == null || pageNum < 0) {
            throw new IllegalArgumentException("Parameter is incorrect.");
        }
        Object result = demoService.getFundBillRecordList(appId, params, pageNum);
        return Results.newSuccessResult(result);
    }

    @RequestMapping(value = "/fund/loan_info/list", method = {RequestMethod.POST})
    public Object getFundLoanInfoList(@RequestParam("appid") String appId,
                                      @RequestParam("demoParams") String params,
                                      @RequestParam("pageNum") Integer pageNum) {
        if (StringUtils.isBlank(appId) || StringUtils.isBlank(params) || pageNum == null || pageNum < 0) {
            throw new IllegalArgumentException("Parameter is incorrect.");
        }
        Object result = demoService.getFundLoanInfoList(appId, params, pageNum);
        return Results.newSuccessResult(result);
    }

    @RequestMapping(value = "/fund/loan_repay_record/list", method = {RequestMethod.POST})
    public Object getFundLoanRepayRecordList(@RequestParam("appid") String appId,
                                             @RequestParam("demoParams") String params,
                                             @RequestParam("pageNum") Integer pageNum) {
        if (StringUtils.isBlank(appId) || StringUtils.isBlank(params) || pageNum == null || pageNum < 0) {
            throw new IllegalArgumentException("Parameter is incorrect.");
        }
        Object result = demoService.getFundLoanRepayRecordList(appId, params, pageNum);
        return Results.newSuccessResult(result);
    }

}
