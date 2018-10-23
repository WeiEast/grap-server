package com.treefinance.saas.grapserver.biz.service;

import com.treefinance.saas.grapserver.common.exception.UnknownException;
import com.treefinance.saas.grapserver.common.utils.DataConverterUtils;
import com.treefinance.saas.grapserver.dao.entity.TaskCallbackLog;
import com.treefinance.saas.taskcenter.facade.result.TaskCallbackLogRO;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;
import com.treefinance.saas.taskcenter.facade.service.TaskCallbackLogFacade;
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
    private TaskCallbackLogFacade taskCallbackLogFacade;

    /**
     * 查询任务ID
     *
     * @param taskId
     * @param configIds
     * @return
     */
    public List<TaskCallbackLog> getTaskCallbackLogs(Long taskId, List<Long> configIds) {
        TaskResult<List<TaskCallbackLogRO>> rpcResult = taskCallbackLogFacade.getTaskCallbackLogs(taskId, configIds);
        if (!rpcResult.isSuccess()) {
            throw new UnknownException("调用taskcenter失败");
        }
        List<TaskCallbackLog> taskCallbackLogList = DataConverterUtils.convert(rpcResult.getData(), TaskCallbackLog.class);
        return taskCallbackLogList;
    }
}
