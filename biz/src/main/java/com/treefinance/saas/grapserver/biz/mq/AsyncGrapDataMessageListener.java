package com.treefinance.saas.grapserver.biz.mq;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.grapserver.common.model.dto.AsycGrapDTO;
import com.treefinance.saas.grapserver.biz.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 异步数据回调处理
 * Created by yh-treefinance on 2017/12/19.
 */
@Component
public class AsyncGrapDataMessageListener extends AbstractRocketMqMessageListener {
    @Autowired
    protected GrapDataCallbackService grapDataCallbackService;


    @Override
    protected void handleMessage(String json) {
        Assert.notNull(json, "message body can't be null");
        // json --> 消息
        AsycGrapDTO asycGrapDTO = JSON.parseObject(json, AsycGrapDTO.class);
        // 处理数据
        grapDataCallbackService.handleAyscData(asycGrapDTO);
    }
}
