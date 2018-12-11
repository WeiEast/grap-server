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

package com.treefinance.saas.grapserver.share.cache.redis;

import com.google.common.base.Joiner;

/**
 * @author haojiahong
 */
public class RedisKeyUtils {

    private final static String PREFIX_KEY = "saas-gateway:%s";
    private final static String PREFIX_LOGIN_LOCK_KEY = "saas-grap-server:login_lock:%s";
    private final static String PREFIX_ALIVE_TIME_UPDATE_LOCK_KEY = "saas-grap-server:alive_time_update_lock:%s";
    private final static String PREFIX_REDIS_LOCK_KEY = "saas-grap-server:redis_lock";
    private final static String PREFIX_CREATE_TASK_USER_LOCK_KEY = "saas-grap-server:create_task_user_lock";
    private final static String PREFIX_TASK_ACTIVE_TIME_KEY = "saas-grap-server-task-active-time:%s";

    private final static String PREFIX_TASK_LOGIN_TIME_KEY = "saas_gateway_task_time:";
    private final static String PREFIX_LOGIN_TASK_SET_KEY = "saas_gateway_task_time:login-taskids";

    public static String genRedisKey(String key) {
        return String.format(PREFIX_KEY, key);
    }

    /**
     * 获取登录锁redis key
     *
     * @param taskId 任务id
     */
    public static String genLoginLockKey(Long taskId) {
        return String.format(PREFIX_LOGIN_LOCK_KEY, taskId);
    }

    public static String genAliveTimeUpdateLockKey(Long taskId) {
        return String.format(PREFIX_ALIVE_TIME_UPDATE_LOCK_KEY, taskId);
    }

    public static String genRedisLockKey(Long taskId, String... s) {
        return Joiner.on(":").join(PREFIX_REDIS_LOCK_KEY, taskId, s);
    }

    public static String genRedisLockKey(String prefix, String... s) {
        return Joiner.on(":").join(PREFIX_REDIS_LOCK_KEY, prefix, s);
    }

    /**
     * 获取任务最近活跃时间redis key
     *
     * @param taskId 任务id
     */
    public static String genTaskActiveTimeKey(Long taskId) {
        return String.format(PREFIX_TASK_ACTIVE_TIME_KEY, taskId);
    }

    /**
     * 获取创建任务用户锁redis key
     *
     * @param appId    商户id
     * @param uniqueId 用户id
     * @param bizType  业务类型
     */
    public static String genCreateTaskUserLockKey(String appId, String uniqueId, String bizType) {
        return Joiner.on(":").join(PREFIX_CREATE_TASK_USER_LOCK_KEY, appId, uniqueId, bizType);
    }

    /**
     * 获取记录任务登录时间的redis key
     */
    public static String genTaskLoginTimeKey(Long taskId) {
        return PREFIX_TASK_LOGIN_TIME_KEY + taskId;
    }

}
