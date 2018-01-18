package com.treefinance.saas.grapserver.biz.mq;

import com.treefinance.saas.grapserver.biz.service.QRCodeAccountNoLogService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by haojiahong on 2018/1/18.
 */
@Service
public class QRCodeAccountNoMessageListener extends AbstractRocketMqMessageListener {

    private static final Logger logger = LoggerFactory.getLogger(QRCodeAccountNoMessageListener.class);

    @Autowired
    private QRCodeAccountNoLogService qrCodeAccountNoLogService;

    @Override
    protected void handleMessage(String message) {
        logger.info("记录二维码登录爬数回传账户信息,message={}", message);
        if (StringUtils.isNotBlank(message)) {
            qrCodeAccountNoLogService.logQRCodeAccountNo(message);
        }
    }
}
