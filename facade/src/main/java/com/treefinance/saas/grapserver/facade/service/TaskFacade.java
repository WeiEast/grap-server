package com.treefinance.saas.grapserver.facade.service;

import com.treefinance.saas.grapserver.facade.model.TaskRO;

import java.util.List;

/**
 * Created by yh-treefinance on 2017/9/11.
 */
public interface TaskFacade {
    /**
     * 获取当前taskId对应该平台该用户曾经导过的taskId列表
     *
     * @param taskId
     * @return
     */
    List<Long> getUserTaskIdList(Long taskId);



    /**
     * 通过id获取task记录
     *
     * @param taskId
     * @return
     */
    TaskRO getById(Long taskId);
}
