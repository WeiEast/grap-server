package com.treefinance.saas.grapserver.biz.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.treefinance.commonservice.uid.UidGenerator;
import com.treefinance.saas.grapserver.dao.entity.TaskOperatorMaintainUserLog;
import com.treefinance.saas.grapserver.dao.mapper.TaskOperatorMaintainUserLogMapper;
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
 * Created by haojiahong on 2017/10/27.
 */
@Service
public class TaskBuryPointSpecialCodeService {
    private final static Logger logger = LoggerFactory.getLogger(TaskBuryPointSpecialCodeService.class);

    @Autowired
    private TaskOperatorMaintainUserLogMapper taskOperatorMaintainUserLogMapper;


    @Async
    public void doProcess(String code, Long taskId, String appId, String extra) {
        //确认手机号及运营商页,运营商正在维护,需要记录用户信息,功夫贷会发短信给用户,挽回用户.
        if (StringUtils.equalsIgnoreCase(code, "300505")) {
            this.logTaskOperatorMaintainUser(taskId, appId, extra);
        }

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
        String operatorName = map.get("operatorName") == null ? "" : String.valueOf(map.get("operatorName"));
        if (StringUtils.isBlank(mobile)) {
            logger.error("运营商正在维护,记录用户信息,extra={}中未传入mobile信息", extra);
            return;
        }
        TaskOperatorMaintainUserLog log = new TaskOperatorMaintainUserLog();
        log.setId(UidGenerator.getId());
        log.setTaskId(taskId);
        log.setAppId(appId);
        log.setMobile(mobile);
        log.setOperatorName(operatorName);
        taskOperatorMaintainUserLogMapper.insertSelective(log);

    }
}
