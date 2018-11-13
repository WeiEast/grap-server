package com.treefinance.saas.grapserver.biz.service;

import com.google.common.collect.Lists;
import com.treefinance.saas.grapserver.common.exception.UnknownException;
import com.treefinance.saas.grapserver.common.utils.DataConverterUtils;
import com.treefinance.saas.grapserver.dao.entity.TaskLog;
import com.treefinance.saas.taskcenter.facade.result.TaskLogRO;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;
import com.treefinance.saas.taskcenter.facade.service.TaskLogFacade;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author luoyihua on 2017/4/26.
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
    private TaskLogFacade taskLogFacade;

    /**
     * 添加一条日志记录
     */
    public Long insert(Long taskId, String msg, Date processTime, String errorMsg) {
        TaskResult<Long> rpcResult = taskLogFacade.insert(taskId, msg, processTime, errorMsg);
        if (!rpcResult.isSuccess()) {
            throw new UnknownException("调用taskcenter失败");
        }
        logger.info("记录任务日志:taskId={},msg={},processTime={},errorMsg={}", taskId, msg, processTime, errorMsg);
        return rpcResult.getData();
    }

    /**
     * 记录任务超时
     */
    public Long logTimeoutTask(Long taskId, String errorMessage) {
        return insert(taskId, TASK_TIMEOUT_LOG, new Date(), errorMessage);
    }

    /**
     * 根据任务ID查询最新任务日志
     */
    public TaskLog queryLatestErrorLog(Long taskId) {
        if (taskId == null) {
            return null;
        }
        TaskResult<TaskLogRO> rpcResult = taskLogFacade.queryLastestErrorLog(taskId);
        if (!rpcResult.isSuccess()) {
            throw new UnknownException("调用taskcenter失败");
        }
        if (rpcResult.getData() == null) {
            return null;

        }
        return DataConverterUtils.convert(rpcResult.getData(), TaskLog.class);
    }

    public List<TaskLog> queryTaskLog(Long taskId, String msg) {
        TaskResult<List<TaskLogRO>> rpcResult = taskLogFacade.queryTaskLog(taskId, msg);
        if (!rpcResult.isSuccess()) {
            throw new UnknownException("调用taskcenter失败");
        }
        if (CollectionUtils.isEmpty(rpcResult.getData())) {
            return Lists.newArrayList();
        }
        return DataConverterUtils.convert(rpcResult.getData(), TaskLog.class);
    }

}
