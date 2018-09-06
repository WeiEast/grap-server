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
 * Created by luoyihua on 2017/4/27.
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
     *
     * @param taskid
     * @param header
     * @param cookie
     * @param url
     * @param website
     * @return
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/acquisition", method = {RequestMethod.POST})
    public Object sendToMq(@RequestParam("taskid") Long taskid, @RequestParam(value = "header", required = false) String header,
                           @RequestParam("cookie") String cookie, @RequestParam("url") String url,
                           @RequestParam("website") String website,
                           @RequestParam(value = "accountNo", required = false) String accountNo) {
        logger.info("电商-发消息：acquisition，taskid={},header={},cookie={},url={},website={},accountNo={}", taskid, header, cookie, url, website, accountNo);
        acquisitionService.acquisition(taskid, header, cookie, url, website, accountNo, ESpiderTopic.SPIDER_ECOMMERCE.name().toLowerCase());
        return new SimpleResult<>();
    }

//    @RequestMapping(value = "/acquisition/process", method = {RequestMethod.POST})
//    public Object loginProcess(@RequestParam("taskid") Long taskid,
//                               @RequestParam("directiveId") String directiveId,
//                               @RequestParam(value = "html", required = false) String html,
//                               @RequestParam(value = "cookie", required = false) String cookie) throws Exception {
//        if (StringUtils.isEmpty(html) && StringUtils.isEmpty(cookie)) {
//            throw new Exception("html和cookie不能同时为空");
//        }
//        logger.info("电商-loginProcess: taskid={},directiveId={},html={},cookie={}", taskid, directiveId, cookie, html, cookie);
//        acquisitionService.loginProcess(directiveId, taskid, html, cookie);
//        return new SimpleResult<>();
//    }

}
