package com.treefinance.saas.grapserver.biz.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.treefinance.saas.grapserver.common.model.dto.AppCallbackConfigDTO;
import com.treefinance.saas.grapserver.common.model.dto.TaskDTO;
import com.treefinance.saas.grapserver.facade.enums.EDataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 爬取数据回调Service
 * @author yh-treefinance on 2017/12/25.
 */
@Service
public class GrapDataCallbackService {
    /**
     * logger
     */
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AppCallbackConfigService appCallbackConfigService;

    /**
     * 获取回调配置
     */
    public List<AppCallbackConfigDTO> getCallbackConfigs(TaskDTO taskDTO, EDataType dataType) {
        String appId = taskDTO.getAppId();
        Byte bizType = taskDTO.getBizType();
        List<AppCallbackConfigDTO> configList = appCallbackConfigService.getByAppIdAndBizType(appId, bizType, dataType);
        logger.info("根据业务类型匹配回调配置结果:taskId={},configList={}", taskDTO.getId(), JSON.toJSONString(configList));
        if (CollectionUtils.isEmpty(configList)) {
            return Lists.newArrayList();
        }
        return configList.stream()
                .filter(config -> config != null && dataType.getType().equals(config.getDataType()))
                .collect(Collectors.toList());
    }

}
