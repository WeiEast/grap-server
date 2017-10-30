package com.treefinance.saas.grapserver.biz.service;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.assistant.model.TaskOperatorMonitorMessage;
import com.treefinance.saas.grapserver.common.enums.ETaskOperatorStatus;
import com.treefinance.saas.grapserver.common.enums.ETaskStep;
import com.treefinance.saas.grapserver.common.model.dto.TaskDTO;
import com.treefinance.saas.grapserver.dao.entity.TaskAttribute;
import com.treefinance.saas.grapserver.dao.entity.TaskAttributeCriteria;
import com.treefinance.saas.grapserver.dao.entity.TaskLog;
import com.treefinance.saas.grapserver.dao.entity.TaskLogCriteria;
import com.treefinance.saas.grapserver.dao.mapper.TaskAttributeMapper;
import com.treefinance.saas.grapserver.dao.mapper.TaskLogMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by haojiahong on 2017/10/30.
 */
@Service
public class TaskLogOperatorSpecialService {

    private final static Logger logger = LoggerFactory.getLogger(TaskLogOperatorSpecialService.class);

    @Autowired
    private MonitorService monitorService;
    @Autowired
    private TaskLogMapper taskLogMapper;
    @Autowired
    private TaskAttributeMapper taskAttributeMapper;

    /**
     * 运营商监控,抓取失败人数，洗数失败人数
     *
     * @param taskDTO
     * @param msg         taskLog消息msg
     * @param processTime
     */
    public void doProcess(TaskDTO taskDTO, String msg, Date processTime) {
        //收到的msg不是抓取失败或者洗数失败,不处理.
        if (!StringUtils.equalsIgnoreCase(msg, ETaskStep.CRAWL_FAIL.getText())
                && !StringUtils.equalsIgnoreCase(msg, ETaskStep.DATA_PREPROCESSED_FAIL.getText())
                && !StringUtils.equalsIgnoreCase(msg, ETaskStep.DATA_PROCESS_FAIL.getText())
                && !StringUtils.equalsIgnoreCase(msg, ETaskStep.DATA_SAVE_FAIL.getText())) {
            return;
        }
        TaskLogCriteria taskLogCriteria = new TaskLogCriteria();
        taskLogCriteria.createCriteria().andTaskIdEqualTo(taskDTO.getId()).andMsgEqualTo(msg);
        List<TaskLog> taskLogList = taskLogMapper.selectByExample(taskLogCriteria);
        if (CollectionUtils.isEmpty(taskLogList)) {
            logger.error("运营商监控,收到消息,未查询到相关任务日志,taskDTO={},msg={},processTime={}",
                    JSON.toJSONString(taskDTO), msg, processTime);
            return;
        }
        if (taskLogList.size() > 1) {
            logger.info("运营商监控,收到taskLog重复消息,重复消息不处理,taskDTO={},msg={},processTime={}",
                    JSON.toJSONString(taskDTO), msg, processTime);
            return;
        }

        TaskAttributeCriteria taskAttributeCriteria = new TaskAttributeCriteria();
        taskAttributeCriteria.createCriteria().andTaskIdEqualTo(taskDTO.getId());
        List<TaskAttribute> taskAttributeList = taskAttributeMapper.selectByExample(taskAttributeCriteria);
        if (CollectionUtils.isEmpty(taskAttributeList)) {
            logger.error("运营商监控,task_attribute表中未查到group_code和group_name信息,taskDTO={},msg={},processTime={}",
                    JSON.toJSONString(taskDTO), msg, processTime);
            return;
        }
        String groupCode = null;
        String groupName = null;
        for (TaskAttribute taskAttribute : taskAttributeList) {
            if (StringUtils.equalsIgnoreCase(taskAttribute.getName(), "group_code")) {
                groupCode = taskAttribute.getValue();
            }
            if (StringUtils.equalsIgnoreCase(taskAttribute.getName(), "group_name")) {
                groupName = taskAttribute.getName();
            }
        }
        if (StringUtils.isBlank(groupCode) || StringUtils.isBlank(groupName)) {
            logger.error("运营商监控,未取到groupCode和groupName字段信息,taskDTO={},msg={},processTime={}",
                    JSON.toJSONString(taskDTO), msg, processTime);
            return;
        }
        TaskOperatorMonitorMessage message = new TaskOperatorMonitorMessage();
        message.setTaskId(taskDTO.getId());
        message.setAppId(taskDTO.getAppId());
        message.setGroupCode(groupCode);
        message.setGroupName(groupName);
        message.setDataTime(new Date());
        if (StringUtils.equalsIgnoreCase(msg, ETaskStep.CRAWL_FAIL.getText())) {
            message.setStatus(ETaskOperatorStatus.CRAWL_FAIL.getStatus());
        }
        if (StringUtils.equalsIgnoreCase(msg, ETaskStep.DATA_PREPROCESSED_FAIL.getText())
                || StringUtils.equalsIgnoreCase(msg, ETaskStep.DATA_PROCESS_FAIL.getText())
                || StringUtils.equalsIgnoreCase(msg, ETaskStep.DATA_SAVE_FAIL.getText())) {
            message.setStatus(ETaskOperatorStatus.PROCESS_FAIL.getStatus());
        }
        monitorService.sendTaskOperatorMonitorMessage(message);

    }
}
