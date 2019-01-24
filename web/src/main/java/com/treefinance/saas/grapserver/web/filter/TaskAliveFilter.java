package com.treefinance.saas.grapserver.web.filter;

import com.google.common.collect.Maps;
import com.treefinance.saas.grapserver.biz.service.TaskAliveService;
import com.treefinance.saas.grapserver.exception.TaskTimeOutException;
import com.treefinance.saas.grapserver.context.config.DiamondConfig;
import com.treefinance.saas.knife.result.SimpleResult;
import com.treefinance.toolkit.util.DateUtils;
import com.treefinance.toolkit.util.http.servlet.ServletRequests;
import com.treefinance.toolkit.util.http.servlet.ServletResponses;
import com.treefinance.toolkit.util.json.Jackson;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

/**
 * Buddha Bless , No Bug !
 * 任务是否活跃过滤器
 *
 * @author haojiahong
 * @date 2018/4/24
 */
public class TaskAliveFilter extends AbstractRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(TaskAliveFilter.class);
    private static final String[] TASK_ID_PARAMS = new String[] {"taskId", "taskid"};
    private DiamondConfig diamondConfig;
    private TaskAliveService taskAliveService;

    public TaskAliveFilter(DiamondConfig diamondConfig, TaskAliveService taskAliveService) {
        this.diamondConfig = diamondConfig;
        this.taskAliveService = taskAliveService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            //校验任务是否超时取消
            long taskId = 0;
            for (String param : TASK_ID_PARAMS) {
                String taskIdStr = request.getParameter(param);
                if (StringUtils.isNotBlank(taskIdStr)) {
                    taskId = Long.parseLong(taskIdStr);
                    break;
                }
            }

            if (taskId > 0) {
                String lastActiveTimeStr = taskAliveService.getTaskAliveTime(taskId);
                if (StringUtils.isBlank(lastActiveTimeStr)) {
                    throw new TaskTimeOutException("task canceled taskId=" + taskId);
                }
                Long lastActiveTime = Long.parseLong(lastActiveTimeStr);
                Long now = System.currentTimeMillis();
                long diff = diamondConfig.getTaskMaxAliveTime();
                if (now - lastActiveTime > diff) {
                    throw new TaskTimeOutException("task time out taskId=" + taskId
                            + ",lastActiveTime=" + DateUtils.format(new Date(lastActiveTime))
                            + ",now=" + DateUtils.format(new Date(now)));
                }
                taskAliveService.updateTaskActiveTime(taskId);
            }
            filterChain.doFilter(request, response);
        } catch (TaskTimeOutException e) {
            taskTimeout(request, response, e);
        }
    }

    /**
     * 任务超时取消处理
     */
    private void taskTimeout(HttpServletRequest request, HttpServletResponse response, TaskTimeOutException e) {
        logger.error(String.format("@[%s;%s;%s] >> %s", request.getRequestURI(), request.getMethod(),
                ServletRequests.getIP(request), e.getMessage()));
        Map<String, Integer> map = Maps.newHashMap();
        map.put("mark", 2);
        SimpleResult<Map<String, Integer>> result = new SimpleResult<>(map);
        result.setErrorMsg("任务失效");
        String responseBody = Jackson.toJSONString(result);
        ServletResponses.responseJson(response, 400, responseBody);
    }
}
