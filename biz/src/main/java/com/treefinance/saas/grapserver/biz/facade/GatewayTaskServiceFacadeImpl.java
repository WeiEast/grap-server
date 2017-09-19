package com.treefinance.saas.grapserver.biz.facade;

import com.google.common.collect.Lists;
import com.treefinance.saas.assistant.httpinvoker.utils.CollectionUtils;
import com.treefinance.saas.gateway.servicefacade.TaskService;
import com.treefinance.saas.gateway.servicefacade.enums.BizTypeEnum;
import com.treefinance.saas.gateway.servicefacade.model.TaskDTO;
import com.treefinance.saas.grapserver.common.utils.DataConverterUtils;
import com.treefinance.saas.grapserver.dao.entity.Task;
import com.treefinance.saas.grapserver.dao.entity.TaskCriteria;
import com.treefinance.saas.grapserver.dao.mapper.TaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 兼容老的任务服务
 * Created by yh-treefinance on 2017/9/19.
 */
@Service("gatewayTaskServiceFacade")
public class GatewayTaskServiceFacadeImpl implements TaskService {

    @Autowired
    private TaskMapper taskMapper;

    @Override
    public List<Long> getUserTaskIdList(Long taskId) {
        Task task = taskMapper.selectByPrimaryKey(taskId);
        TaskCriteria taskCriteria = new TaskCriteria();
        taskCriteria.createCriteria()
                .andUniqueIdEqualTo(task.getUniqueId())
                .andAppIdEqualTo(task.getAppId())
                .andBizTypeEqualTo(task.getBizType());
        List<Long> list = Lists.newArrayList();
        List<Task> tasks = taskMapper.selectByExample(taskCriteria);
        if (CollectionUtils.isNotEmpty(tasks)) {
            list = tasks.stream().map(s -> s.getId()).collect(Collectors.toList());
        }
        return list;
    }

    @Override
    public TaskDTO getById(Long taskId) {
        Task task = taskMapper.selectByPrimaryKey(taskId);
        if (task == null) {
            return null;
        }
        TaskDTO result = DataConverterUtils.convert(task, TaskDTO.class);
        for (BizTypeEnum type : BizTypeEnum.values()) {
            if (BizTypeEnum.valueOfType(type).equals(task.getBizType())) {
                result.setBizTypeEnum(type);
            }
        }
        return null;
    }
}
