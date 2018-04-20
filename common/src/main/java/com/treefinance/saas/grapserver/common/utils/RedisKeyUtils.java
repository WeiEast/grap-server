package com.treefinance.saas.grapserver.common.utils;

import com.google.common.base.Joiner;

/**
 * @author haojiahong
 */
public class RedisKeyUtils {

    private final static String PREFIX_KEY = "saas-gateway:%s";
    private final static String PREFIX_LOGIN_LOCK_KEY = "saas-grap-server:login_lock:%s";
    private final static String PREFIX_REDIS_LOCK_KEY = "saas-grap-server:redis_lock";
    private final static String PREFIX_CREATE_TASK_USER_LOCK_KEY = "saas-grap-server:create_task_user_lock";
    private final static String PREFIX_TASK_ACTIVE_TIME_KEY = "saas-grap-server-task-active-time:%s";

    public static String genRedisKey(String key) {
        return String.format(PREFIX_KEY, key);
    }

    /**
     * 获取登录锁redis key
     *
     * @param taskId 任务id
     * @return
     */
    public static String genLoginLockKey(Long taskId) {
        return String.format(PREFIX_LOGIN_LOCK_KEY, taskId);
    }

    public static String genRedisLockKey(Long taskId, String... s) {
        return Joiner.on(":").join(PREFIX_REDIS_LOCK_KEY, taskId, s);
    }

    /**
     * 获取任务最近活跃时间redis key
     *
     * @param taskId 任务id
     * @return
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
     * @return
     */
    public static String genCreateTaskUserLockKey(String appId, String uniqueId, String bizType) {
        return Joiner.on(":").join(PREFIX_CREATE_TASK_USER_LOCK_KEY, appId, uniqueId, bizType);
    }

}
