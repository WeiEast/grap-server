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

import com.treefinance.saas.assistant.model.Constants;
import com.treefinance.saas.grapserver.biz.service.TaskService;
import com.treefinance.saas.grapserver.biz.validation.AccessValidationHandler;
import com.treefinance.saas.grapserver.common.enums.EBizType;
import com.treefinance.saas.grapserver.context.component.AbstractService;
import com.treefinance.saas.grapserver.context.config.DiamondConfig;
import com.treefinance.saas.grapserver.exception.UniqueidMaxException;
import com.treefinance.saas.grapserver.manager.TaskManager;
import com.treefinance.saas.grapserver.manager.domain.RecordStatusBO;
import com.treefinance.saas.grapserver.manager.domain.TaskBO;
import com.treefinance.saas.grapserver.manager.param.TaskParams;
import com.treefinance.saas.grapserver.manager.param.TaskUpdateParams;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.List;

/**
 * @author Jerry
 * @date 2018/12/14 13:24
 */
@Service("taskService")
public class TaskServiceImpl extends AbstractService implements TaskService {

    private static final String UNIQUE_ACCOUNT_CACHE_PREFIX = "saas_gateway_task_account_uniqueid:";
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private DiamondConfig diamondConfig;
    @Autowired
    private TaskManager taskManager;
    @Autowired
    private AccessValidationHandler validationHandler;

    @Override
    public String getUniqueIdInTask(Long taskId) {
        TaskBO task = taskManager.getTaskById(taskId);

        return task.getUniqueId();
    }

    @Override
    public void validateTask(String appId, String uniqueId, EBizType bizType) {
        validationHandler.validateTask(appId, uniqueId, bizType);
    }

    @Override
    public Long createTask(String appId, String uniqueId, EBizType bizType) {
        return createTask(appId, uniqueId, bizType, null, null, null);
    }

    @Override
    public Long createTask(String appId, String uniqueId, EBizType bizType, String extra, String website, String source) {
        validateTask(appId, uniqueId, bizType);

        // 校验uniqueId
        List<String> excludedAppIds = diamondConfig.getExcludedAppIds();
        if(CollectionUtils.isEmpty(excludedAppIds) || !excludedAppIds.contains(appId)){
            checkUniqueId(uniqueId, appId, bizType);
        }

        TaskParams params = new TaskParams();
        params.setAppId(appId);
        params.setUniqueId(uniqueId);
        params.setBizType(bizType.getCode());
        params.setSource(source);
        params.setWebsite(website);
        params.setExtra(extra);
        params.setSaasEnv(Byte.valueOf(Constants.SAAS_ENV_VALUE));

        return taskManager.createTask(params);
    }

    /**
     * uniqueId校验
     */
    private void checkUniqueId(String uniqueId, String appId, EBizType bizType) {
        Integer maxCount = diamondConfig.getMaxCount();
        if (maxCount != null) {
            String key = keyOfUniqueId(uniqueId, appId, bizType.getCode());
            Long count = redisTemplate.opsForSet().size(key);
            if (count != null && count >= maxCount) {
                throw new UniqueidMaxException("uniqueId访问超阈值");
            }
        }
    }

    private String keyOfUniqueId(String uniqueId, String appId, Byte bizType) {
        // prefix
        return UNIQUE_ACCOUNT_CACHE_PREFIX + ":" + appId + ":" + bizType + ":" + uniqueId;
    }

    @Override
    public void updateWebsite(Long taskId, String website) {
        TaskUpdateParams params = new TaskUpdateParams();
        params.setId(taskId);
        params.setWebsite(website);
        taskManager.updateProcessingTaskById(params);
    }

    @Override
    public void recordAccountNoAndWebsite(@Nonnull Long taskId, @Nullable String accountNo, @Nullable String website) {
        RecordStatusBO status = taskManager.recordAccountNoAndWebsite(taskId, accountNo, website);
        if (status.isAccountNoUpdate()) {
            TaskBO task = taskManager.getTaskById(taskId);
            if (task != null) {
                String key = keyOfUniqueId(task.getUniqueId(), task.getAppId(), task.getBizType());
                redisTemplate.opsForSet().add(key, accountNo);
            }
        }
    }

    @Override
    public void cancelTask(@Nonnull Long taskId) {
        logger.info("取消任务 : taskId={} ", taskId);
        taskManager.cancelTask(taskId);
    }
}
