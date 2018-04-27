package com.treefinance.saas.grapserver.web.filter;

import com.alibaba.fastjson.JSON;
import com.datatrees.toolkits.util.http.servlet.ServletRequestUtils;
import com.datatrees.toolkits.util.http.servlet.ServletResponseUtils;
import com.datatrees.toolkits.util.json.Jackson;
import com.google.common.collect.Maps;
import com.treefinance.saas.grapserver.biz.config.DiamondConfig;
import com.treefinance.saas.grapserver.common.exception.TaskTimeOutException;
import com.treefinance.saas.grapserver.common.utils.GrapDateUtils;
import com.treefinance.saas.grapserver.common.utils.RedisKeyUtils;
import com.treefinance.saas.knife.result.SimpleResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

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
    private DiamondConfig diamondConfig;
    private RedisTemplate<String, String> redisTemplate;

    public TaskAliveFilter(DiamondConfig diamondConfig,
                           RedisTemplate<String, String> redisTemplate) {
        this.diamondConfig = diamondConfig;
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            //校验任务是否超时取消
            String taskIdStr = request.getParameter("taskId");
            String taskidStr = request.getParameter("taskid");
            long taskId = 0;
            if (StringUtils.isNotBlank(taskIdStr)) {
                taskId = Long.parseLong(taskIdStr);
            }
            if (StringUtils.isNotBlank(taskidStr)) {
                taskId = Long.parseLong(taskidStr);
            }
            if (taskId > 0) {
                String key = RedisKeyUtils.genTaskActiveTimeKey(taskId);
                String lastActiveTimeStr = redisTemplate.opsForValue().get(key);
                if (StringUtils.isBlank(lastActiveTimeStr)) {
                    throw new TaskTimeOutException("task finished taskId=" + taskId);
                }
                Long lastActiveTime = Long.parseLong(lastActiveTimeStr);
                Long now = System.currentTimeMillis();
                long diff = diamondConfig.getTaskMaxAliveTime();
                if (now - lastActiveTime > diff) {
                    throw new TaskTimeOutException("task time out taskId=" + taskId
                            + ",lastActiveTime=" + GrapDateUtils.getDateStrByDate(new Date(lastActiveTime))
                            + ",now=" + GrapDateUtils.getDateStrByDate(new Date(now)));
                }
                redisTemplate.opsForValue().set(key, now + "");
            }
            filterChain.doFilter(request, response);
        } catch (TaskTimeOutException e) {
            taskTimeout(request, response, e);
        } finally {
            logger.info("{} of {} : params={}", request.getMethod(), request.getRequestURL(),
                    JSON.toJSONString(request.getParameterMap()));
        }
    }

    /**
     * 任务超时取消处理
     *
     * @param request
     * @param response
     * @param e
     */
    private void taskTimeout(HttpServletRequest request, HttpServletResponse response, TaskTimeOutException e) {
        logger.error(String.format("@[%s;%s;%s] >> %s", request.getRequestURI(), request.getMethod(),
                ServletRequestUtils.getIP(request), e.getMessage()));
        Map<String, Integer> map = Maps.newHashMap();
        map.put("mark", 2);
        SimpleResult<Map<String, Integer>> result = new SimpleResult<>(map);
        result.setErrorMsg("任务失效");
        String responseBody = Jackson.toJSONString(result);
        ServletResponseUtils.responseJson(response, 400, responseBody);
    }
}
