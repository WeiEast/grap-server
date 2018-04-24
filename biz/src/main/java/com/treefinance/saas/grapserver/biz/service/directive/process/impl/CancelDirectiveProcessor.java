package com.treefinance.saas.grapserver.biz.service.directive.process.impl;

import com.datatrees.rawdatacentral.api.CrawlerService;
import com.google.common.collect.Maps;
import com.treefinance.saas.grapserver.biz.common.AsycExcutor;
import com.treefinance.saas.grapserver.biz.service.directive.process.AbstractDirectiveProcessor;
import com.treefinance.saas.grapserver.biz.service.monitor.MonitorService;
import com.treefinance.saas.grapserver.common.enums.EDirective;
import com.treefinance.saas.grapserver.common.enums.ETaskStatus;
import com.treefinance.saas.grapserver.common.model.dto.DirectiveDTO;
import com.treefinance.saas.grapserver.common.model.dto.TaskDTO;
import com.treefinance.saas.grapserver.common.utils.RedisKeyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 取消任务执行
 * Created by yh-treefinance on 2017/7/10.
 */
@Component
public class CancelDirectiveProcessor extends AbstractDirectiveProcessor {
    @Autowired
    protected MonitorService monitorService;
    @Autowired
    private AsycExcutor asycExcutor;
    @Autowired
    private CrawlerService crawlerService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    protected void doProcess(EDirective directive, DirectiveDTO directiveDTO) {
        TaskDTO taskDTO = directiveDTO.getTask();
        taskDTO.setStatus(ETaskStatus.CANCEL.getStatus());
        // 取消任务
        taskService.cancelTaskWithStep(taskDTO.getId());
        Map<String, String> extMap = Maps.newHashMap();
        extMap.put("reason", "user");
        crawlerService.cancel(taskDTO.getId(), extMap);
        //删除记录任务活跃时间redisKey
        deleteTaskAliveRedisKey(taskDTO.getId());
        monitorService.sendMonitorMessage(taskDTO);

        // 异步触发触发回调
        asycExcutor.runAsyc(directiveDTO, _directiveDTO -> callback(_directiveDTO));
    }

    private void deleteTaskAliveRedisKey(Long taskId) {
        String key = RedisKeyUtils.genTaskActiveTimeKey(taskId);
        redisTemplate.delete(key);
        logger.info("删除记录任务活跃时间redisKey, taskId={}", taskId);
    }


}
