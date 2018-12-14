package com.treefinance.saas.grapserver.facade.impl;

import com.treefinance.saas.grapserver.common.enums.EBizType;
import com.treefinance.saas.grapserver.context.component.AbstractFacade;
import com.treefinance.saas.grapserver.facade.model.TaskRO;
import com.treefinance.saas.grapserver.facade.service.TaskFacade;
import com.treefinance.saas.grapserver.manager.TaskManager;
import com.treefinance.saas.grapserver.manager.domain.TaskBO;
import com.treefinance.saas.knife.result.Results;
import com.treefinance.saas.knife.result.SaasResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author haojiahong on 2017/12/22.
 */
@Component("grapTaskFacade")
public class TaskFacadeImpl extends AbstractFacade implements TaskFacade {

    @Autowired
    private TaskManager taskManager;

    @Override
    public SaasResult<List<Long>> getUserTaskIdList(Long taskId) {
        List<Long> taskIds = taskManager.listTaskIdsWithSameTrigger(taskId);
        return Results.newSuccessResult(taskIds);
    }

    @Override
    public SaasResult<TaskRO> getById(Long taskId) {
        TaskBO task = taskManager.getTaskById(taskId);
        TaskRO result = convertStrict(task, TaskRO.class);
        result.setBizTypeName(EBizType.getName(task.getBizType()));
        return Results.newSuccessResult(result);
    }

}
