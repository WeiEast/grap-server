package com.treefinance.saas.grapserver.common.utils;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.cglib.beans.BeanCopier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 张琰佳
 * @since 3:09 PM 2019/3/11
 */
public class BeanCopyUtils {

    public static <F, T> List<T> convert(List<F> sourceList, Class<T> targetClz) {
        if (CollectionUtils.isNotEmpty(sourceList)) {
            List<T> ret = Lists.newArrayListWithExpectedSize(sourceList.size());
            for (F source : sourceList) {
                ret.add(convert(source, targetClz));
            }
            return ret;
        }
        return null;
    }

    public static <F, T> T convert(F source, Class<T> targetClz) {
        try {
            if (source==null){
                return null;
            }
            T target = targetClz.newInstance();
            copyPropertieBaseCopier(source, target);
            return target;
        } catch (IllegalAccessException | InstantiationException | ExceptionInInitializerError | SecurityException e) {
            throw new RuntimeException("failed to create instance of " + targetClz.getName() + " - " + e.getMessage());
        }
    }

    private static final Map<String, BeanCopier> beanCopierMap = new ConcurrentHashMap<>();

    private static void copyPropertieBaseCopier(Object source, Object target) {
        if (source == null || target == null) {
            target = null;
            return;
        }

        String key = String.format("%s:%s", source.getClass().getName(), target.getClass().getName());
        if (!beanCopierMap.containsKey(key)) {
            BeanCopier beanCopier = BeanCopier.create(source.getClass(), target.getClass(), false);
            beanCopierMap.putIfAbsent(key, beanCopier);
        }
        BeanCopier beanCopier = beanCopierMap.get(key);
        beanCopier.copy(source, target, null);
    }
}