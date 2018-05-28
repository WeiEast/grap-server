package com.treefinance.saas.grapserver.biz.service.elasticjob;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.treefinance.saas.assistant.model.Constants;
import com.treefinance.saas.grapserver.biz.service.thread.TaskActiveTimeoutThread;
import com.treefinance.saas.grapserver.common.enums.ETaskStatus;
import com.treefinance.saas.grapserver.dao.entity.Task;
import com.treefinance.saas.grapserver.dao.entity.TaskCriteria;
import com.treefinance.saas.grapserver.dao.mapper.TaskMapper;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Date;
import java.util.List;

/**
 * Good Luck Bro , No Bug !
 *
 * @author haojiahong
 * @date 2018/5/28
 */
public class TaskActiveTimeoutJob implements SimpleJob {

    private static final Logger logger = LoggerFactory.getLogger(TaskActiveTimeoutJob.class);

    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private ThreadPoolTaskExecutor threadPoolExecutor;

    @Override
    public void execute(ShardingContext shardingContext) {
        long start = System.currentTimeMillis();
        try {
            Date startTime = new Date();
            Date endTime = DateUtils.addMinutes(startTime, -60);
            TaskCriteria criteria = new TaskCriteria();
            criteria.createCriteria().andStatusEqualTo(ETaskStatus.RUNNING.getStatus())
                    .andSaasEnvEqualTo(Byte.parseByte(Constants.SAAS_ENV_VALUE))
                    .andCreateTimeGreaterThanOrEqualTo(endTime)
                    .andCreateTimeLessThan(startTime);

            List<Task> taskList = taskMapper.selectByExample(criteria);
            for (Task task : taskList) {
                threadPoolExecutor.execute(new TaskActiveTimeoutThread(task, startTime));
            }
        } finally {
            logger.info("任务活跃时间超时定时任务执行耗时{}ms", System.currentTimeMillis() - start);
        }
    }
}
