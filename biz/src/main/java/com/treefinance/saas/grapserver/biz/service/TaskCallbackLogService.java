package com.treefinance.saas.grapserver.biz.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.treefinance.commonservice.uid.UidGenerator;
import com.treefinance.saas.grapserver.common.model.dto.AppCallbackConfigDTO;
import com.treefinance.saas.grapserver.dao.entity.TaskCallbackLog;
import com.treefinance.saas.grapserver.dao.entity.TaskCallbackLogCriteria;
import com.treefinance.saas.grapserver.dao.mapper.TaskCallbackLogMapper;
import com.treefinance.saas.grapserver.dao.mapper.TaskCallbackLogUpdateMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by haojiahong on 2017/8/17.
 */
@Service
public class TaskCallbackLogService {

    private static final Logger logger = LoggerFactory.getLogger(TaskLogService.class);
    @Autowired
    protected TaskCallbackLogMapper taskCallbackLogMapper;
    @Autowired
    private TaskCallbackLogUpdateMapper taskCallbackLogUpdateMapper;

    public void insert(AppCallbackConfigDTO config, Long taskId, Byte type, String params, String result,
                       long consumeTime, int httpCode) {
        TaskCallbackLog taskCallbackLog = new TaskCallbackLog();
        taskCallbackLog.setId(UidGenerator.getId());
        taskCallbackLog.setTaskId(taskId);
        if (config != null) {
            taskCallbackLog.setConfigId(Long.valueOf(config.getId()));
            taskCallbackLog.setUrl(config.getUrl());
        } else {
            taskCallbackLog.setConfigId(0L);
        }
        taskCallbackLog.setType(type);
        if (StringUtils.isNotBlank(params)) {
            taskCallbackLog.setRequestParam(params.length() > 1000 ? params.substring(0, 1000) : params);
        } else {
            taskCallbackLog.setRequestParam("");
        }
        if (StringUtils.isNotBlank(result)) {
            taskCallbackLog.setResponseData(result.length() > 1000 ? result.substring(0, 1000) : result);
            if (httpCode == 200) {
                taskCallbackLog.setCallbackMsg("回调成功");
            } else {
                try {
                    JSONObject jsonObject = JSON.parseObject(result);
                    String errorMsg = jsonObject.getString("errorMsg");
                    String errorCode = jsonObject.getString("code");
                    taskCallbackLog.setCallbackCode(errorCode);
                    if (StringUtils.isNotBlank(errorMsg)) {
                        taskCallbackLog.setCallbackMsg(errorMsg);
                    } else {
                        taskCallbackLog.setCallbackMsg("回调错误信息为空");
                    }
                } catch (Exception e) {
                    logger.error("记录回调错误信息:解析返回回调结果json有误,taskId={},回调返回结果result={}", taskId, result);
                    taskCallbackLog.setCallbackMsg(result.length() > 1000 ? result.substring(0, 100) + "..." : result);
                }

            }
        } else {
            taskCallbackLog.setResponseData("");
        }
        taskCallbackLog.setHttpCode(httpCode);
        taskCallbackLog.setConsumeTime((int) consumeTime);

        taskCallbackLogUpdateMapper.insertOrUpdateSelective(taskCallbackLog);
    }

    /**
     * 查询任务ID
     *
     * @param taskId
     * @param configIds
     * @return
     */
    public List<TaskCallbackLog> getTaskCallbackLogs(Long taskId, List<Long> configIds) {
        TaskCallbackLogCriteria taskCallbackLogCriteria = new TaskCallbackLogCriteria();
        TaskCallbackLogCriteria.Criteria criteria = taskCallbackLogCriteria.createCriteria();
        if (CollectionUtils.isNotEmpty(configIds)) {
            criteria.andConfigIdIn(configIds);

        }
        criteria.andTaskIdEqualTo(taskId);
        return taskCallbackLogMapper.selectByExample(taskCallbackLogCriteria);
    }
}
