/*
 * Copyright © 2015 - 2017 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.treefinance.saas.grapserver.biz.service.impl;

import com.treefinance.saas.grapserver.biz.domain.TaskLog;
import com.treefinance.saas.grapserver.biz.service.TaskLogService;
import com.treefinance.saas.grapserver.context.component.AbstractService;
import com.treefinance.saas.grapserver.manager.TaskLogManager;
import com.treefinance.saas.grapserver.manager.domain.TaskLogBO;
import com.treefinance.saas.grapserver.manager.param.TaskLogParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;

import java.util.Date;
import java.util.List;

/**
 * @author Jerry
 * @date 2018/12/12 14:03
 */
@Service("taskLogService")
public class TaskLogServiceImpl extends AbstractService implements TaskLogService {
    @Autowired
    private TaskLogManager taskLogManager;

    @Override
    public Long insert(Long taskId, String msg, Date processTime, String errorMsg) {
        TaskLogParams params = new TaskLogParams();
        params.setTaskId(taskId);
        params.setMsg(msg);
        params.setErrorMsg(errorMsg);
        params.setProcessTime(processTime);

        return taskLogManager.addLog(params);
    }

    @Override
    public TaskLog queryLatestErrorLog(@Nonnull Long taskId) {
        TaskLogBO log = taskLogManager.queryLatestErrorLogByTaskId(taskId);

        return convert(log, TaskLog.class);
    }

    @Override
    public int countTaskLogsByTaskIdAndMsg(@Nonnull Long taskId, @Nonnull String msg) {
        // TODO: 2018/12/12 需要改进，可以直接提供count接口
        List<TaskLogBO> list = taskLogManager.listTaskLogsByTaskIdAndMsg(taskId, msg);

        return list.size();
    }

}
