package com.treefinance.saas.grapserver.biz.service;

import com.alibaba.fastjson.JSON;
import com.treefinance.commonservice.uid.UidGenerator;
import com.treefinance.saas.grapserver.dao.entity.TaskLog;
import com.treefinance.saas.grapserver.dao.entity.TaskLogCriteria;
import com.treefinance.saas.grapserver.dao.mapper.TaskLogMapper;
import com.treefinance.saas.grapserver.dao.mapper.TaskMapper;
import com.treefinance.saas.grapserver.common.enums.TaskStepEnum;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Created by luoyihua on 2017/4/26.
 */
@Service
public class TaskLogService {

    /**
     * logger
     */
    private static final Logger logger = LoggerFactory.getLogger(TaskLogService.class);

    public static final String TASK_CANCEL_LOG = "任务取消";
    public static final String TASK_CREATE_LOG = "任务创建";
    public static final String TASK_TIMEOUT_LOG = "任务超时";
    public static final String TASK_FAIL_LOG = "任务失败";
    public static final String TASK_SUCCESS_LOG = "任务成功";


    @Autowired
    private TaskLogMapper taskLogMapper;

    @Autowired
    private TaskMapper taskMapper;

    /**
     * 添加一条日志记录
     *
     * @param taskId
     * @param msg
     * @param processTime
     * @return
     */
    public Long insert(Long taskId, String msg, Date processTime, String errorMsg) {
        long id = UidGenerator.getId();
        TaskLog taskLog = new TaskLog();
        taskLog.setId(id);
        taskLog.setTaskId(taskId);
        taskLog.setMsg(msg);
        taskLog.setStepCode(TaskStepEnum.getStepCodeByText(msg));
        taskLog.setOccurTime(processTime);
        taskLog.setErrorMsg(errorMsg != null && errorMsg.length() > 1000 ? errorMsg.substring(0, 1000) : errorMsg);
        taskLogMapper.insertSelective(taskLog);
        logger.info("记录任务日志: {}", JSON.toJSONString(taskLog));
        return id;
    }

    /**
     * 记录取消任务
     *
     * @param taskId
     * @return
     */
    public Long logCancleTask(Long taskId) {
        return insert(taskId, TASK_CANCEL_LOG, new Date(), null);
    }

    /**
     * 记录创建任务
     *
     * @param taskId
     * @return
     */
    public Long logCreateTask(Long taskId) {
        return insert(taskId, TASK_CREATE_LOG, new Date(), null);
    }

    /**
     * 记录任务超时
     *
     * @param taskId
     * @return
     */
    public Long logTimeoutTask(Long taskId, String errorMessage) {
        return insert(taskId, TASK_TIMEOUT_LOG, new Date(), errorMessage);
    }

    /**
     * 根据任务ID查询最新任务日志
     *
     * @param taskId
     * @return
     */
    public TaskLog queryLastestErrorLog(Long taskId) {
        if (taskId == null) {
            return null;
        }
        TaskLogCriteria criteria = new TaskLogCriteria();
        criteria.setOrderByClause("LastUpdateTime desc");
        criteria.createCriteria().andTaskIdEqualTo(taskId);
        List<TaskLog> taskLogs = taskLogMapper.selectByExample(criteria);
        if (CollectionUtils.isEmpty(taskLogs)) {
            return null;
        }
        Optional<TaskLog> optional = taskLogs.stream().filter(log -> !log.getMsg().contains(TASK_CANCEL_LOG)
                && !log.getMsg().contains("成功")).findFirst();
        return optional.isPresent() ? optional.get() : null;
    }

    public Long logFailTask(Long taskId) {
        return insert(taskId, TASK_FAIL_LOG, new Date(), null);
    }

    public Long logSuccessTask(Long taskId) {
        return insert(taskId, TASK_SUCCESS_LOG, new Date(), null);
    }


}
