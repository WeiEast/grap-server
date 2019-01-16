package com.treefinance.saas.grapserver.facade.service;

import com.treefinance.saas.grapserver.facade.model.TaskRO;
import com.treefinance.saas.knife.result.SaasResult;

import java.util.List;

/**
 * Created by yh-treefinance on 2017/9/11.
 */
@Deprecated
public interface TaskFacade {
    /**
     * 获取当前taskId对应该平台该用户曾经导过的taskId列表
     *
     * @param taskId
     * @return
     */
    SaasResult<List<Long>> getUserTaskIdList(Long taskId);


    /**
     * 通过id获取task记录
     *
     * @param taskId
     * @return
     */
    SaasResult<TaskRO> getById(Long taskId);
}
