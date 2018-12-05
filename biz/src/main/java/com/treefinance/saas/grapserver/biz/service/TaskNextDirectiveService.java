package com.treefinance.saas.grapserver.biz.service;

import com.treefinance.saas.grapserver.common.exception.UnknownException;
import com.treefinance.saas.grapserver.common.model.dto.DirectiveDTO;
import com.treefinance.saas.grapserver.common.utils.DataConverterUtils;
import com.treefinance.saas.grapserver.biz.dto.TaskNextDirective;
import com.treefinance.saas.taskcenter.facade.request.TaskDirectiveRequest;
import com.treefinance.saas.taskcenter.facade.result.TaskNextDirectiveRO;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;
import com.treefinance.saas.taskcenter.facade.service.TaskNextDirectiveFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author luoyihua on 2017/4/26.
 */
@Service
public class TaskNextDirectiveService {

    @Autowired
    private TaskNextDirectiveFacade taskNextDirectiveFacade;

    /**
     * 添加一条指令记录
     */
    public Long insert(Long taskId, String directive, String remark) {
        TaskResult<Long> rpcResult = taskNextDirectiveFacade.insert(taskId, directive, remark);
        return rpcResult.getData();
    }

    public Long insert(Long taskId, String directive) {
        return this.insert(taskId, directive, null);
    }

    /**
     * 查询最近一条指令记录
     */
    public TaskNextDirective queryRecentDirective(Long taskId) {
        TaskResult<TaskNextDirectiveRO> rpcResult = taskNextDirectiveFacade.queryRecentDirective(taskId);
        if (!rpcResult.isSuccess()) {
            throw new UnknownException("调用taskcenter失败");
        }
        if (rpcResult.getData() == null) {
            return null;
        }
        return DataConverterUtils.convert(rpcResult.getData(), TaskNextDirective.class);
    }


    /**
     * 记录并缓存指令
     */
    public void insertAndCacheNextDirective(Long taskId, DirectiveDTO directive) {
        TaskDirectiveRequest rpcRequest = DataConverterUtils.convert(directive, TaskDirectiveRequest.class);
        taskNextDirectiveFacade.insertAndCacheNextDirective(taskId, rpcRequest);
    }

    /**
     * 获取指令
     */
    public String getNextDirective(Long taskId) {
        TaskResult<String> rpcResult = taskNextDirectiveFacade.getNextDirective(taskId);
        if (!rpcResult.isSuccess()) {
            throw new UnknownException("调用taskcenter失败");
        }
        return rpcResult.getData();
    }

    /**
     * 删除指令
     * 数据库中的指令是只插入的,所以这里的删除指插入waiting指令
     */
    public void deleteNextDirective(Long taskId) {
        taskNextDirectiveFacade.deleteNextDirective(taskId);
    }

    public void deleteNextDirective(Long taskId, String directive) {
        taskNextDirectiveFacade.deleteNextDirective(taskId, directive);
    }

}
