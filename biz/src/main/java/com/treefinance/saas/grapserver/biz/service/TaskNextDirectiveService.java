package com.treefinance.saas.grapserver.biz.service;

import com.treefinance.commonservice.uid.UidGenerator;
import com.treefinance.saas.grapserver.biz.cache.RedisDao;
import com.treefinance.saas.grapserver.common.model.dto.DirectiveDTO;
import com.treefinance.saas.grapserver.common.utils.JsonUtils;
import com.treefinance.saas.grapserver.dao.entity.TaskNextDirective;
import com.treefinance.saas.grapserver.dao.entity.TaskNextDirectiveCriteria;
import com.treefinance.saas.grapserver.dao.mapper.TaskNextDirectiveMapper;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by luoyihua on 2017/4/26.
 */
@Service
public class TaskNextDirectiveService {
    private static final Logger logger = LoggerFactory.getLogger(TaskNextDirectiveService.class);

    @Autowired
    private TaskNextDirectiveMapper taskNextDirectiveMapper;
    @Autowired
    private RedisDao redisDao;

    private final static int DAY_SECOND = 24 * 60 * 60;

    public String generaRedisKey(Long taskId) {
        return String.format("saas-gateway:nextDirective:%s", taskId);
    }

    /**
     * 添加一条指令记录
     *
     * @param taskId
     * @param directive
     * @return
     */
    public Long insert(Long taskId, String directive, String remark) {
        TaskNextDirective taskNextDirective = new TaskNextDirective();
        long id = UidGenerator.getId();
        taskNextDirective.setId(id);
        taskNextDirective.setTaskId(taskId);
        taskNextDirective.setDirective(directive);
        taskNextDirective.setRemark(remark);
        taskNextDirectiveMapper.insertSelective(taskNextDirective);
        return id;
    }

    public Long insert(Long taskId, String directive) {
        return this.insert(taskId, directive, null);
    }

    /**
     * 查询最近一条指令记录
     *
     * @param taskId
     * @return
     */
    public TaskNextDirective queryRecentDirective(Long taskId) {
        TaskNextDirectiveCriteria criteria = new TaskNextDirectiveCriteria();
        criteria.setOrderByClause("createTime desc");
        TaskNextDirectiveCriteria.Criteria innerCriteria = criteria.createCriteria();
        innerCriteria.andTaskIdEqualTo(taskId);
        List<TaskNextDirective> taskNextDirectiveList = taskNextDirectiveMapper.selectByExample(criteria);
        if (CollectionUtils.isNotEmpty(taskNextDirectiveList)) {
            return taskNextDirectiveList.get(0);
        }
        return null;
    }


    /**
     * 记录并缓存指令
     *
     * @param taskId
     * @param directive
     */
    public void insertAndCacheNextDirective(Long taskId, DirectiveDTO directive) {

        this.insert(taskId, directive.getDirective(), directive.getRemark());

        String content = JsonUtils.toJsonString(directive, "task");
        String key = generaRedisKey(taskId);
        if (redisDao.setEx(key, content, DAY_SECOND, TimeUnit.SECONDS)) {
            logger.info("指令已经放到redis缓存,有效期一天, key={}，content={}", key, content);
        }
    }

    /**
     * 获取指令
     *
     * @param taskId
     * @return
     */
    public String getNextDirective(Long taskId) {
        String key = generaRedisKey(taskId);
        String value = redisDao.get(key);
        if (StringUtils.isNotBlank(value)) {
            return value;
        } else {
            TaskNextDirective taskNextDirective = this.queryRecentDirective(taskId);
            if (taskNextDirective == null) {
                return null;
            }
            DirectiveDTO directiveDTO = new DirectiveDTO();
            directiveDTO.setTaskId(taskNextDirective.getTaskId());
            directiveDTO.setDirective(taskNextDirective.getDirective());
            directiveDTO.setRemark(taskNextDirective.getRemark());
            return JsonUtils.toJsonString(directiveDTO);
        }
    }

    /**
     * 删除指令
     * 数据库中的指令是只插入的,所以这里的删除指插入waiting指令
     *
     * @param taskId
     */
    public void deleteNextDirective(Long taskId) {
        redisDao.deleteKey(generaRedisKey(taskId));
        this.insert(taskId, "waiting", "请等待");
    }

    public void deleteNextDirective(Long taskId, String directive) {
        if (StringUtils.isNotEmpty(directive)) {
            String value = this.getNextDirective(taskId);
            if (StringUtils.isNotEmpty(value)) {
                JSONObject jasonObject = JSONObject.fromObject(value);
                String existDirective = jasonObject.get("directive").toString();
                if (directive.equals(existDirective)) {
                    this.deleteNextDirective(taskId);
                    logger.info("taskId={},下一指令信息={}已删除", taskId, existDirective);
                } else {
                    logger.info("taskId={},需要删除的指令信息={}和缓存的指令信息={}不一致", taskId, directive, existDirective);
                }
            } else {
                logger.info("taskId={},下一指令信息={}不存在", taskId, directive);
            }
        } else {
            this.deleteNextDirective(taskId);
            logger.info("taskId={},下一指令信息已删除", taskId);
        }
    }

    public static void main(String[] args) {
        DirectiveDTO directiveDTO = new DirectiveDTO();
        directiveDTO.setTaskId(directiveDTO.getTaskId());
        directiveDTO.setDirective(directiveDTO.getDirective());
        directiveDTO.setRemark(directiveDTO.getRemark());
    }
}
