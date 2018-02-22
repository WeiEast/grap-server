package com.treefinance.saas.grapserver.common.utils;

import com.google.common.base.Joiner;

/**
 * @author haojiahong
 */
public class RedisKeyUtils {

    private final static String PREFIX_KEY = "saas-gateway:%s";
    private final static String PREFIX_LOGIN_LOCK_KEY = "saas-grap-server:login_lock:%s";
    private final static String PREFIX_REDIS_LOCK_KEY = "saas-grap-server:redis_lock";

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

}
