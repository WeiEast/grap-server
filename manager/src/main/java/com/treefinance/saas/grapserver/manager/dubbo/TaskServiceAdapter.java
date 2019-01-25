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
import com.treefinance.saas.grapserver.manager.domain.RecordStatusBO;
import com.treefinance.saas.grapserver.manager.domain.TaskBO;
import com.treefinance.saas.grapserver.manager.param.TaskParams;
import com.treefinance.saas.grapserver.manager.param.TaskUpdateParams;
import com.treefinance.saas.taskcenter.facade.request.TaskCreateRequest;
import com.treefinance.saas.taskcenter.facade.request.TaskUpdateRequest;
import com.treefinance.saas.taskcenter.facade.response.TaskResponse;
import com.treefinance.saas.taskcenter.facade.result.SimpleTaskDTO;
import com.treefinance.saas.taskcenter.facade.result.TaskUpdateStatusDTO;
import com.treefinance.saas.taskcenter.facade.service.TaskFacade;
import com.treefinance.toolkit.util.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public TaskBO getTaskById(@Nonnull Long id) {
        Preconditions.notNull("id", id);

        TaskResponse<SimpleTaskDTO> response = taskFacade.getTaskById(id);

        Map<String, Object> params = new HashMap<>(1);
        params.put("id", id);
        validateResponseEntity(response, RpcActionEnum.QUERY_TASK_BY_ID, params);

        return convert(response.getEntity(), TaskBO.class);
    }

    @Override
    public List<TaskBO> listRunningTasks(@Nonnull Byte saasEnv, @Nonnull Date startTime, @Nonnull Date endTime) {
        Preconditions.notNull("saasEnv", saasEnv);
        Preconditions.notNull("startTime", startTime);
        Preconditions.notNull("endTime", endTime);

        TaskResponse<List<SimpleTaskDTO>> response = taskFacade.listRunningTasks(saasEnv, startTime, endTime);

        Map<String, Object> params = new HashMap<>(3);
        params.put("saasEnv", saasEnv);
        params.put("startTime", startTime);
        params.put("endTime", endTime);

        validateResponse(response, RpcActionEnum.LIST_RUNNING_TASKS, params);

        return convert(response.getEntity(), TaskBO.class);
    }

    @Override
    public List<Long> listTaskIdsWithSameTrigger(@Nonnull Long taskId) {
        Preconditions.notNull("taskId", taskId);

        TaskResponse<List<Long>> response = taskFacade.listTaskIdsWithSameTrigger(taskId);

        Map<String, Object> params = new HashMap<>(1);
        params.put("taskId", taskId);

        validateResponse(response, RpcActionEnum.LIST_TASK_ID_WITH_SAME_TRIGGER, params);

        List<Long> entity = response.getEntity();
        return entity == null? Collections.emptyList(): entity;
    }

    @Override
    public TaskBO queryCompletedTaskById(@Nonnull Long id) {
        Preconditions.notNull("id", id);

        TaskResponse<SimpleTaskDTO> response = taskFacade.queryCompletedTaskById(id);

        Map<String, Object> params = new HashMap<>(1);
        params.put("id", id);
        validateResponse(response, RpcActionEnum.QUERY_COMPLETED_TASK_BY_ID, params);

        return convert(response.getEntity(), TaskBO.class);
    }

    @Override
    public Long createTask(@Nonnull TaskParams params) {
        TaskCreateRequest request = new TaskCreateRequest();
        request.setAppId(params.getAppId());
        request.setUniqueId(params.getUniqueId());
        request.setBizType(params.getBizType());
        request.setAccountNo(StringUtils.trimToNull(params.getAccountNo()));
        request.setWebsite(StringUtils.trimToNull(params.getWebsite()));
        request.setSaasEnv(params.getSaasEnv());
        request.setSource(params.getSource());
        request.setExtra(params.getExtra());
        request.setStatus((byte)0);
        TaskResponse<Long> response = taskFacade.createTask(request);

        validateResponseEntity(response, RpcActionEnum.CREATE_TASK, request);

        return response.getEntity();
    }

    @Override
    public int updateProcessingTaskById(@Nonnull TaskUpdateParams params) {
        TaskUpdateRequest request = convertStrict(params, TaskUpdateRequest.class);

        TaskResponse<Integer> response = taskFacade.updateProcessingTaskById(request);

        validateResponseEntity(response, RpcActionEnum.UPDATE_PROCESSING_TASK_BY_ID, request);

        return response.getEntity();
    }

    @Override
    public RecordStatusBO recordAccountNoAndWebsite(@Nonnull Long taskId, @Nullable String accountNo, @Nullable String website) {
        Preconditions.notNull("taskId", taskId);
        TaskResponse<TaskUpdateStatusDTO> response = taskFacade.updateAccountNoAndWebsiteIfNeedWhenProcessing(taskId, accountNo, website);

        Map<String, Object> params = new HashMap<>(3);
        params.put("taskId", taskId);
        params.put("accountNo", accountNo);
        params.put("website", website);

        validateResponseEntity(response, RpcActionEnum.UPDATE_ACCOUNT_WEBSITE_IF_NEED_WHEN_PROCESSING, params);

        return convert(response.getEntity(), RecordStatusBO.class);
    }

    @Override
    public RecordStatusBO setAccountNoAndWebsite(@Nonnull Long taskId, @Nullable String accountNo, @Nullable String website) {
        Preconditions.notNull("taskId", taskId);
        TaskResponse<TaskUpdateStatusDTO> response = taskFacade.updateAccountNoAndWebsiteWhenProcessing(taskId, accountNo, website);

        Map<String, Object> params = new HashMap<>(3);
        params.put("taskId", taskId);
        params.put("accountNo", accountNo);
        params.put("website", website);

        validateResponseEntity(response, RpcActionEnum.UPDATE_ACCOUNT_WEBSITE_WHEN_PROCESSING, params);

        return convert(response.getEntity(), RecordStatusBO.class);
    }

    @Override
    public String updateStatusIfDone(@Nonnull Long taskId, @Nonnull Byte status) {
        Preconditions.notNull("taskId", taskId);
        Preconditions.notNull("status", status);

        TaskResponse<String> response = taskFacade.updateStatusIfDone(taskId, status);

        Map<String, Object> params = new HashMap<>(2);
        params.put("taskId", taskId);
        params.put("status", status);

        validateResponse(response, RpcActionEnum.UPDATE_STATUS_IF_DONE, params);

        return response.getEntity();
    }

    @Override
    public void cancelTask(@Nonnull Long taskId) {
        Preconditions.notNull("taskId", taskId);

        TaskResponse<Void> response = taskFacade.cancelTask(taskId);

        Map<String, Object> params = new HashMap<>(1);
        params.put("taskId", taskId);

        validateResponse(response, RpcActionEnum.CANCEL_TASK, params);
    }
}
