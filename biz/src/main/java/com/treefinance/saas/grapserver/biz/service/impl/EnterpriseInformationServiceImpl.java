package com.treefinance.saas.grapserver.biz.service.impl;

import com.treefinance.saas.grapserver.biz.service.AcquisitionService;
import com.treefinance.saas.grapserver.biz.service.EnterpriseInformationService;
import com.treefinance.saas.grapserver.biz.service.TaskLicenseService;
import com.treefinance.saas.grapserver.biz.service.TaskService;
import com.treefinance.saas.grapserver.common.enums.EBizType;
import com.treefinance.saas.grapserver.common.enums.ESpiderTopic;
import com.treefinance.saas.grapserver.common.result.SaasResult;
import com.treefinance.saas.grapserver.context.component.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author guimeichao
 * @date 2018/12/29
 */
@Service("enterpriseInformationService")
public class EnterpriseInformationServiceImpl extends AbstractService implements EnterpriseInformationService {

    @Autowired
    private TaskLicenseService taskLicenseService;

    @Autowired
    private TaskService        taskService;

    @Autowired
    private AcquisitionService acquisitionService;

    @Override
    public Long creatTask(String appId, String uniqueId) {
        taskLicenseService.verifyCreateTask(appId, uniqueId, EBizType.ENTERPRISE);
        return taskService.createTask(uniqueId, appId, EBizType.ENTERPRISE.getCode(), null, null, null);
    }

    @Override
    public Object startCrawler(Long taskid, String extra) {
        logger.info("工商信息-发消息：acquisition，taskid={},extra={}", taskid, extra);
        acquisitionService.acquisition(taskid, null, null, null, "qichacha.com_gfd", null, ESpiderTopic.SPIDER_EXTRA.name().toLowerCase(), extra);
        return SaasResult.successResult(taskid);
    }

}
