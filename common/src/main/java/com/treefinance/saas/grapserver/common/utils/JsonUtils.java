package com.treefinance.saas.grapserver.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.PropertyFilter;

/**
 * Created by yh-treefinance on 2017/7/6.
 */
public class JsonUtils {

    /**
     * 转JSON，剔除某些字段
     * @param obj
     * @param excludeProperties
     * @return
     */
    public static String toJsonString(Object obj, String... excludeProperties) {

        return JSON.toJSONString(obj, new PropertyFilter() {
            @Override
            public boolean apply(Object obj, String name, Object value) {
                if (excludeProperties != null && excludeProperties.length > 0) {
                    for (String property : excludeProperties) {
                        if (name.equalsIgnoreCase(property)) {
                            return false;
                        }
                    }
                }
                return true;
            }
        });
    }
}
