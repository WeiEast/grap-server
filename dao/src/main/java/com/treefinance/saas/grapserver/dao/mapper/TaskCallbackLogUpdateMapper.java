package com.treefinance.saas.grapserver.dao.mapper;

import com.treefinance.saas.grapserver.dao.entity.TaskCallbackLog;

public interface TaskCallbackLogUpdateMapper {
    /**
     * 插入更新
     *
     * @param record
     * @return
     */
    int insertOrUpdateSelective(TaskCallbackLog record);

}