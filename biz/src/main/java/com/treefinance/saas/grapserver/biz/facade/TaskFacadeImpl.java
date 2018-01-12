package com.treefinance.saas.grapserver.biz.facade;

import com.google.common.collect.Lists;
import com.treefinance.saas.assistant.httpinvoker.utils.CollectionUtils;
import com.treefinance.saas.grapserver.common.enums.EBizType;
import com.treefinance.saas.grapserver.common.utils.DataConverterUtils;
import com.treefinance.saas.grapserver.dao.entity.Task;
import com.treefinance.saas.grapserver.dao.entity.TaskCriteria;
import com.treefinance.saas.grapserver.dao.mapper.TaskMapper;
import com.treefinance.saas.grapserver.facade.model.TaskRO;
import com.treefinance.saas.grapserver.facade.service.TaskFacade;
import com.treefinance.saas.knife.result.Results;
import com.treefinance.saas.knife.result.SaasResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by haojiahong on 2017/12/22.
 */
@Component("taskFacade")
public class TaskFacadeImpl implements TaskFacade {
    @Autowired
    private TaskMapper taskMapper;

    @Override
    public SaasResult<List<Long>> getUserTaskIdList(Long taskId) {
        Task task = taskMapper.selectByPrimaryKey(taskId);
        TaskCriteria taskCriteria = new TaskCriteria();
        taskCriteria.createCriteria()
                .andUniqueIdEqualTo(task.getUniqueId())
                .andAppIdEqualTo(task.getAppId())
                .andBizTypeEqualTo(task.getBizType());
        List<Long> list = Lists.newArrayList();
        List<Task> tasks = taskMapper.selectByExample(taskCriteria);
        if (CollectionUtils.isNotEmpty(tasks)) {
            list = tasks.stream().map(Task::getId).collect(Collectors.toList());
        }
        return Results.newSuccessResult(list);
    }

    @Override
    public SaasResult<TaskRO> getById(Long taskId) {
        Task task = taskMapper.selectByPrimaryKey(taskId);
        if (task == null) {
            return null;
        }

        TaskRO result = DataConverterUtils.convert(task, TaskRO.class);
        result.setBizTypeName(EBizType.getName(task.getBizType()));
        return Results.newSuccessResult(result);
    }
}