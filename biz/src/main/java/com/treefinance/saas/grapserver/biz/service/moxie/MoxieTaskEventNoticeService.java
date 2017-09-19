package com.treefinance.saas.grapserver.biz.service.moxie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 魔蝎任务与账单统计处理服务
 * Created by haojiahong on 2017/9/15.
 */
@Service
public class MoxieTaskEventNoticeService {

    @Autowired
    private MoxieBusinessService moxieBusinessService;

    /**
     * 收到魔蝎回调:登录成功
     *
     * @param moxieTaskId
     */
    public void loginSuccess(String moxieTaskId) {
        moxieBusinessService.loginSuccess(moxieTaskId);
    }

    /**
     * 收到魔蝎回调:登录失败
     *
     * @param moxieTaskId
     * @param message
     */
    public void loginFail(String moxieTaskId, String message) {
        moxieBusinessService.loginFail(moxieTaskId, message);
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
