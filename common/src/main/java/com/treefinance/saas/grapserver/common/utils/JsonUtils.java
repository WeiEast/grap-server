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
 * Created by yh-treefinance on 2017/7/6.
 */
public class JsonUtils {

    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);


    /**
     * 转JSON，剔除某些字段
     *
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
        JSONObject jsonObject = JSONObject.parseObject(jsonStr);
        return jsonObject;
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
        List<Object> objectList = Lists.newArrayList();
        Object[] objects = JsonUtils.toJsonObjects(jsonStr);
        objectList = new ArrayList<>(Arrays.asList(objects));
        return objectList;
    }

    public static <K, V> Map<K, V> toMap(String jsonStr, Class<K> kClass, Class<V> vClass) {
        Map<K, V> map = JSON.parseObject(jsonStr, new TypeReference<Map<K, V>>() {
        });
        return map;
    }

    public static void main(String[] args) {
//        String str = "{\"test1\":{\"name\":\"zhangsan\"},\"test2\":{\"name\":\"lisi\"},\"test3\":{\"name\":\"wanger\"}}";
        String str = "{\"test1\":\"zhangsan\",\"test2\":\"lisi\",\"test3\":\"wanger\"}";
        Map<String, String> map = JsonUtils.toMap(str, String.class, String.class);
        System.out.println(map);

    }
}
