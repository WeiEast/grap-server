package com.treefinance.saas.grapserver.biz.service;

import com.google.common.base.Splitter;
import com.treefinance.saas.assistant.model.Constants;
import com.treefinance.saas.grapserver.common.exception.ParamsCheckException;
import com.treefinance.saas.grapserver.common.exception.UniqueidMaxException;
import com.treefinance.saas.grapserver.context.config.DiamondConfig;
import com.treefinance.saas.grapserver.manager.TaskManager;
import com.treefinance.saas.grapserver.manager.param.TaskParams;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.xml.bind.ValidationException;

import java.util.List;

/**
 * @author guoguoyun
 * @date Created in 2018/11/1下午3:32
 */
@Service("saasTaskService")
public class SaasTaskService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private DiamondConfig diamondConfig;
    @Autowired
    private TaskManager taskManager;

    /**
     * 创建任务
     */
    public Long createTask(String uniqueId, String appId, Byte bizType, String extra, String website, String source)
        throws ValidationException {
        if (StringUtils.isBlank(appId)) {
            throw new ValidationException("appid为空");
        }
        if (bizType == null) {
            logger.error("创建任务时传入的bizType为null");
            throw new ParamsCheckException("bizType为null");
        }
        // 校验uniqueId
        String excludeAppId = diamondConfig.getExcludeAppId();
        if (StringUtils.isNotEmpty(excludeAppId)) {
            List<String> excludeAppIdList = Splitter.on(",").trimResults().splitToList(excludeAppId);
            if (!excludeAppIdList.contains(appId)) {
                checkUniqueId(uniqueId, appId, bizType);
            }
        } else {
            checkUniqueId(uniqueId, appId, bizType);
        }

        TaskParams params = new TaskParams();
        params.setUniqueId(uniqueId);
        params.setAppId(appId);
        params.setBizType(bizType);
        params.setSource(source);
        params.setWebsite(website);
        params.setSaasEnv(Byte.valueOf(Constants.SAAS_ENV_VALUE));
        params.setExtra(extra);

        return taskManager.createTask(params);
    }

    /**
     * uniqueId校验
     */
    private void checkUniqueId(String uniqueId, String appId, Byte bizType) {
        Integer maxCount = diamondConfig.getMaxCount();
        if (maxCount != null) {
            String key = keyOfUniqueId(uniqueId, appId, bizType);
            Long count = redisTemplate.opsForSet().size(key);
            if (count != null && count >= maxCount) {
                throw new UniqueidMaxException();
            }
        }
    }

    private String keyOfUniqueId(String uniqueId, String appId, Byte bizType) {
       // prefix
        String prefix = "saas_gateway_task_account_uniqueid:";
        return prefix + ":" + appId + ":" + bizType + ":" + uniqueId;
    }

}