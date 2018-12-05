package com.treefinance.saas.grapserver.biz.service.moxie;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.treefinance.saas.grapserver.biz.service.AppBizTypeService;
import com.treefinance.saas.grapserver.common.exception.UnknownException;
import com.treefinance.saas.grapserver.common.utils.DataConverterUtils;
import com.treefinance.saas.grapserver.common.utils.SystemUtils;
import com.treefinance.saas.grapserver.biz.dto.AppBizType;
import com.treefinance.saas.grapserver.biz.dto.Task;
import com.treefinance.saas.taskcenter.facade.request.TaskRequest;
import com.treefinance.saas.taskcenter.facade.result.TaskRO;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;
import com.treefinance.saas.taskcenter.facade.service.MoxieTimeoutFacade;
import com.treefinance.saas.taskcenter.facade.service.TaskFacade;
import com.treefinance.toolkit.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author haojiahong on 2017/9/22.
 */
@Service
public class MoxieTimeoutService {

    private static final Logger logger = LoggerFactory.getLogger(MoxieTimeoutService.class);

    /**
     * 登录超时时间90s
     */
    private static final int LOGIN_TIME_TIMEOUT = 90;

    @Autowired
    private AppBizTypeService appBizTypeService;
    @Autowired
    private TaskFacade taskFacade;
    @Autowired
    private MoxieTimeoutFacade moxieTimeoutFacade;

    /**
     * 本地任务缓存
     */
    private final LoadingCache<Long, Task> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .maximumSize(20000)
            .build(CacheLoader.from(taskId -> {
                TaskRequest taskRequest = new TaskRequest();
                taskRequest.setId(taskId);
                TaskResult<TaskRO> rpcResult = taskFacade.getTaskByPrimaryKey(taskRequest);
                return DataConverterUtils.convert(rpcResult.getData(), Task.class);
            }));


    /**
     * 登录是否超时,即等待魔蝎登录状态接口回调是否超时
     */
    public boolean isLoginTaskTimeout(Long taskId) {
        Date date = this.getLoginTime(taskId);
        if (date == null) {
            logger.info("公积金登录超时,taskId={}未查询到登录时间,需检查魔蝎登录状态回调接口是否异常", taskId);
            return true;
        }
        Date timeoutDate = DateUtils.plusSeconds(date, LOGIN_TIME_TIMEOUT);
        Date nowDate = SystemUtils.now();
        if (nowDate.after(timeoutDate)) {
            logger.info("公积金登录超时,taskId={}登录时间超过{}s,登录时间为value={},需检查魔蝎登录状态回调接口是否异常",
                taskId, LOGIN_TIME_TIMEOUT, DateUtils.format(date));
            return true;
        }
        return false;
    }

    /**
     * 记录魔蝎任务创建时间,即开始登录时间.
     */
    public void logLoginTime(Long taskId) {
        moxieTimeoutFacade.logLoginTime(taskId);
    }

    /**
     * 获取登录时间
     */
    public Date getLoginTime(Long taskId) {
        TaskResult<Date> rpcResult = moxieTimeoutFacade.getLoginTime(taskId);
        if (!rpcResult.isSuccess()) {
            throw new UnknownException("调用taskcenter失败");
        }
        return rpcResult.getData();
    }

    /**
     * 前端收到登录超时后,重置登录时间
     */
    public void resetLoginTaskTimeOut(Long taskId) {
        this.logLoginTime(taskId);
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
            Date timeoutDate = DateUtils.plusSeconds(loginTime, timeout);
            Date current = SystemUtils.now();
            logger.info("moxie isTaskTimeout: taskId={}，loginTime={},current={},timeout={}",
                taskId, DateUtils.format(loginTime), DateUtils.format(current), timeout);
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
        moxieTimeoutFacade.handleTaskTimeout(taskId);
    }

    public void handleLoginTimeout(Long taskId, String moxieTaskId) {
        moxieTimeoutFacade.handleLoginTimeout(taskId, moxieTaskId);
    }

}
