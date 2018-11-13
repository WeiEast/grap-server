package com.treefinance.saas.grapserver.biz.facade;

import com.treefinance.saas.grapserver.common.enums.EBizType;
import com.treefinance.saas.grapserver.common.exception.UnknownException;
import com.treefinance.saas.grapserver.common.utils.DataConverterUtils;
import com.treefinance.saas.grapserver.facade.model.TaskRO;
import com.treefinance.saas.grapserver.facade.service.TaskFacade;
import com.treefinance.saas.knife.result.Results;
import com.treefinance.saas.knife.result.SaasResult;
import com.treefinance.saas.taskcenter.facade.request.TaskRequest;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author haojiahong on 2017/12/22.
 */
@Component("grapTaskFacade")
public class TaskFacadeImpl implements TaskFacade {

    @Autowired
    private com.treefinance.saas.taskcenter.facade.service.TaskFacade taskFacade;

    @Override
    public SaasResult<List<Long>> getUserTaskIdList(Long taskId) {
        TaskResult<List<Long>> rpcResult = taskFacade.getUserTaskIdList(taskId);
        if (!rpcResult.isSuccess()) {
            throw new UnknownException("调用taskcenter失败");
        }
        return Results.newSuccessResult(rpcResult.getData());
    }

    @Override
    public SaasResult<TaskRO> getById(Long taskId) {
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setId(taskId);
        TaskResult<com.treefinance.saas.taskcenter.facade.result.TaskRO> rpcResult = taskFacade.getTaskByPrimaryKey(taskRequest);
        if (!rpcResult.isSuccess()) {
            throw new UnknownException("调用taskcenter失败");
        }
        TaskRO result = DataConverterUtils.convert(rpcResult.getData(), TaskRO.class);
        result.setBizTypeName(EBizType.getName(rpcResult.getData().getBizType()));
        return Results.newSuccessResult(result);
    }

}
