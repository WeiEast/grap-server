package com.treefinance.saas.grapserver.web.controller;

import com.treefinance.saas.grapserver.biz.service.TaskBuryPointLogService;
import com.treefinance.saas.grapserver.biz.service.TaskBuryPointSpecialCodeService;
import com.treefinance.saas.grapserver.biz.service.TaskPointService;
import com.treefinance.saas.grapserver.common.enums.CodeEnum;
import com.treefinance.toolkit.util.http.servlet.ServletRequests;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;

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
    public void addTaskPoint(@RequestParam("taskid") Long taskId,
        @RequestParam("appid") String appId,
        @RequestParam("code") String code,
        @RequestParam(value = "extra", required = false) String extra, HttpServletRequest httpServletRequest) {
        if (taskId == null) {
            throw new ValidationException("参数taskid为空");
        }
        if (StringUtils.isBlank(appId)) {
            throw new ValidationException("参数appid为空");
        }
        if (StringUtils.isBlank(code)) {
            throw new ValidationException("参数code为空");
        }
        String ip=ServletRequests.getIP(httpServletRequest);
        taskPointService.addTaskPoint(taskId,appId,code,ip);
        String oldCode = CodeEnum.getName(code);
        if (oldCode != null) {
            taskBuryPointLogService.pushTaskBuryPointLog(taskId, appId, oldCode);
            taskBuryPointSpecialCodeService.doProcess(oldCode, taskId, appId, extra);
        }
    }
}
