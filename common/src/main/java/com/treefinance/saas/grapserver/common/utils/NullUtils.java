package com.treefinance.saas.grapserver.common.utils;

/**
 * Created by yh-treefinance on 2018/2/7.
 */
public class NullUtils {

    /**
     * null 值判断
     *
     * @param value
     * @param defaultValue
     * @param <T>
     * @return
     */
    public static  <T> T ifNull(T value, T defaultValue) {
        return value == null ? defaultValue : value;
    }
}
