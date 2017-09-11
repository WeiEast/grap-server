package com.treefinance.saas.grapserver.biz.service.directive.impl;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.grapserver.common.enums.EDirective;
import com.treefinance.saas.grapserver.biz.service.directive.DirectiveService;
import com.treefinance.saas.grapserver.biz.service.directive.process.impl.BaseDirectiveProcessor;
import com.treefinance.saas.grapserver.biz.service.directive.process.impl.CancelDirectiveProcessor;
import com.treefinance.saas.grapserver.biz.service.directive.process.impl.FailureDirectiveProcessor;
import com.treefinance.saas.grapserver.biz.service.directive.process.impl.SuccessDirectiveProcessor;
import com.treefinance.saas.grapserver.common.model.dto.DirectiveDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class DirectiveServiceImpl implements DirectiveService {
    // logger
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    //任务成功处理
    @Autowired
    private SuccessDirectiveProcessor successDirectiveProcessor;
    //任务失败处理
    @Autowired
    private FailureDirectiveProcessor failureDirectiveProcessor;
    // 任务失败处理
    @Autowired
    private CancelDirectiveProcessor cancelDirectiveProcessor;
    // 基础指令消息
    @Autowired
    private BaseDirectiveProcessor baseDirectiveProcessor;

    @Override
    public void process(DirectiveDTO directiveDTO) {
        // 转化为指令
        String directiveName = directiveDTO.getDirective();
        EDirective directive = EDirective.directiveOf(directiveName);
        if (directive == null) {
            logger.error("not supported directive : {} ...", JSON.toJSONString(directiveDTO));
            return;
        }
        switch (directive) {
            case TASK_SUCCESS:
                successDirectiveProcessor.process(directiveDTO);
                break;
            case TASK_FAIL:
                failureDirectiveProcessor.process(directiveDTO);
                break;
            case TASK_CANCEL:
                cancelDirectiveProcessor.process(directiveDTO);
                break;
            default:
                baseDirectiveProcessor.process(directiveDTO);
        }
    }
}
