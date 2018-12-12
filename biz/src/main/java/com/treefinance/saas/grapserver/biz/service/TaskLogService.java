package com.treefinance.saas.grapserver.biz.service;

import com.treefinance.saas.grapserver.biz.domain.TaskLog;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;

import java.util.Date;

/**
 * @author luoyihua on 2017/4/26.
 */
@Service
public interface TaskLogService {

    /**
     * 添加一条日志记录
     */
    Long insert(Long taskId, String msg, Date processTime, String errorMsg);

    /**
     * 根据任务ID查询最新任务日志
     */
    TaskLog queryLatestErrorLog(@Nonnull Long taskId);

    /**
     * 根据任务ID和<code>msg</code>统计任务日志数量
     */
    int countTaskLogsByTaskIdAndMsg(@Nonnull Long taskId, @Nonnull String msg);
}
