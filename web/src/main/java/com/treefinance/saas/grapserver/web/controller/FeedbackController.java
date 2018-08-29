package com.treefinance.saas.grapserver.web.controller;

import com.treefinance.saas.grapserver.biz.service.AppFeedbackService;
import com.treefinance.saas.grapserver.common.enums.EBizType;
import com.treefinance.saas.grapserver.common.exception.BizException;
import com.treefinance.saas.grapserver.common.model.dto.AppFeedbackResultDTO;
import com.treefinance.saas.knife.result.SimpleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 意见反馈
 *
 * @author haojiahong
 * @date 2018/8/29
 */
@RestController
@RequestMapping(value = {"/grap/feedback", "/grap/h5/feedback"})
public class FeedbackController {

    private static final Logger logger = LoggerFactory.getLogger(FeedbackController.class);


    @Autowired
    private AppFeedbackService appFeedbackService;

    @RequestMapping(value = "/save", method = {RequestMethod.POST})
    public Object saveResult(@RequestParam("bizType") String bizType,
                             @RequestParam("appid") String appId,
                             @RequestParam("taskid") Long taskId,
                             @RequestParam(value = "uniqueid", required = false) String uniqueId,
                             @RequestParam("feedbackValue") String feedbackValue) {
        logger.info("意见反馈输入参数:bizType={},appid={},taskid={},uniqueid={},feedbackValue={}",
                bizType, appId, taskId, uniqueId, feedbackValue);
        EBizType biz = EBizType.of(bizType);
        if (biz == null) {
            throw new BizException("不支持的业务类型");
        }
        AppFeedbackResultDTO appFeedbackResultDTO = new AppFeedbackResultDTO();
        appFeedbackResultDTO.setBizType(biz.getCode());
        appFeedbackResultDTO.setAppId(appId);
        appFeedbackResultDTO.setTaskId(taskId);
        appFeedbackResultDTO.setUniqueId(uniqueId);
        appFeedbackResultDTO.setFeedbackDesc(feedbackValue);
        appFeedbackService.save(appFeedbackResultDTO);
        return SimpleResult.successResult(true);

    }

}
