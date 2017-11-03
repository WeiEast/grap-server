package com.treefinance.saas.grapserver.biz.processor.burypoint.operator;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.treefinance.saas.assistant.model.TaskOperatorMonitorMessage;
import com.treefinance.saas.grapserver.biz.processor.BaseBusinessComponent;
import com.treefinance.saas.grapserver.biz.service.MonitorService;
import com.treefinance.saas.grapserver.common.utils.JsonUtils;
import com.treefinance.saas.grapserver.facade.model.enums.ETaskOperatorMonitorStatus;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 确认手机号人数监控
 *
 * @author haojiahong
 * @date 2017/11/2
 */
@Component("confirmMobileSpecialComponent")
public class ConfirmMobileSpecialComponent extends BaseBusinessComponent<OperatorBuryPointSpecialRequest> {
    private final static Logger logger = LoggerFactory.getLogger(ConfirmMobileSpecialComponent.class);

    @Autowired
    private MonitorService monitorService;

    @Override
    protected void doBusiness(OperatorBuryPointSpecialRequest request) {
        if (request == null || StringUtils.isBlank(request.getCode())) {
            return;
        }

        if (!StringUtils.equalsIgnoreCase(request.getCode(), "300502")) {
            return;
        }
        if (StringUtils.isBlank(request.getExtra())) {
            logger.error("运营商监控中,extra为空,request={}", JSON.toJSONString(request));
            return;
        }
        JSONObject jsonObject = (JSONObject) JsonUtils.toJsonObject(request.getExtra());
        String groupCode = jsonObject.getString("groupCode");
        String groupName = jsonObject.getString("groupName");
        if (StringUtils.isBlank(groupCode) || StringUtils.isBlank(groupName)) {
            logger.error("运营商监控中,传入参数有误request={},groupCode={},groupName={}", JSON.toJSONString(request), groupCode, groupName);
            return;
        }
        TaskOperatorMonitorMessage message = new TaskOperatorMonitorMessage();
        message.setTaskId(request.getTaskId());
        message.setAppId(request.getAppId());
        message.setGroupCode(groupCode);
        message.setGroupName(groupName);
        message.setDataTime(new Date());
        message.setStatus(ETaskOperatorMonitorStatus.COMFIRM_MOBILE.getStatus());
        logger.info("运营商监控,发送确认手机号(埋点)消息到monitor,message={},status={}",
                JSON.toJSONString(message), ETaskOperatorMonitorStatus.COMFIRM_MOBILE);
        monitorService.sendTaskOperatorMonitorMessage(message);

    }
}
