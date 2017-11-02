package com.treefinance.saas.grapserver.biz.service;

import com.treefinance.saas.grapserver.biz.processor.burypoint.operator.OperatorBuryPointSpecialProcessor;
import com.treefinance.saas.grapserver.biz.processor.burypoint.operator.OperatorBuryPointSpecialRequest;
import com.treefinance.saas.grapserver.common.enums.EBizType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 业务埋点的特殊处理类
 * Created by haojiahong on 2017/10/27.
 */
@Service
public class TaskBuryPointSpecialService {

    private final static Logger logger = LoggerFactory.getLogger(TaskBuryPointSpecialService.class);

    @Autowired
    private OperatorBuryPointSpecialProcessor operatorBuryPointSpecialProcessor;

    /**
     * 业务埋点的特殊处理
     *
     * @param bizType 业务类型
     * @param extra   特殊处理字段
     * @param taskId  任务id
     * @param appId   商户id
     * @param code    埋点编码
     */
    @Async
    public void doProcess(String bizType, String extra, Long taskId, String appId, String code) {
        //运营商确认手机号人数,任务创建任务
        if (StringUtils.equalsIgnoreCase(bizType, EBizType.OPERATOR.getText())) {
            OperatorBuryPointSpecialRequest request = new OperatorBuryPointSpecialRequest();
            request.setAppId(appId);
            request.setTaskId(taskId);
            request.setExtra(extra);
            request.setCode(code);
            operatorBuryPointSpecialProcessor.doService(request);
        }

    }


}
