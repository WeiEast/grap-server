package com.treefinance.saas.grapserver.biz.facade;

import com.treefinance.saas.gateway.servicefacade.TaskService;
import com.treefinance.saas.gateway.servicefacade.enums.BizTypeEnum;
import com.treefinance.saas.gateway.servicefacade.model.TaskDTO;
import com.treefinance.saas.grapserver.common.exception.UnknownException;
import com.treefinance.saas.grapserver.common.utils.DataConverterUtils;
import com.treefinance.saas.grapserver.biz.dto.Task;
import com.treefinance.saas.taskcenter.facade.request.TaskRequest;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;
import com.treefinance.saas.taskcenter.facade.service.TaskFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 兼容老的任务服务
 * @author yh-treefinance on 2017/9/19.
 */
@Service("gatewayTaskServiceFacade")
public class GatewayTaskServiceFacadeImpl implements TaskService {

    @Autowired
    private TaskFacade taskFacade;

    @Override
    public List<Long> getUserTaskIdList(Long taskId) {
        TaskResult<List<Long>> rpcResult = taskFacade.getUserTaskIdList(taskId);
        if (!rpcResult.isSuccess()) {
            throw new UnknownException("调用taskcenter失败");
        }
        return rpcResult.getData();
    }

    @Override
    public TaskDTO getById(Long taskId) {
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setId(taskId);
        TaskResult<com.treefinance.saas.taskcenter.facade.result.TaskRO> rpcResult = taskFacade.getTaskByPrimaryKey(taskRequest);
        if (!rpcResult.isSuccess()) {
            throw new UnknownException("调用taskcenter失败");
        }
        Task task = DataConverterUtils.convert(rpcResult.getData(), Task.class);
        TaskDTO result = DataConverterUtils.convert(task, TaskDTO.class);
        for (BizTypeEnum type : BizTypeEnum.values()) {
            if (BizTypeEnum.valueOfType(type).equals(task.getBizType())) {
                result.setBizTypeEnum(type);
            }
        }
        return result;
    }

}
