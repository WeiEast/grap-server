package com.treefinance.saas.grapserver.biz.service;

import com.datatrees.rawdatacentral.api.CrawlerService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.treefinance.saas.grapserver.biz.service.directive.DirectiveService;
import com.treefinance.saas.grapserver.common.enums.EDirective;
import com.treefinance.saas.grapserver.common.enums.ETaskStatus;
import com.treefinance.saas.grapserver.common.model.dto.DirectiveDTO;
import com.treefinance.saas.grapserver.common.utils.CommonUtils;
import com.treefinance.saas.grapserver.common.utils.JsonUtils;
import com.treefinance.saas.grapserver.dao.entity.AppBizType;
import com.treefinance.saas.grapserver.dao.entity.Task;
import com.treefinance.saas.grapserver.dao.entity.TaskCriteria;
import com.treefinance.saas.grapserver.dao.mapper.TaskMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 任务时间Service
 * Created by yh-treefinance on 2017/8/3.
 */
@Service
public class TaskTimeService {
    /**
     * logger
     */
    private static final Logger logger = LoggerFactory.getLogger(TaskTimeService.class);
    /**
     * prefix
     */
    private final String prefix = "saas_gateway_task_time:";
    /**
     * taskIds
     */
    private final String taskSetKey = prefix + "login-taskids";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private AppBizTypeService appBizTypeService;

    @Autowired
    private DirectiveService directiveService;

    @Autowired
    private TaskLogService taskLogService;

    @Autowired
    private CrawlerService crawlerService;

    /**
     * 更新登录时间
     *
     * @param taskId
     * @param date
     */
    public void updateLoginTime(Long taskId, Date date) {
        if (taskId == null || date == null) {
            return;
        }
        String key = prefix + taskId;
        redisTemplate.opsForValue().set(key, String.valueOf(date.getTime()), 1, TimeUnit.HOURS);

        redisTemplate.opsForSet().add(taskSetKey, taskId + "");
        logger.info("updateLoginTime : taskId={}  key={}, value={}", taskId, key, DateFormatUtils.format(date, DateFormatUtils.ISO_DATETIME_FORMAT.getPattern()));
    }

    /**
     * 获取登录时间
     *
     * @param taskId
     * @return
     */
    public Date getLoginTime(Long taskId) {
        String key = prefix + taskId;
        String dateTime = redisTemplate.opsForValue().get(key);
        if (StringUtils.isEmpty(dateTime)) {
            return null;
        }
        return new Date(Long.valueOf(dateTime));
    }


    @Scheduled(cron = "0 0/1 * * * ?")
    public void scheduleTaskTimeout() {
        String key = prefix + "scheduling";
        if (redisTemplate.opsForValue().setIfAbsent(key, "1")) {
            try {
                redisTemplate.expire(key, 1, TimeUnit.MINUTES);
                Set<String> taskIdSet = redisTemplate.opsForSet().members(taskSetKey);
                logger.info("scheduleTaskTimeout：running ：lock-key={}, taskid-key={}，taskIds={}", key, taskSetKey, taskIdSet);
                if (CollectionUtils.isEmpty(taskIdSet)) {
                    logger.info("scheduleTaskTimeout：taskIds is empty, key={}", taskSetKey);
                    return;
                }
                List<AppBizType> types = appBizTypeService.getAllAppBizType();
                Map<Byte, Integer> timeoutMap = types.stream().collect(Collectors.toMap(AppBizType::getBizType, AppBizType::getTimeout));

                // 处理超时任务
                List<Long> taskIds = taskIdSet.stream().map(id -> Long.valueOf(id)).collect(Collectors.toList());
                for (List<Long> _taskIds : Lists.partition(taskIds, 200)) {

                    TaskCriteria criteria = new TaskCriteria();
                    criteria.createCriteria().andIdIn(_taskIds);
                    List<Task> tasks = taskMapper.selectByExample(criteria);
                    if (CollectionUtils.isEmpty(tasks)) {
                        continue;
                    }
                    // 清除已经完成任务
                    List<String> completedTaskIds = tasks.stream().filter(task -> !ETaskStatus.RUNNING.getStatus().equals(task.getStatus()))
                            .map(task -> task.getId().toString()).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(completedTaskIds)) {
                        redisTemplate.opsForSet().remove(taskSetKey, completedTaskIds.toArray(new String[]{}));
                        logger.info("scheduleTaskTimeout : task is completed : completedTaskIds={}", completedTaskIds);
                    }

                    List<String> timeoutTaskIds = Lists.newArrayList();
                    // 计算正在运行任务是否有超时
                    tasks.stream().filter(task -> ETaskStatus.RUNNING.getStatus().equals(task.getStatus()))
                            .forEach(task -> {
                                Long taskId = task.getId();
                                if (handleTaskTimeout(task)) {
                                    timeoutTaskIds.add(taskId.toString());
                                }
                            });
                    // 清除已经处理的超时任务
                    if (CollectionUtils.isNotEmpty(timeoutTaskIds)) {
                        redisTemplate.opsForSet().remove(taskSetKey, timeoutTaskIds.toArray(new String[]{}));
                        logger.info("scheduleTaskTimeout : task is timeout : timeoutTaskIds={}", completedTaskIds);
                    }
                }

            } finally {
                redisTemplate.delete(key);
            }
            return;
        }
        redisTemplate.expire(key, 30, TimeUnit.SECONDS);
        logger.info("scheduleTaskTimeout：task is running in other node : key={}", key);
    }

    /**
     * 获取超时时间
     *
     * @param bizType
     * @return
     */
    private Integer getTimeout(Byte bizType) {
        AppBizType appBizType = appBizTypeService.getAppBizType(bizType);
        return appBizType.getTimeout();
    }

    /**
     * 处理超时任务
     *
     * @param taskId
     */
    @Async
    public void handleTaskTimeout(Long taskId) {
        Task task = taskMapper.selectByPrimaryKey(taskId);
        logger.info("handleTaskTimeout async : taskId={}, task={}", taskId, JsonUtils.toJsonString(task));
        if (task != null) {
            handleTaskTimeout(task);
        }
    }


    /**
     * 处理任务超时
     *
     * @param task
     * @return
     */
    public boolean handleTaskTimeout(Task task) {
        Byte taskStatus = task.getStatus();
        if (ETaskStatus.CANCEL.getStatus().equals(taskStatus)
                || ETaskStatus.SUCCESS.getStatus().equals(taskStatus)
                || ETaskStatus.FAIL.getStatus().equals(taskStatus)) {
            logger.info("handleTaskTimeout error : the task is completed: {}", JsonUtils.toJsonString(task));
            return false;
        }
        Long taskId = task.getId();
        Date loginTime = getLoginTime(taskId);
        Integer timeout = getTimeout(task.getBizType());
        // 任务超时: 当前时间-登录时间>超时时间
        Date currentTime = new Date();
        Date timeoutDate = DateUtils.addSeconds(loginTime, timeout);
        logger.info("isTaskTimeout: taskid={}，loginTime={},current={},timeout={}",
                taskId, CommonUtils.date2Str(loginTime), CommonUtils.date2Str(currentTime), timeout);
        if (timeoutDate.before(currentTime)) {
            // 增加日志：任务超时
            String errorMessage = "任务超时：当前时间(" + DateFormatUtils.format(currentTime, "yyyy-MM-dd HH:mm:ss")
                    + ") - 登录时间(" + DateFormatUtils.format(loginTime, "yyyy-MM-dd HH:mm:ss")
                    + ")> 超时时间(" + timeout + "秒)";
            taskLogService.logTimeoutTask(task.getId(), errorMessage);

            // 通知爬数取消任务
            try {
                Map<String, String> extMap = Maps.newHashMap();
                extMap.put("reason", "timeout");
                this.crawlerService.cancel(taskId, extMap);
            } catch (Exception e) {
                logger.error("crawlerService.cancel(" + taskId + ") failed", e);
            }
            // 超时处理：任务更新为失败
            DirectiveDTO directiveDTO = new DirectiveDTO();
            directiveDTO.setTaskId(task.getId());
            directiveDTO.setDirective(EDirective.TASK_FAIL.getText());
            directiveService.process(directiveDTO);

            return true;
        }
        return false;
    }
}
