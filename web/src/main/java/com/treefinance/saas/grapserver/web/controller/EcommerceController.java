package com.treefinance.saas.grapserver.web.controller;

import com.treefinance.saas.grapserver.biz.service.AcquisitionService;
import com.treefinance.saas.grapserver.common.enums.EBizType;
import com.treefinance.saas.grapserver.common.enums.ESpiderTopic;
import com.treefinance.saas.knife.result.SimpleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author luoyihua on 2017/4/27.
 */
@RestController
@RequestMapping(value = {"/ecommerce", "/grap/ecommerce"})
public class EcommerceController {

    private static final Logger logger = LoggerFactory.getLogger(EcommerceController.class);

    @Autowired
    private AcquisitionService acquisitionService;

    @RequestMapping(value = "/start", method = {RequestMethod.POST})
    public ModelAndView createTask() {
        ModelAndView modelAndView = new ModelAndView("forward:/start");
        modelAndView.addObject("bizType", EBizType.ECOMMERCE.getText());
        return modelAndView;
    }

    /**
     * 发消息到mq
     * 登陆成功之后调用
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/acquisition", method = {RequestMethod.POST})
    public Object sendToMq(@RequestParam("taskid") Long taskid, @RequestParam(value = "header", required = false) String header,
                           @RequestParam("cookie") String cookie, @RequestParam("url") String url,
                           @RequestParam("website") String website,
                           @RequestParam(value = "accountNo", required = false) String accountNo) {
        logger.info("电商-发消息：acquisition，taskid={},header={},cookie={},url={},website={},accountNo={}",
                taskid, header, cookie, url, website, accountNo);
        acquisitionService.acquisition(taskid, header, cookie, url, website, accountNo, ESpiderTopic.SPIDER_ECOMMERCE.name().toLowerCase());
        return new SimpleResult<>();
    }

}
