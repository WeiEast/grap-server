package com.treefinance.saas.grapserver.biz.service;

import com.treefinance.saas.grapserver.common.exception.UnknownException;
import com.treefinance.saas.grapserver.common.utils.DataConverterUtils;
import com.treefinance.saas.grapserver.biz.dto.TaskCallbackLog;
import com.treefinance.saas.taskcenter.facade.result.TaskCallbackLogRO;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;
import com.treefinance.saas.taskcenter.facade.service.TaskCallbackLogFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author haojiahong on 2017/8/17.
 */
@Service
public class TaskCallbackLogService {

    @Autowired
    private TaskCallbackLogFacade taskCallbackLogFacade;

    /**
     * 查询任务ID
     */
    public List<TaskCallbackLog> getTaskCallbackLogs(Long taskId, List<Long> configIds) {
        TaskResult<List<TaskCallbackLogRO>> rpcResult = taskCallbackLogFacade.getTaskCallbackLogs(taskId, configIds);
        if (!rpcResult.isSuccess()) {
            throw new UnknownException("调用taskcenter失败");
        }
        return DataConverterUtils.convert(rpcResult.getData(), TaskCallbackLog.class);
    }

}
