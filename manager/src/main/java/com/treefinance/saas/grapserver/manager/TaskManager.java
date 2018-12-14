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

package com.treefinance.saas.grapserver.manager;

import com.treefinance.saas.grapserver.manager.domain.RecordStatusBO;
import com.treefinance.saas.grapserver.manager.domain.TaskBO;
import com.treefinance.saas.grapserver.manager.param.TaskParams;
import com.treefinance.saas.grapserver.manager.param.TaskUpdateParams;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Date;
import java.util.List;

/**
 * @author Jerry
 * @date 2018/12/12 02:06
 */
public interface TaskManager {

    /**
     * 根据任务ID获取任务
     *
     * @param id 任务ID
     * @return 任务{@link TaskBO}
     */
    TaskBO getTaskById(@Nonnull Long id);

    /**
     * 根据指定的环境<code>saasEnv</code>和时间区间列出正在运行中的任务。
     * <p/>
     * 注意：<code>startTime</code> &lt; <code>endTime</code>
     *
     * @param saasEnv 环境标识
     * @param startTime 起始时间（包含）
     * @param endTime 终止时间（不包含）
     * @return 运行中的任务{@link TaskBO}列表
     */
    List<TaskBO> listRunningTasks(@Nonnull Byte saasEnv, @Nonnull Date startTime, @Nonnull Date endTime);

    /**
     * 列出跟指定任务具有相同触发条件的任务ID。 触发条件包括相同的appId，相同的uniqueId以及相同的业务类型
     *
     * @param taskId 任务ID
     * @return taskId的列表
     */
    List<Long> listTaskIdsWithSameTrigger(@Nonnull Long taskId);

    /**
     * 根据taskId查询任务并检查是否是完成的。
     *
     * @param id 任务ID
     * @return {@link TaskBO} if the task was completed, otherwise null.
     */
    TaskBO queryCompletedTaskById(@Nonnull Long id);

    /**
     * 创建任务
     * 
     * @param params 任务初始化参数
     * @return 创建好的任务ID
     */
    Long createTask(@Nonnull TaskParams params);

    /**
     * 更新处理中的任务信息
     *
     * @param params 更新参数
     * @return 更新的记录数
     */
    int updateProcessingTaskById(@Nonnull TaskUpdateParams params);

    /**
     * 根据需要更新任务的<code>accountNo</code>和<code>website</code>
     * 
     * @param taskId 任务ID
     * @param accountNo 账号
     * @param website 网站标识
     * @return 记录更新结果
     */
    RecordStatusBO recordAccountNoAndWebsite(@Nonnull Long taskId, @Nullable String accountNo, @Nullable String website);

    /**
     * 设置任务的<code>accountNo</code>和<code>website</code>
     *
     * @param taskId 任务ID
     * @param accountNo 账户
     * @param website 网站标识
     * @return 记录更新结果
     */
    RecordStatusBO setAccountNoAndWebsite(@Nonnull Long taskId, @Nullable String accountNo, @Nullable String website);

    /**
     * 更新任务完成时的任务状态，比如：成功，失败或取消
     *
     * @param taskId 任务ID
     * @param status 任务状态，比如：成功，失败或取消
     * @return stepCode when the task was cancel or failed, otherwise return null.
     */
    String updateStatusIfDone(@Nonnull Long taskId, @Nonnull Byte status);

    /**
     * 取消任务
     * 
     * @param taskId 任务ID
     */
    void cancelTask(@Nonnull Long taskId);
}
