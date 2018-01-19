package com.treefinance.saas.grapserver.biz.service;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.grapserver.biz.mq.model.QRCodeAccountNoMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 记录二维码登录爬数回传的账号信息
 * Created by haojiahong on 2018/1/18.
 */
@Service
public class QRCodeAccountNoLogService {

    @Autowired
    private TaskService taskService;

    public void logQRCodeAccountNo(String message) {
        QRCodeAccountNoMessage msg = JSON.parseObject(message, QRCodeAccountNoMessage.class);
        Long taskId = msg.getTaskId();
        String accountNo = msg.getAccountNo();
        taskService.setAccountNo(taskId, accountNo);
    }
}
