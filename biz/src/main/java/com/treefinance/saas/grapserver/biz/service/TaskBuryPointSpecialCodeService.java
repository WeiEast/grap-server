package com.treefinance.saas.grapserver.biz.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.treefinance.saas.grapserver.facade.enums.ETaskAttribute;
import com.treefinance.saas.taskcenter.facade.service.TaskBuryPointLogFacade;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

/**
 * 特殊任务埋点处理
 * @author haojiahong on 2017/10/27.
 */
@Service
public class TaskBuryPointSpecialCodeService {

    private final static Logger logger = LoggerFactory.getLogger(TaskBuryPointSpecialCodeService.class);

    @Autowired
    private TaskAttributeService taskAttributeService;
    @Autowired
    private TaskBuryPointLogFacade taskBuryPointLogFacade;

    @Async
    public void doProcess(String code, Long taskId, String appId, String extra) {
        //确认手机号及运营商页,运营商正在维护:需要记录用户信息,功夫贷会发短信给用户,挽回用户.
        if (StringUtils.equalsIgnoreCase(code, "300505")) {
            this.logTaskOperatorMaintainUser(taskId, appId, extra);
        }

        //确认手机号及运营商页，点击确认:需要记录运营商的groupCode和groupName信息到task_attribute表
        if (StringUtils.equalsIgnoreCase(code, "300502")) {
            this.logGroupCodeInAttribute(taskId, extra);
        }

    }

    private void logGroupCodeInAttribute(Long taskId, String extra) {
        if (StringUtils.isBlank(extra)) {
            logger.error("运营商监控中,extra为空,extra={}", JSON.toJSONString(extra));
            return;
        }
        JSONObject jsonObject = JSON.parseObject(extra);
        String groupCode = jsonObject.getString("groupCode");
        String groupName = jsonObject.getString("groupName");
        if (StringUtils.isBlank(groupCode) || StringUtils.isBlank(groupName)) {
            logger.error("运营商监控中,传入参数有误 extra={},groupCode={},groupName={}", JSON.toJSONString(extra), groupCode, groupName);
            return;
        }
        taskAttributeService.insertOrUpdateSelective(taskId, ETaskAttribute.OPERATOR_GROUP_CODE.getAttribute(), groupCode);
        taskAttributeService.insertOrUpdateSelective(taskId, ETaskAttribute.OPERATOR_GROUP_NAME.getAttribute(), groupName);
    }

    private void logTaskOperatorMaintainUser(Long taskId, String appId, String extra) {
        if (StringUtils.isBlank(extra)) {
            logger.error("运营商正在维护,记录用户信息,传入信息为空extra={}", extra);
            return;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        Map map = null;
        try {
            map = objectMapper.readValue(extra, Map.class);
        } catch (IOException e) {
            logger.error("运营商正在维护,记录用户信息,extra={}解析出错", extra, e);
            e.printStackTrace();
        }
        if (MapUtils.isEmpty(map)) {
            return;
        }
        String mobile = map.get("mobile") == null ? "" : String.valueOf(map.get("mobile"));
        if (StringUtils.isBlank(mobile)) {
            logger.error("运营商正在维护,记录用户信息,extra={}中未传入mobile信息", extra);
            return;
        }
        taskBuryPointLogFacade.logTaskOperatorMaintainUser(taskId, appId, extra);
    }

}
