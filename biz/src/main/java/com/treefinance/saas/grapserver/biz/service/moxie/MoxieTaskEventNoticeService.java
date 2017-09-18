package com.treefinance.saas.grapserver.biz.service.moxie;

import com.treefinance.saas.grapserver.biz.service.moxie.directive.MoxieDirectiveService;
import com.treefinance.saas.grapserver.common.enums.moxie.EMoxieDirective;
import com.treefinance.saas.grapserver.common.model.dto.moxie.MoxieDirectiveDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 魔蝎任务与账单统计处理服务
 * Created by haojiahong on 2017/9/15.
 */
@Service
public class MoxieTaskEventNoticeService {

    @Autowired
    private MoxieDirectiveService moxieDirectiveService;
    @Autowired
    private MoxieBusinessService moxieBusinessService;

    /**
     * 收到魔蝎回调:登录成功
     *
     * @param moxieTaskId
     */
    public void loginSuccess(String moxieTaskId) {
        MoxieDirectiveDTO directiveDTO = new MoxieDirectiveDTO();
        directiveDTO.setMoxieTaskId(moxieTaskId);
        directiveDTO.setDirective(EMoxieDirective.LOGIN_SUCCESS.getText());
        moxieDirectiveService.process(directiveDTO);
    }

    /**
     * 收到魔蝎回调:登录失败
     *
     * @param moxieTaskId
     * @param message
     */
    public void loginFail(String moxieTaskId, String message) {
        MoxieDirectiveDTO directiveDTO = new MoxieDirectiveDTO();
        directiveDTO.setMoxieTaskId(moxieTaskId);
        directiveDTO.setDirective(EMoxieDirective.LOGIN_FAIL.getText());
        directiveDTO.setRemark(message);//登录错误信息
        moxieDirectiveService.process(directiveDTO);
    }

    /**
     * 收到魔蝎回调:任务采集失败
     *
     * @param moxieTaskId
     * @param message
     */
    public void taskFail(String moxieTaskId, String message) {
        moxieBusinessService.grabFail(moxieTaskId, message);
    }


    /**
     * 收到魔蝎回调:账单通知
     *
     * @param moxieTaskId
     */
    public void bill(String moxieTaskId) {
        moxieBusinessService.bill(moxieTaskId);
    }
}
