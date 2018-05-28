package com.treefinance.saas.grapserver.biz.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.treefinance.commonservice.uid.UidGenerator;
import com.treefinance.saas.grapserver.dao.entity.TaskBuryPointLog;
import com.treefinance.saas.grapserver.dao.entity.TaskBuryPointLogCriteria;
import com.treefinance.saas.grapserver.dao.mapper.TaskBuryPointLogMapper;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by haojiahong on 2017/8/17.
 */
@Service
public class TaskBuryPointLogService {

    private static final Logger logger = LoggerFactory.getLogger(TaskBuryPointLogService.class);

    private final ConcurrentLinkedQueue<TaskBuryPointLog> logQueue = new ConcurrentLinkedQueue<>();

    @Autowired
    protected TaskBuryPointLogMapper taskBuryPointLogMapper;

    @Scheduled(fixedRate = 1000)
    public void insert() {
        try {
            List<TaskBuryPointLog> list = Lists.newArrayList();
            int i = 1;
            while (!logQueue.isEmpty() && i <= 20) {
                TaskBuryPointLog log = logQueue.poll();
                if (log != null) {
                    list.add(log);
                }
                i++;
            }
            logger.debug("batchInsert taskBuryPointLog: list={}", JSON.toJSONString(list));
            try {
                Thread.sleep(2 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (CollectionUtils.isEmpty(list)) {
                return;
            }
            try {
                taskBuryPointLogMapper.batchInsert(list);
            } catch (Exception e) {
                logger.error("batchInsert taskBuryPointLog error: list={}", JSON.toJSONString(list), e);
                // 失败重新放入队列,埋点信息并不是很重要,去掉重试.
                //            logQueue.addAll(list);
            }
        } finally {
            logger.info("batchInsert taskBuryPointLog finished");
        }

    }

    public void pushTaskBuryPointLog(Long taskId, String appId, String code) {
        TaskBuryPointLog log = new TaskBuryPointLog();
        log.setId(UidGenerator.getId());
        log.setTaskId(taskId);
        log.setAppId(appId);
        log.setCode(code);
        log.setCreateTime(new Date());
        logQueue.offer(log);
    }

    public List<TaskBuryPointLog> queryTaskBuryPointLogByCode(Long taskId, String... codes) {
        TaskBuryPointLogCriteria criteria = new TaskBuryPointLogCriteria();
        TaskBuryPointLogCriteria.Criteria _criteria = criteria.createCriteria();
        _criteria.andTaskIdEqualTo(taskId);
        if (codes != null && codes.length >= 0) {
            _criteria.andCodeIn(Arrays.asList(codes));
        }
        List<TaskBuryPointLog> list = taskBuryPointLogMapper.selectByExample(criteria);
        if (CollectionUtils.isEmpty(list)) {
            return Lists.newArrayList();
        }
        return list;
    }
}
