package com.treefinance.saas.grapserver.biz.service.impl;

import com.alibaba.dubbo.rpc.RpcException;
import com.google.gson.reflect.TypeToken;
import com.treefinance.saas.grapserver.biz.service.AcquisitionService;
import com.treefinance.saas.grapserver.biz.service.WebDetectService;
import com.treefinance.saas.grapserver.common.enums.ESpiderTopic;
import com.treefinance.saas.grapserver.common.enums.ETaskStatus;
import com.treefinance.saas.grapserver.common.result.SaasResult;
import com.treefinance.saas.grapserver.context.component.AbstractService;
import com.treefinance.saas.grapserver.context.config.DiamondConfig;
import com.treefinance.saas.grapserver.manager.TaskManager;
import com.treefinance.saas.grapserver.manager.domain.TaskBO;
import com.treefinance.saas.processor.thirdparty.facade.opiniondetect.OpinionDetectService;
import com.treefinance.saas.processor.thirdparty.facade.opiniondetect.model.OpinionDetectDataResult;
import com.treefinance.saas.processor.thirdparty.facade.opiniondetect.model.OpinionDetectResultQuery;
import com.treefinance.toolkit.util.json.GsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author guimeichao
 * @date 2018/12/12
 */
@Service("opinionService")
public class WebDetectServiceImpl extends AbstractService implements WebDetectService {

    private static final Logger logger = LoggerFactory.getLogger(OpinionDetectService.class);

    @Autowired
    private AcquisitionService acquisitionService;

    @Autowired
    private OpinionDetectService opinionDetectService;

    @Autowired
    private DiamondConfig config;

    @Autowired
    private TaskManager taskManager;

    @Override
    public Object startCrawler(Long taskid, String platform, String extra) {
        Map platToWebsite = GsonUtils.fromJson(config.getOpinionDetectPlatformToWebsite(), new TypeToken<Map>() {}.getType());
        String website = (String)platToWebsite.get(platform);
        if (StringUtils.isBlank(website)) {
            return SaasResult.failResult("当前平台不支持!");
        }
        logger.info("爬取任务-发消息：acquisition，taskid={},extra={}", taskid, extra);
        acquisitionService.acquisition(taskid, null, null, null, website, null, ESpiderTopic.SPIDER_EXTRA.name().toLowerCase(), extra);
        return SaasResult.successResult(taskid);
    }

    @Override
    public Object getData(String appId, String uniqueId, Long taskid, Integer size, Long start, String platform, String entryname, String keyword) {
        TaskBO task = taskManager.getTaskById(taskid);
        if (ETaskStatus.RUNNING.getStatus().equals(task.getStatus())) {
            return SaasResult.failResult(null, "任务还在进行中...", 1);
        }
        // if (ETaskStatus.FAIL.getStatus().equals(task.getStatus()) ||
        // ETaskStatus.CANCEL.getStatus().equals(task.getStatus())) {
        // return SaasResult.failResult(null, "任务失败或取消", -2);
        // }
        // if (ETaskStatus.SUCCESS.getStatus().equals(task.getStatus())) {
        OpinionDetectResultQuery query = new OpinionDetectResultQuery();
        query.setSize(size);
        query.setStart(start);
        query.setPlatform(platform);
        query.setEntryName(entryname);
        query.setKeyword(keyword);
        try {
            OpinionDetectDataResult result = opinionDetectService.queryOpinionDetectData(query);
            return SaasResult.successResult(result);
        } catch (RpcException e) {
            logger.error("调用dubbo服务失败", e);
        }
        // }
        return SaasResult.failResult("获取数据失败");
    }

}
