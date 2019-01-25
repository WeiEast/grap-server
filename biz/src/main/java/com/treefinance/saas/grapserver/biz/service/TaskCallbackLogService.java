package com.treefinance.saas.grapserver.biz.service;

import com.treefinance.saas.grapserver.biz.dto.TaskCallbackLog;
import com.treefinance.saas.grapserver.exception.UnknownException;
import com.treefinance.saas.grapserver.context.component.AbstractService;
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
public class TaskCallbackLogService extends AbstractService {

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
        return convert(rpcResult.getData(), TaskCallbackLog.class);
    }

}
