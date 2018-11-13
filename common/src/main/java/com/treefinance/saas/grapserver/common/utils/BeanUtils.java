package com.treefinance.saas.grapserver.common.utils;

import org.springframework.cglib.beans.BeanCopier;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author luoyihua on 2017/5/10.
 */
public final class BeanUtils {

    private BeanUtils() {}

    private static final Map<String, BeanCopier> BEAN_COPIER_MAP = new ConcurrentHashMap<>();

    /**
     * 基于CGLIB的bean properties 的拷贝，性能要远优于{@code org.springframework.beans.BeanUtils.copyProperties}
     *
     * @param source    源对象
     * @param target    目标对象
     */
    public static void copyProperties(Object source, Object target) {
        if (source == null || target == null) {
            return;
        }

        String key = String.format("%s:%s", source.getClass().getName(), target.getClass().getName());
        if (!BEAN_COPIER_MAP.containsKey(key)) {
            BeanCopier beanCopier = BeanCopier.create(source.getClass(), target.getClass(), false);
            BEAN_COPIER_MAP.putIfAbsent(key, beanCopier);
        }
        BeanCopier beanCopier = BEAN_COPIER_MAP.get(key);
        beanCopier.copy(source, target, null);
    }

}
