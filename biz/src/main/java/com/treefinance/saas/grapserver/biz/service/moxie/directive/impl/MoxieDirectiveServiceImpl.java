package com.treefinance.saas.grapserver.biz.service.moxie.directive.impl;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.grapserver.biz.service.moxie.directive.MoxieDirectiveService;
import com.treefinance.saas.grapserver.biz.service.moxie.directive.process.impl.*;
import com.treefinance.saas.grapserver.common.enums.moxie.EMoxieDirective;
import com.treefinance.saas.grapserver.common.model.dto.moxie.MoxieDirectiveDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class MoxieDirectiveServiceImpl implements MoxieDirectiveService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    //登录成功处理
    @Autowired
    private MoxieLoginSuccessDirectiveProcessor moxieLoginSuccessDirectiveProcessor;
    //登录失败处理
    @Autowired
    private MoxieLoginFailDirectiveProcessor moxieLoginFailDirectiveProcessor;
    //任务成功处理
    @Autowired
    private MoxieSuccessDirectiveProcessor moxieSuccessDirectiveProcessor;
    //任务失败处理
    @Autowired
    private MoxieFailureDirectiveProcessor moxieFailureDirectiveProcessor;
    // 任务取消处理
    @Autowired
    private MoxieCancelDirectiveProcessor moxieCancelDirectiveProcessor;
    // 基础指令消息
    @Autowired
    private MoxieBaseDirectiveProcessor moxieBaseDirectiveProcessor;

    @Override
    public void process(MoxieDirectiveDTO directiveDTO) {
        // 转化为指令
        String directiveName = directiveDTO.getDirective();
        EMoxieDirective directive = EMoxieDirective.directiveOf(directiveName);
        if (directive == null) {
            logger.error("not supported moxie directive : {} ...", JSON.toJSONString(directiveDTO));
            return;
        }
        switch (directive) {
            case LOGIN_SUCCESS:
                moxieLoginSuccessDirectiveProcessor.process(directiveDTO);
                break;
            case LOGIN_FAIL:
                moxieLoginFailDirectiveProcessor.process(directiveDTO);
                break;
            case TASK_SUCCESS:
                moxieSuccessDirectiveProcessor.process(directiveDTO);
                break;
            case TASK_FAIL:
                moxieFailureDirectiveProcessor.process(directiveDTO);
                break;
            case TASK_CANCEL:
                moxieCancelDirectiveProcessor.process(directiveDTO);
                break;
            default:
                moxieBaseDirectiveProcessor.process(directiveDTO);
        }
    }
}
