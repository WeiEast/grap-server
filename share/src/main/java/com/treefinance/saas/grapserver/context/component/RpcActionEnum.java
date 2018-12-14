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

package com.treefinance.saas.grapserver.context.component;

/**
 * RPC action list.
 *
 * @author Jerry
 * @date 2018/11/29 17:02
 */
public enum RpcActionEnum {

    /**
     * 查询app的颜色配置
     */
    QUERY_APP_COLOR_CONFIG,
    /**
     * 根据appId查询商户的license信息
     */
    QUERY_APP_LICENSE_BY_APP_ID,
    /**
     * 根据任务ID查询下一条指令
     */
    QUERY_NEXT_DIRECTIVE,
    /**
     * 根据appId查询商户的基本信息
     */
    QUERY_MERCHANT_BASE_INFO_BY_APP_ID,
    /**
     * 根据appId查询license业务信息
     */
    QUERY_APP_BIZ_LICENSE_BY_APP_ID,
    /**
     * 查询全部的license业务信息
     */
    QUERY_APP_BIZ_LICENSE_ALL,
    /**
     * 根据callbackId查询callback_biz信息
     */
    QUERY_APP_CALLBACK_BIZ_BY_CALLBACK_ID,
    /**
     * 查询
     */
    QUERY_APP_CALLBACK_BIZ_ALL,
    /**
     * 根据appId查询callback配置
     */
    QUERY_APP_CALLBACK_CONFIG_BY_APP_ID,
    /**
     * 查询全部的callback配置
     */
    QUERY_APP_CALLBACK_CONFIG_ALL,
    /**
     * 根据给定值查询biz-type信息
     */
    QUERY_APP_BIZ_TYPE_ASSIGNED,
    /**
     * 查询全部的biz-type信息
     */
    QUERY_APP_BIZ_TYPE_ALL,
    /**
     * 启动爬虫入口
     */
    ACQUISITION_ENTRY_BOOT,
    /**
     * 添加任务日志
     */
    ADD_TASK_LOG,
    /**
     * 查询失败任务的最后一条错误日志
     */
    QUERY_LAST_ERROR_TASK_LOG,
    /**
     * 根据任务ID查询任务
     */
    QUERY_TASK_BY_ID,
    /**
     * 根据条件列出运行中的任务
     */
    LIST_RUNNING_TASKS,
    /**
     * 列出跟指定任务具有相同触发条件的任务ID
     */
    LIST_TASK_ID_WITH_SAME_TRIGGER,
    /**
     * 创建任务
     */
    CREATE_TASK,
    /**
     * 针对正在处理中的任务，根据需要更新账号<code>accountNo</code>和网站标识<code>website</code>
     */
    UPDATE_ACCOUNT_WEBSITE_IF_NEED_WHEN_PROCESSING,
    /**
     * 针对正在处理中的任务，更新账号<code>accountNo</code>和网站标识<code>website</code>
     */
    UPDATE_ACCOUNT_WEBSITE_WHEN_PROCESSING,
    /**
     * 更新任务的<code>AccountNo</code>
     */
    UPDATE_TASK_ACCOUNT_NO,
    /**
     * 根据任务ID更新处理中的任务
     */
    UPDATE_PROCESSING_TASK_BY_ID,
    /**
     * 更新任务完成时的任务状态
     */
    UPDATE_STATUS_IF_DONE,
    /**
     * 取消任务
     */
    CANCEL_TASK,
    /**
     * 根据taskId查询已完成的任务
     */
    QUERY_COMPLETED_TASK_BY_ID,

}
