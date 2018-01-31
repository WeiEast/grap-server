package com.treefinance.saas.grapserver.biz.service;

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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void insert(String callbackUrl, AppCallbackConfigDTO config, Long taskId, String params, String result, long consumeTime) {
        TaskCallbackLog taskCallbackLog = new TaskCallbackLog();
        taskCallbackLog.setId(UidGenerator.getId());
        taskCallbackLog.setTaskId(taskId);
        if (config != null) {
            taskCallbackLog.setConfigId(Long.valueOf(config.getId()));
        } else {
            taskCallbackLog.setConfigId(0L);
        }
        taskCallbackLog.setUrl(callbackUrl);
        if (StringUtils.isNotBlank(params)) {
            taskCallbackLog.setRequestParam(params.length() > 1000 ? params.substring(0, 1000) : params);
        } else {
            taskCallbackLog.setRequestParam("");
        }
        if (StringUtils.isNotBlank(result)) {
            taskCallbackLog.setResponseData(result.length() > 1000 ? result.substring(0, 1000) : result);
        } else {
            taskCallbackLog.setResponseData("");
        }
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
