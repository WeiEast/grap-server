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

import com.treefinance.saas.grapserver.context.component.RpcActionEnum;
import com.treefinance.saas.grapserver.manager.TaskManager;
import com.treefinance.saas.grapserver.manager.domain.TaskBO;
import com.treefinance.saas.taskcenter.facade.request.TaskRequest;
import com.treefinance.saas.taskcenter.facade.result.TaskRO;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;
import com.treefinance.saas.taskcenter.facade.service.TaskFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Jerry
 * @date 2018/12/12 02:07
 */
@Service
public class TaskServiceAdapter extends AbstractTaskServiceAdapter implements TaskManager {

    private final TaskFacade taskFacade;

    @Autowired
    public TaskServiceAdapter(TaskFacade taskFacade) {
        this.taskFacade = taskFacade;
    }

    @Override
    public TaskBO getTaskById(Long id) {
        TaskRequest request = new TaskRequest();
        request.setId(id);

        TaskResult<TaskRO> result = taskFacade.getTaskByPrimaryKey(request);

        validateResult(result, RpcActionEnum.QUERY_TASK_BY_ID, request);

        return convert(result.getData(), TaskBO.class);
    }
}
