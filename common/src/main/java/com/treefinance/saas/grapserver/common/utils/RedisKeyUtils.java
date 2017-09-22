package com.treefinance.saas.grapserver.common.utils;

public class RedisKeyUtils {
    private final static String PREFIX_KEY = "saas-gateway:%s";

    public static String genRedisKey(String key) {
        return String.format(PREFIX_KEY, key);
    }
}
