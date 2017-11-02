package com.treefinance.saas.grapserver.biz.service;

import com.treefinance.saas.grapserver.biz.processor.tasklog.operator.OperatorTaskLogSpecialProcessor;
import com.treefinance.saas.grapserver.biz.processor.tasklog.operator.OperatorTaskLogSpecialRequest;
import com.treefinance.saas.grapserver.common.enums.EBizType;
import com.treefinance.saas.grapserver.common.model.dto.TaskDTO;
import com.treefinance.saas.grapserver.common.utils.BeanUtils;
import com.treefinance.saas.grapserver.dao.entity.Task;
import com.treefinance.saas.grapserver.dao.entity.TaskCriteria;
import com.treefinance.saas.grapserver.dao.mapper.TaskMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by haojiahong on 2017/10/30.
 */
@Service
public class TaskLogSpecialService {

    private final static Logger logger = LoggerFactory.getLogger(TaskLogSpecialService.class);

    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private OperatorTaskLogSpecialProcessor operatorTaskLogSpecialProcessor;


    @Async
    public void doProcess(Long taskId, String msg, Date processTime) {
        TaskCriteria taskCriteria = new TaskCriteria();
        taskCriteria.createCriteria().andIdEqualTo(taskId);
        List<Task> taskList = taskMapper.selectByExample(taskCriteria);
        if (CollectionUtils.isEmpty(taskList)) {
            logger.error("根据taskId={}未查到task信息", taskId);
            return;
        }
        Task task = taskList.get(0);
        TaskDTO taskDTO = new TaskDTO();
        BeanUtils.copyProperties(task, taskDTO);
        if (EBizType.OPERATOR.getCode().equals(task.getBizType())) {
            OperatorTaskLogSpecialRequest request = new OperatorTaskLogSpecialRequest();
            request.setTaskDTO(taskDTO);
            request.setMsg(msg);
            request.setProcessTime(processTime);
            operatorTaskLogSpecialProcessor.doService(request);
        }

    }
}
