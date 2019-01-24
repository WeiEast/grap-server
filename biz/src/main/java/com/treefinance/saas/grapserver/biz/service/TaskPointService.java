package com.treefinance.saas.grapserver.biz.service;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.taskcenter.facade.request.TaskPointRequest;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;
import com.treefinance.saas.taskcenter.facade.service.TaskPointFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author 张琰佳
 * @since 4:34 PM 2019/1/22
 */
@Service
public class TaskPointService {
    private static final Logger logger = LoggerFactory.getLogger(TaskPointService.class);

    @Resource
    private TaskPointFacade taskPointFacade;

    public void addTaskPoint(Long taskId,String appId,String code,String ip){
        TaskPointRequest taskPointRequest=new TaskPointRequest();
        taskPointRequest.setIp(ip);
        taskPointRequest.setTaskId(taskId);
        taskPointRequest.setAppId(appId);
        taskPointRequest.setCode(code);
        taskPointRequest.setType((byte)0);
        TaskResult<Void> result=taskPointFacade.addTaskPoint(taskPointRequest);
        if (!result.isSuccess()){
            logger.error("埋点记录失败,taskPointRequest={}", JSON.toJSONString(taskPointRequest));
        }
    }
}
