package com.treefinance.saas.grapserver.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author yh-treefinance on 2017/7/6.
 */
public class JsonUtils {

    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);

    /**
     * 转JSON，剔除某些字段
     */
    public static String toJsonString(Object obj, String... excludeProperties) {
        return JSON.toJSONString(obj, (PropertyFilter) (obj1, name, value) -> {
            if (excludeProperties != null && excludeProperties.length > 0) {
                for (String property : excludeProperties) {
                    if (name.equalsIgnoreCase(property)) {
                        return false;
                    }
                }
            }
            return true;
        });
    }

    public static <T> List<T> toJavaBeanList(String jsonStr, Class<T> cls) {
        List<T> list = new ArrayList<T>();
        try {
            list = JSON.parseArray(jsonStr, cls);
        } catch (Exception e) {
            logger.error("handle json string error:", e);
        }
        return list;
    }

    public static <T> T toJavaBean(String jsonString, Class<T> cls) {
        T t = null;
        try {
            t = JSON.parseObject(jsonString, cls);
        } catch (Exception e) {
            logger.error("handle json string error:", e);
        }
        return t;
    }


    public static Object toJsonObject(String jsonStr) {
        return JSONObject.parseObject(jsonStr);
    }

    public static Object[] toJsonObjects(String jsonStr) {
        JSONArray jsonArray = JSON.parseArray(jsonStr);
        int size = jsonArray.size();
        Object[] objects = new Object[size];
        for (int i = 0; i < size; i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            objects[i] = jsonObject;
        }
        return objects;
    }

    public static List<Object> toJsonObjectList(String jsonStr) {
        Object[] objects = JsonUtils.toJsonObjects(jsonStr);
        return new ArrayList<>(Arrays.asList(objects));
    }

    public static <K, V> Map<K, V> toMap(String jsonStr, Class<K> kClass, Class<V> vClass) {
        return JSON.parseObject(jsonStr, new TypeReference<Map<K, V>>() {});
    }

}
