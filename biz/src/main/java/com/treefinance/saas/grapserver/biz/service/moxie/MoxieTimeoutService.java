package com.treefinance.saas.grapserver.biz.service.moxie;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.treefinance.saas.grapserver.biz.service.AppBizTypeService;
import com.treefinance.saas.grapserver.biz.service.TaskLogService;
import com.treefinance.saas.grapserver.biz.service.moxie.directive.MoxieDirectiveService;
import com.treefinance.saas.grapserver.common.enums.ETaskStatus;
import com.treefinance.saas.grapserver.common.enums.ETaskStep;
import com.treefinance.saas.grapserver.common.enums.moxie.EMoxieDirective;
import com.treefinance.saas.grapserver.common.model.dto.moxie.MoxieDirectiveDTO;
import com.treefinance.saas.grapserver.common.utils.CommonUtils;
import com.treefinance.saas.grapserver.common.utils.GrapDateUtils;
import com.treefinance.saas.grapserver.common.utils.JsonUtils;
import com.treefinance.saas.grapserver.dao.entity.AppBizType;
import com.treefinance.saas.grapserver.dao.entity.Task;
import com.treefinance.saas.grapserver.dao.mapper.TaskMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by haojiahong on 2017/9/22.
 */
@Service
public class MoxieTimeoutService {

    private static final Logger logger = LoggerFactory.getLogger(MoxieTimeoutService.class);

    private static String LOGIN_TIME_PREFIX = "saas-grap-server-moxie-login-time:";

    private static int LOGIN_TIME_TIMEOUT = 90;//登录超时时间90s

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private AppBizTypeService appBizTypeService;
    @Autowired
    private TaskLogService taskLogService;
    @Autowired
    private MoxieDirectiveService moxieDirectiveService;

    /**
     * 本地任务缓存
     */
    private final LoadingCache<Long, Task> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .maximumSize(20000)
            .build(CacheLoader.from(taskid -> taskMapper.selectByPrimaryKey(taskid)));


    /**
     * 登录是否超时,即等待魔蝎登录状态接口回调是否超时
     *
     * @param taskId
     * @return
     */
    public boolean isLoginTaskTimeout(Long taskId) {
        String key = LOGIN_TIME_PREFIX + taskId;
        String value = redisTemplate.opsForValue().get(key);
        if (StringUtils.isBlank(value)) {
            logger.info("公积金登录超时,taskId={}登录时间记录key={}已失效,需检查魔蝎登录状态回调接口是否异常", taskId, key);
            return true;
        }
        Date date = GrapDateUtils.getDateByStr(value);
        Date timeoutDate = DateUtils.addSeconds(date, LOGIN_TIME_TIMEOUT);
        Date nowDate = GrapDateUtils.nowDateTime();
        if (nowDate.after(timeoutDate)) {
            logger.info("公积金登录超时,taskId={}登录时间超过{}s,登录时间记录key={},value={},需检查魔蝎登录状态回调接口是否异常",
                    taskId, LOGIN_TIME_TIMEOUT, key, value);
            return true;
        }
        return false;
    }

    /**
     * 记录魔蝎任务创建时间,即开始登录时间.
     *
     * @param taskId
     */
    public void logLoginTime(Long taskId) {
        String key = LOGIN_TIME_PREFIX + taskId;
        redisTemplate.opsForValue().set(key, GrapDateUtils.nowDateTimeStr(), 10, TimeUnit.MINUTES);
    }

    /**
     * 前端收到登录超时后,重置登录时间
     *
     * @param taskId
     */
    public void resetLoginTaskTimeOut(Long taskId) {
        String key = LOGIN_TIME_PREFIX + taskId;
        redisTemplate.opsForValue().set(key, GrapDateUtils.nowDateTimeStr(), 10, TimeUnit.MINUTES);
    }

    public Date getLoginTime(Long taskId) {
        String key = LOGIN_TIME_PREFIX + taskId;
        String value = redisTemplate.opsForValue().get(key);
        if (StringUtils.isBlank(value)) {
            logger.info("公积金登录超时,taskId={}登录时间记录key={}已失效,需检查魔蝎登录状态回调接口是否异常", taskId, key);
            return null;
        }
        return GrapDateUtils.getDateByStr(value);
    }


    public boolean isTaskTimeout(Long taskId) {
        try {
            Task task = cache.get(taskId);
            AppBizType bizType = appBizTypeService.getAppBizType(task.getBizType());
            if (bizType == null || bizType.getTimeout() == null) {
                return false;
            }
            // 超时时间秒
            int timeout = bizType.getTimeout();
            Date loginTime = this.getLoginTime(taskId);
            if (loginTime == null) {
                return false;
            }
            // 未超时: 登录时间+超时时间 < 当前时间
            Date timeoutDate = DateUtils.addSeconds(loginTime, timeout);
            Date current = GrapDateUtils.nowDateTime();
            logger.info("moxie isTaskTimeout: taskId={}，loginTime={},current={},timeout={}",
                    taskId, GrapDateUtils.getDateStrByDate(loginTime), GrapDateUtils.getDateStrByDate(current), timeout);
            if (timeoutDate.after(current)) {
                return false;
            }
        } catch (ExecutionException e) {
            logger.error("taskId={} is not exists...", taskId, e);
        }
        return true;
    }

    @Async
    public void handleTaskTimeout(Long taskId) {
        Task task = null;
        try {
            task = cache.get(taskId);
        } catch (ExecutionException e) {
            logger.error("taskId={} is not exists...", taskId, e);
            return;
        }
        logger.info("handleTaskTimeout async : taskId={}, task={}", taskId, JsonUtils.toJsonString(task));

        Byte taskStatus = task.getStatus();
        if (ETaskStatus.CANCEL.getStatus().equals(taskStatus)
                || ETaskStatus.SUCCESS.getStatus().equals(taskStatus)
                || ETaskStatus.FAIL.getStatus().equals(taskStatus)) {
            logger.info("handleTaskTimeout error : the task is completed: {}", JsonUtils.toJsonString(task));
            return;
        }
        Date loginTime = getLoginTime(taskId);
        AppBizType bizType = appBizTypeService.getAppBizType(task.getBizType());
        if (bizType == null || bizType.getTimeout() == null) {
            return;
        }
        Integer timeout = bizType.getTimeout();
        // 任务超时: 当前时间-登录时间>超时时间
        Date currentTime = new Date();
        Date timeoutDate = DateUtils.addSeconds(loginTime, timeout);
        logger.info("moxie isTaskTimeout: taskid={}，loginTime={},current={},timeout={}",
                taskId, CommonUtils.date2Str(loginTime), CommonUtils.date2Str(currentTime), timeout);
        if (timeoutDate.before(currentTime)) {
            // 增加日志：任务超时
            String errorMessage = "任务超时：当前时间(" + DateFormatUtils.format(currentTime, "yyyy-MM-dd HH:mm:ss")
                    + ") - 登录时间(" + DateFormatUtils.format(loginTime, "yyyy-MM-dd HH:mm:ss")
                    + ")> 超时时间(" + timeout + "秒)";
            taskLogService.logTimeoutTask(task.getId(), errorMessage);

            // 超时处理：任务更新为失败
            MoxieDirectiveDTO directiveDTO = new MoxieDirectiveDTO();
            directiveDTO.setTaskId(task.getId());
            directiveDTO.setDirective(EMoxieDirective.TASK_FAIL.getText());
            Map<String, Object> map = Maps.newHashMap();
            map.put("taskErrorMsg", errorMessage);
            directiveDTO.setRemark(JsonUtils.toJsonString(map));
            moxieDirectiveService.process(directiveDTO);
        }
    }

    public void handleLoginTimeout(Long taskId, String moxieTaskId) {
        //重置登录时间
        this.resetLoginTaskTimeOut(taskId);

        //记录登录超时日志
        Map<String, Object> map = Maps.newHashMap();
        map.put("error", "登录超时");
        map.put("moxieTaskId", moxieTaskId);
        taskLogService.insert(taskId, ETaskStep.LOGIN_FAIL.getText(), new Date(), JsonUtils.toJsonString(map));

    }
}
