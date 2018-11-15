package com.treefinance.saas.grapserver.biz.service;

import com.treefinance.saas.grapserver.common.exception.BizException;
import com.treefinance.saas.grapserver.common.model.dto.AppFeedbackResultDTO;
import com.treefinance.saas.grapserver.common.utils.DataConverterUtils;
import com.treefinance.saas.merchant.center.facade.request.grapserver.SaveAppFeedbackRequest;
import com.treefinance.saas.merchant.center.facade.service.AppFeedbackFacade;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 意见反馈
 *
 * @author haojiahong
 * @date 2018/8/29
 */
@Service
public class AppFeedbackService {

    @Autowired
    private AppFeedbackFacade appFeedbackFacade;

    /**
     * 添加意见反馈
     */
    public void save(AppFeedbackResultDTO appFeedbackResultDTO) {
        if (appFeedbackResultDTO == null) {
            throw new BizException("请求参数不能为空");
        }
        if (appFeedbackResultDTO.getTaskId() == null) {
            throw new BizException("taskId不能为空");
        }
        if (StringUtils.isBlank(appFeedbackResultDTO.getAppId())) {
            throw new BizException("appId不能为空");
        }
        if (appFeedbackResultDTO.getBizType() == null) {
            throw new BizException("bizType不能为空");
        }
        SaveAppFeedbackRequest request = DataConverterUtils.convert(appFeedbackResultDTO, SaveAppFeedbackRequest.class);
        appFeedbackFacade.addAppFeedbackResult(request);
    }

}
