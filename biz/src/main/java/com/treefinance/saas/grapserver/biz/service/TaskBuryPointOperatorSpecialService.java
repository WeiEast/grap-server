package com.treefinance.saas.grapserver.biz.service;

import com.alibaba.fastjson.JSONObject;
import com.treefinance.saas.assistant.model.TaskOperatorMonitorMessage;
import com.treefinance.saas.grapserver.common.enums.ETaskOperatorStatus;
import com.treefinance.saas.grapserver.common.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by haojiahong on 2017/10/27.
 */
@Service
public class TaskBuryPointOperatorSpecialService {
    private final static Logger logger = LoggerFactory.getLogger(TaskBuryPointOperatorSpecialService.class);

    @Autowired
    private MonitorService monitorService;

    public void doProcess(String extra) {
        if (StringUtils.isBlank(extra)) {
            logger.error("运营商监控中,extra为空,extra={}", extra);
            return;
        }
        JSONObject jsonObject = (JSONObject) JsonUtils.toJsonObject(extra);
        String groupCode = jsonObject.getString("groupCode");
        String groupName = jsonObject.getString("groupName");
        if (StringUtils.isBlank(groupCode) || StringUtils.isBlank(groupName)) {
            logger.error("运营商监控中,传入参数有误,extra={},groupCode={},groupName={}", extra, groupCode, groupName);
            return;
        }
        TaskOperatorMonitorMessage message = new TaskOperatorMonitorMessage();
        message.setGroupCode(groupCode);
        message.setGroupName(groupName);
        message.setDataTime(new Date());
        message.setStatus(ETaskOperatorStatus.COMFIRM_MOBILE.getStatus());
        monitorService.sendTaskOperatorMonitorMessage(message);

    }
}
