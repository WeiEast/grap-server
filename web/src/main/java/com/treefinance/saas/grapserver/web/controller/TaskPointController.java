package com.treefinance.saas.grapserver.web.controller;

import com.treefinance.saas.grapserver.biz.service.TaskBuryPointLogService;
import com.treefinance.saas.grapserver.biz.service.TaskBuryPointSpecialCodeService;
import com.treefinance.saas.grapserver.biz.service.TaskPointService;
import com.treefinance.saas.grapserver.common.enums.CodeEnum;
import com.treefinance.saas.taskcenter.facade.request.TaskPointRequest;
import com.treefinance.toolkit.util.http.servlet.ServletRequests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 张琰佳
 * @since 4:31 PM 2019/1/22
 */
@RestController
@RequestMapping(value = "/taskPoint")
public class TaskPointController {
    @Autowired
    private TaskPointService taskPointService;
    @Autowired
    private TaskBuryPointLogService taskBuryPointLogService;
    @Autowired
    private TaskBuryPointSpecialCodeService taskBuryPointSpecialCodeService;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public void addTaskPoint(@RequestBody TaskPointRequest taskPointRequest, HttpServletRequest httpServletRequest) {
        if (taskPointRequest.getType()!=1){
            taskPointRequest.setIp(ServletRequests.getIP(httpServletRequest));
        }
        taskPointService.addTaskPoint(taskPointRequest);
        String oldCode = CodeEnum.getName(taskPointRequest.getCode());
        if (oldCode != null) {
            taskBuryPointLogService.pushTaskBuryPointLog(taskPointRequest.getTaskId(), taskPointRequest.getAppId(), oldCode);
            taskBuryPointSpecialCodeService.doProcess(oldCode, taskPointRequest.getTaskId(), taskPointRequest.getAppId(), taskPointRequest.getExtra());
        }
    }
}
