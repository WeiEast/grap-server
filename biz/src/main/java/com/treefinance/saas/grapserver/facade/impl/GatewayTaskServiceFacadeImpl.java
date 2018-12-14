package com.treefinance.saas.grapserver.facade.impl;

import com.treefinance.saas.gateway.servicefacade.TaskService;
import com.treefinance.saas.gateway.servicefacade.enums.BizTypeEnum;
import com.treefinance.saas.gateway.servicefacade.model.TaskDTO;
import com.treefinance.saas.grapserver.context.component.AbstractFacade;
import com.treefinance.saas.grapserver.manager.TaskManager;
import com.treefinance.saas.grapserver.manager.domain.TaskBO;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 兼容老的任务服务
 * @author yh-treefinance on 2017/9/19.
 */
@Service("gatewayTaskServiceFacade")
public class GatewayTaskServiceFacadeImpl extends AbstractFacade implements TaskService {

    @Autowired
    private TaskManager taskManager;

    @Override
    public List<Long> getUserTaskIdList(Long taskId) {
        return taskManager.listTaskIdsWithSameTrigger(taskId);
    }

    @Override
    public TaskDTO getById(Long taskId) {
        TaskBO task = taskManager.getTaskById(taskId);
        TaskDTO result = convertStrict(task, TaskDTO.class);
        for (BizTypeEnum type : BizTypeEnum.values()) {
            if (BizTypeEnum.valueOfType(type).equals(task.getBizType())) {
                result.setBizTypeEnum(type);
            }
        }
        return result;
    }

}
