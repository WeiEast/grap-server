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

import com.treefinance.saas.grapserver.biz.service.TaskAliveService;
import com.treefinance.saas.grapserver.context.component.AbstractService;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;
import com.treefinance.saas.taskcenter.facade.service.TaskAliveFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Jerry
 * @date 2018/12/12 13:30
 */
@Service("taskAliveService")
public class TaskAliveServiceImpl extends AbstractService implements TaskAliveService {
    @Autowired
    private TaskAliveFacade taskAliveFacade;

    @Override
    public void updateTaskActiveTime(Long taskId) {
        taskAliveFacade.updateTaskActiveTime(taskId);
    }

    @Override
    public String getTaskAliveTime(Long taskId) {
        TaskResult<String> rpcResult = taskAliveFacade.getTaskAliveTime(taskId);
        return rpcResult.getData();
    }
}
