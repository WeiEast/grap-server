package com.treefinance.saas.grapserver.dao.mapper;

import com.treefinance.saas.grapserver.dao.entity.TaskAttribute;

/**
 * Created by haojiahong on 2017/9/15.
 */
public interface TaskAttributeUpdateMapper {
    /**
     * 插入更新
     *
     * @param record
     * @return
     */
    int insertOrUpdateSelective(TaskAttribute record);
}
