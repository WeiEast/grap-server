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
import com.treefinance.saas.grapserver.manager.DirectiveManager;
import com.treefinance.saas.grapserver.manager.domain.DirectiveBO;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;
import com.treefinance.saas.taskcenter.facade.service.TaskNextDirectiveFacade;
import com.treefinance.toolkit.util.json.Jackson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;

/**
 * @author Jerry
 * @date 2018/12/11 00:36
 */
@Service
public class DirectiveServiceAdapter extends AbstractTaskServiceAdapter implements DirectiveManager {

    private final TaskNextDirectiveFacade taskDirectiveFacade;

    @Autowired
    public DirectiveServiceAdapter(TaskNextDirectiveFacade taskDirectiveFacade) {
        this.taskDirectiveFacade = taskDirectiveFacade;
    }

    @Override
    public DirectiveBO queryNextDirective(@Nonnull Long taskId) {
        TaskResult<String> result = taskDirectiveFacade.getNextDirective(taskId);

        validateResponse(result, RpcActionEnum.QUERY_NEXT_DIRECTIVE, "taskId=" + taskId);

        return Jackson.parse(result.getData(), DirectiveBO.class);
    }

    @Override
    public void deleteDirective(Long taskId) {
        taskDirectiveFacade.deleteNextDirective(taskId);
    }

    @Override
    public void deleteDirective(Long taskId, String directive) {
        taskDirectiveFacade.deleteNextDirective(taskId, directive);
    }
}
