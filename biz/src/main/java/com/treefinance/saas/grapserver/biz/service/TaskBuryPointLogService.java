package com.treefinance.saas.grapserver.biz.service;

import com.treefinance.saas.taskcenter.facade.service.TaskBuryPointLogFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author haojiahong on 2017/8/17.
 */
@Service
public class TaskBuryPointLogService {

    @Autowired
    private TaskBuryPointLogFacade taskBuryPointLogFacade;

    public void pushTaskBuryPointLog(Long taskId, String appId, String code) {
        taskBuryPointLogFacade.pushTaskBuryPointLog(taskId, appId, code);
    }

}
