package com.treefinance.saas.grapserver.biz.processor.component.operator;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.assistant.model.TaskOperatorMonitorMessage;
import com.treefinance.saas.grapserver.biz.processor.BaseBusinessComponent;
import com.treefinance.saas.grapserver.biz.processor.request.OperatorMonitorSpecialRequest;
import com.treefinance.saas.grapserver.biz.service.MonitorService;
import com.treefinance.saas.grapserver.biz.service.TaskAttributeService;
import com.treefinance.saas.grapserver.biz.service.TaskBuryPointLogService;
import com.treefinance.saas.grapserver.biz.service.TaskLogService;
import com.treefinance.saas.grapserver.common.enums.ETaskStep;
import com.treefinance.saas.grapserver.dao.entity.TaskAttribute;
import com.treefinance.saas.grapserver.dao.entity.TaskLog;
import com.treefinance.saas.grapserver.facade.enums.ETaskAttribute;
import com.treefinance.saas.grapserver.facade.model.enums.ETaskOperatorMonitorStatus;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by haojiahong on 2017/11/9.
 */
@Component("crawlSuccessMonitorComponent")
public class CrawlSuccessMonitorComponent extends BaseBusinessComponent<OperatorMonitorSpecialRequest> {
    private final static Logger logger = LoggerFactory.getLogger(CrawlSuccessMonitorComponent.class);

    @Autowired
    private TaskBuryPointLogService taskBuryPointLogService;
    @Autowired
    private TaskLogService taskLogService;
    @Autowired
    private MonitorService monitorService;
    @Autowired
    private TaskAttributeService taskAttributeService;

    @Override
    protected void doBusiness(OperatorMonitorSpecialRequest request) {
        if (request == null || request.getTask() == null) {
            logger.error("运营商监控,发送抓取成功(任务日志)消息到monitor,请求参数为空,request={}", JSON.toJSONString(request));
            return;
        }
        List<TaskLog> list = taskLogService.queryTaskLog(request.getTaskId(), ETaskStep.CRAWL_SUCCESS.getText());
        if (CollectionUtils.isEmpty(list)) {
            logger.info("运营商监控,发送抓取成功(任务日志)消息到monitor,未查询到抓取成功日志信息,request={}", JSON.toJSONString(request));
            return;
        }
        String groupCode = request.getGroupCode();
        String groupName = request.getGroupName();

        if (StringUtils.isBlank(groupCode) || StringUtils.isBlank(groupName)) {
            TaskAttribute groupCodeAttribute = taskAttributeService.findByName(request.getTaskId(), ETaskAttribute.OPERATOR_GROUP_CODE.getAttribute(), false);
            TaskAttribute groupNameAttribute = taskAttributeService.findByName(request.getTaskId(), ETaskAttribute.OPERATOR_GROUP_NAME.getAttribute(), false);

            if (groupCodeAttribute == null || groupNameAttribute == null) {
                logger.error("运营商监控,task_attribute表中未查到group_code和group_name信息,request={}", JSON.toJSONString(request));
                return;
            }
            groupCode = groupCodeAttribute.getValue();
            groupName = groupNameAttribute.getValue();

            if (StringUtils.isBlank(groupCode) || StringUtils.isBlank(groupName)) {
                logger.error("运营商监控,未取到groupCode和groupName字段信息,request={}", JSON.toJSONString(request));
                return;
            }
            request.setGroupCode(groupCode);
            request.setGroupName(groupName);
        }

        TaskOperatorMonitorMessage message = new TaskOperatorMonitorMessage();
        message.setTaskId(request.getTaskId());
        message.setAppId(request.getTask().getAppId());
        message.setGroupCode(groupCode);
        message.setGroupName(groupName);
        message.setDataTime(request.getTask().getCreateTime());
        message.setStatus(ETaskOperatorMonitorStatus.CRAWL_SUCCESS.getStatus());
        logger.info("运营商监控,发送抓取成功(任务日志)消息到monitor,request={},message={},status={}",
                JSON.toJSONString(request), JSON.toJSONString(message), ETaskOperatorMonitorStatus.CRAWL_SUCCESS);
        monitorService.sendTaskOperatorMonitorMessage(message);

    }
}