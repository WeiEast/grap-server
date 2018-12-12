/*
 * Copyright © 2015 - 2017 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.treefinance.saas.grapserver.manager.dubbo;

import com.google.common.collect.ImmutableMap;
import com.treefinance.saas.grapserver.context.component.RpcActionEnum;
import com.treefinance.saas.grapserver.manager.TaskLogManager;
import com.treefinance.saas.grapserver.manager.domain.TaskLogBO;
import com.treefinance.saas.grapserver.manager.param.TaskLogParams;
import com.treefinance.saas.taskcenter.facade.result.TaskLogRO;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;
import com.treefinance.saas.taskcenter.facade.service.TaskLogFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;

import java.util.List;

/**
 * @author Jerry
 * @date 2018/12/12 13:33
 */
@Service
public class TaskLogServiceAdapter extends AbstractTaskServiceAdapter implements TaskLogManager {
    private final TaskLogFacade taskLogFacade;

    @Autowired
    public TaskLogServiceAdapter(TaskLogFacade taskLogFacade) {
        this.taskLogFacade = taskLogFacade;
    }

    @Override
    public Long addLog(@Nonnull TaskLogParams params) {
        TaskResult<Long> result = taskLogFacade.insert(params.getTaskId(), params.getMsg(), params.getProcessTime(), params.getErrorMsg());

        logger.info("记录任务日志, params={}, result={}", params, result);
        validateResult(result, RpcActionEnum.ADD_TASK_LOG, params);

        return result.getData();
    }

    @Override
    public TaskLogBO getLatestErrorLogByTaskId(@Nonnull Long taskId) {
        TaskResult<TaskLogRO> result = taskLogFacade.queryLastErrorLog(taskId);

        validateResult(result, RpcActionEnum.QUERY_LAST_ERROR_TASK_LOG, ImmutableMap.of("taskId", taskId));

        return convert(result.getData(), TaskLogBO.class);
    }

    @Override
    public List<TaskLogBO> listTaskLogsByTaskIdAndMsg(@Nonnull Long taskId, @Nonnull String msg) {
        TaskResult<List<TaskLogRO>> result = taskLogFacade.queryTaskLog(taskId, msg);

        validateResponse(result, RpcActionEnum.QUERY_LAST_ERROR_TASK_LOG, ImmutableMap.of("taskId", taskId, "msg", msg));

        return convert(result.getData(), TaskLogBO.class);
    }
}
