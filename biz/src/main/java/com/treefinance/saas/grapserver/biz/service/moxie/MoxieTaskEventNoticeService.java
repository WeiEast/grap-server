package com.treefinance.saas.grapserver.biz.service.moxie;

import com.treefinance.saas.grapserver.share.AsycExcutor;
import com.treefinance.saas.grapserver.common.model.dto.moxie.MoxieTaskEventNoticeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 魔蝎任务与账单统计处理服务
 * @author haojiahong on 2017/9/15.
 */
@Service
public class MoxieTaskEventNoticeService {

    @Autowired
    private MoxieBusinessService moxieBusinessService;
    @Autowired
    private AsycExcutor asycExcutor;

    /**
     * 收到魔蝎回调:登录成功
     */
    public void loginSuccess(String moxieTaskId) {
        MoxieTaskEventNoticeDTO eventNoticeDTO = new MoxieTaskEventNoticeDTO();
        eventNoticeDTO.setMoxieTaskId(moxieTaskId);
        asycExcutor.runAsyc(eventNoticeDTO, eventNoticeDTO1 -> moxieBusinessService.loginSuccess(eventNoticeDTO1));
    }

    /**
     * 收到魔蝎回调:登录失败
     */
    public void loginFail(String moxieTaskId, String message) {
        MoxieTaskEventNoticeDTO eventNoticeDTO = new MoxieTaskEventNoticeDTO();
        eventNoticeDTO.setMoxieTaskId(moxieTaskId);
        eventNoticeDTO.setMessage(message);
        asycExcutor.runAsyc(eventNoticeDTO, eventNoticeDTO1 -> moxieBusinessService.loginFail(eventNoticeDTO1));
    }

    /**
     * 收到魔蝎回调:任务采集失败
     */
    public void taskFail(String moxieTaskId, String message) {
        MoxieTaskEventNoticeDTO eventNoticeDTO = new MoxieTaskEventNoticeDTO();
        eventNoticeDTO.setMoxieTaskId(moxieTaskId);
        eventNoticeDTO.setMessage(message);
        asycExcutor.runAsyc(eventNoticeDTO, eventNoticeDTO1 -> moxieBusinessService.grabFail(eventNoticeDTO1));
    }


    /**
     * 收到魔蝎回调:账单通知
     */
    public void bill(String moxieTaskId) {
        MoxieTaskEventNoticeDTO eventNoticeDTO = new MoxieTaskEventNoticeDTO();
        eventNoticeDTO.setMoxieTaskId(moxieTaskId);
        asycExcutor.runAsyc(eventNoticeDTO, eventNoticeDTO1 -> moxieBusinessService.bill(eventNoticeDTO1));
    }

}
