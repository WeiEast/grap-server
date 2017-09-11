package com.treefinance.saas.grapserver.biz.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.treefinance.commonservice.uid.UidGenerator;
import com.treefinance.saas.grapserver.biz.cache.RedisService;
import com.treefinance.saas.grapserver.dao.entity.TaskNextDirective;
import com.treefinance.saas.grapserver.dao.mapper.TaskNextDirectiveMapper;
import com.treefinance.saas.grapserver.common.enums.EDirective;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by luoyihua on 2017/4/26.
 */
@Service
public class TaskNextDirectiveService {
    private static final Logger logger = LoggerFactory.getLogger(TaskNextDirectiveService.class);

    @Autowired
    private TaskNextDirectiveMapper taskNextDirectiveMapper;
    @Autowired
    private RedisService redisService;
    private final static int DAY_SECOND = 24 * 60 * 60;

    /**
     * 添加一条指令记录
     *
     * @param taskId
     * @param directive
     * @return
     */
    public Long insert(Long taskId, String directive) {
        TaskNextDirective taskNextDirective = new TaskNextDirective();
        long id = UidGenerator.getId();
        taskNextDirective.setId(id);
        taskNextDirective.setTaskId(taskId);
        taskNextDirective.setDirective(directive);
        taskNextDirectiveMapper.insertSelective(taskNextDirective);
        return id;
    }

    public String generaRedisKey(Long taskId) {
        return String.format("saas-gateway:nextDirective:%s", taskId);
    }

    public void deleteNextDirective(Long taskId, String directive) {
        if (StringUtils.isNotEmpty(directive)) {
            String value = redisService.getString(generaRedisKey(taskId));
            if (StringUtils.isNotEmpty(value)) {
                JSONObject jasonObject = JSONObject.fromObject(value);
                Map map = (Map) jasonObject;
                String existDirective = map.get("directive").toString();
                if (directive.equals(existDirective)) {
                    redisService.deleteKey(generaRedisKey(taskId));
                    logger.info("taskId={},下一指令信息={}已删除", taskId, existDirective);
                } else {
                    logger.info("taskId={},需要删除的指令信息={}和缓存的指令信息={}不一致", taskId, directive, existDirective);
                }
            } else {
                logger.info("taskId={},下一指令信息={}不存在", taskId, directive);
            }
        } else {
            redisService.deleteKey(generaRedisKey(taskId));
            logger.info("taskId={},下一指令信息已删除", taskId);
        }
    }

    public String getNextDirective(Long taskId) {
        return redisService.getString(generaRedisKey(taskId));
    }

    public String putNextDirectiveToRedis(Long taskId, String content) {
        String key = generaRedisKey(taskId);
        if (redisService.saveString(key, content, DAY_SECOND)) {
            logger.info("指令已经放到redis缓存,有效期一天, key={}，content={}", key, content);
            return key;
        }
        return null;
    }

    /**
     * 任务是否完成
     *
     * @param taskid
     * @return
     */
    public boolean isTaskCompleted(Long taskid) {
        String content = getNextDirective(taskid);
        Map<String, Object> map = Maps.newHashMap();
        if (StringUtils.isEmpty(content)) {
            return false;
        }
        try {
            Map<String, Object> directiveMessage = JSON.parseObject(content);
            if (directiveMessage == null) {
                return false;
            }
            Object directive = directiveMessage.get("directive");
            if (EDirective.TASK_SUCCESS.getText().equals(directive) ||
                    EDirective.CALLBACK_FAIL.getText().equals(directive) ||
                    EDirective.TASK_FAIL.getText().equals(directive)) {
                return true;
            }
        } catch (Exception e) {
            logger.error("isTaskCompleted(" + taskid + ") error", e);
        }
        return false;
    }

}
