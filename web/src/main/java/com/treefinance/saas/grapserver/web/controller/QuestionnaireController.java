package com.treefinance.saas.grapserver.web.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.treefinance.saas.grapserver.common.enums.EBizType;
import com.treefinance.saas.grapserver.common.exception.BizException;
import com.treefinance.saas.grapserver.common.model.vo.AppQuestionnaireResultRequest;
import com.treefinance.saas.knife.result.SimpleResult;
import com.treefinance.saas.merchant.center.facade.request.grapserver.GetAppQuestionnaireRequest;
import com.treefinance.saas.merchant.center.facade.request.grapserver.SaveAppQuestionnaireResultRequest;
import com.treefinance.saas.merchant.center.facade.result.console.MerchantResult;
import com.treefinance.saas.merchant.center.facade.result.grapsever.AppQuestionnaireRO;
import com.treefinance.saas.merchant.center.facade.result.grapsever.AppQuestionnaireResultDetailRO;
import com.treefinance.saas.merchant.center.facade.service.AppQuestionnaireFacade;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author yh-treefinance on 2018/6/29.
 */
@RestController
@RequestMapping(value = {"/grap", "/grap/h5/"})
public class QuestionnaireController {

    private static final Logger logger = LoggerFactory.getLogger(QuestionnaireController.class);

    @Autowired
    private AppQuestionnaireFacade appQuestionnaireFacade;

    /**
     * 查询问卷
     */
    @RequestMapping(value = "/{bizType}/questionnaire/get", method = {RequestMethod.POST})
    public Object getQuestionnaire(@PathVariable(name = "bizType") String bizType,
                                   @RequestParam("appid") String appId, GetAppQuestionnaireRequest request) {
        if (EBizType.of(bizType) == null) {
            throw new BizException("不支持的业务类型");
        }
        request.setAppId(appId);
        logger.info("getAppQuestionnaire : {}", JSON.toJSONString(request));
        MerchantResult<AppQuestionnaireRO> result = appQuestionnaireFacade.getAppQuestionnaire(request);
        return new SimpleResult<>(result.getData());
    }

    /**
     * 保存问卷结果
     */
    @RequestMapping(value = "/{bizType}/questionnaire/save", method = {RequestMethod.POST})
    public Object saveResult(@PathVariable(name = "bizType") String bizType,
                             @RequestParam("appid") String appId,
                             @RequestParam("taskid") Long taskid,
                             @RequestParam("uniqueid") String uniqueId,
                             AppQuestionnaireResultRequest request) {
        try {
            logger.info("save questionnaire result: {}", JSON.toJSONString(request));
            if (EBizType.of(bizType) == null) {
                throw new BizException("不支持的业务类型");
            }
            SaveAppQuestionnaireResultRequest resultRO = new SaveAppQuestionnaireResultRequest();
            resultRO.setAppId(appId);
            resultRO.setQuestionnaireDesc(request.getQuestionnaireDesc());
            resultRO.setQuestionnaireId(request.getQuestionnaireId());
            resultRO.setUniqueId(uniqueId);
            resultRO.setTaskId(taskid);
            if (StringUtils.isEmpty(request.getDetailIds())) {
                resultRO.setQuestionnaireDetailCount(0);
            } else {
                List<Long> detailIds = Lists.newArrayList();
                List<AppQuestionnaireResultDetailRO> detailROList = Lists.newArrayList();
                detailIds = JSON.parseArray(request.getDetailIds(), Long.class);
                detailIds.forEach(detailId -> {
                    AppQuestionnaireResultDetailRO detailRO = new AppQuestionnaireResultDetailRO();
                    detailRO.setDetailId(detailId);
                    detailROList.add(detailRO);
                });
                resultRO.setQuestionnaireDetailCount(detailIds.size());
                resultRO.setDetailROList(detailROList);
            }
            MerchantResult<Boolean> result = appQuestionnaireFacade.addAppQuestionnaireResult(resultRO);
            return new SimpleResult<>(result.getData());
        } catch (Exception e) {
            logger.error("saveResult error: request={}", JSON.toJSONString(request), e);
            throw new RuntimeException(e);
        }
    }

}
