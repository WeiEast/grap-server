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

    /**
     * 运营商确认手机号人数,开始登陆人数
     *
     * @param extra  前端传入的特殊处理json字符串
     * @param taskId
     * @param appId
     * @param code
     */
    public void doProcess(String extra, Long taskId, String appId, String code) {
        if (!StringUtils.equalsIgnoreCase(code, "300502")
                && !StringUtils.equalsIgnoreCase(code, "300701")) {
            return;
        }
        if (StringUtils.isBlank(extra)) {
            logger.error("运营商监控中,extra为空,extra={},code={},taskId={},appId={}", extra, code, taskId, appId);
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
        message.setTaskId(taskId);
        message.setAppId(appId);
        message.setGroupCode(groupCode);
        message.setGroupName(groupName);
        message.setDataTime(new Date());
        if (StringUtils.equalsIgnoreCase(code, "300502")) {
            message.setStatus(ETaskOperatorStatus.COMFIRM_MOBILE.getStatus());
        }
        if (StringUtils.equalsIgnoreCase(code, "300701")) {
            message.setStatus(ETaskOperatorStatus.LOGIN.getStatus());
        }
        monitorService.sendTaskOperatorMonitorMessage(message);

    }
}
