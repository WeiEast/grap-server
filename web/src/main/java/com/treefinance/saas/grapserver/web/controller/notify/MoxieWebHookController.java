package com.treefinance.saas.grapserver.web.controller.notify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.treefinance.saas.grapserver.biz.service.moxie.MoxieTaskEventNoticeService;
import org.apache.commons.codec.binary.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * Created by haojiahong on 2017/9/14.
 */
@RestController
@RequestMapping(value = "/moxie/webhook")
public class MoxieWebHookController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MoxieWebHookController.class);

    private static final String HEADER_MOXIE_EVENT = "X-Moxie-Event";

    private static final String HEADER_MOXIE_TYPE = "X-Moxie-Type";

    private static final String HEADER_MOXIE_SIGNATURE = "X-Moxie-Signature";


    private static ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MoxieTaskEventNoticeService moxieTaskEventNoticeService;

//    @Value("${moxie.signature.secret}")
//    private String secret;


    /**
     * 回调接口, moxie通过此endpoint通知账单更新和任务状态更新
     */
    @RequestMapping(value = "/notifications", method = RequestMethod.POST)
    public void notifyUpdateBill(@RequestBody String body, ServletRequest request, ServletResponse response) {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        //事件类型：task or bill
        String eventName = httpServletRequest.getHeader(HEADER_MOXIE_EVENT);

        //业务类型：email、bank、carrier 等
        String eventType = httpServletRequest.getHeader(HEADER_MOXIE_TYPE);

        //body签名
        String signature = httpServletRequest.getHeader(HEADER_MOXIE_SIGNATURE);

        if (Strings.isNullOrEmpty(eventName)) {
            writeMessage(httpServletResponse, HttpServletResponse.SC_BAD_REQUEST, "header not found:" + HEADER_MOXIE_EVENT);
            return;
        }

        if (Strings.isNullOrEmpty(eventType)) {
            writeMessage(httpServletResponse, HttpServletResponse.SC_BAD_REQUEST, "header not found:" + HEADER_MOXIE_TYPE);
            return;
        }

        if (Strings.isNullOrEmpty(signature)) {
            writeMessage(httpServletResponse, HttpServletResponse.SC_BAD_REQUEST, "header not found:" + HEADER_MOXIE_SIGNATURE);
            return;
        }


        if (Strings.isNullOrEmpty(body)) {
            writeMessage(httpServletResponse, HttpServletResponse.SC_BAD_REQUEST, "request body is empty");
            return;
        }

        //验签，判断body是否被篡改
//        if (!SignatureUtils.base64Hmac256(secret, body).equals(signature)) {
//            writeMessage(httpServletResponse, HttpServletResponse.SC_BAD_REQUEST, "signature mismatch");
//            return;
//        }

        LOGGER.info("receive moxie eventName={},body={}", eventName.toLowerCase(), body);
        //任务创建通知
        if (StringUtils.equals(eventName.toLowerCase(), "task.submit")) {
            //通知状态变更为 '认证中'
            LOGGER.info("task submit event 认证中...");
        }

        //任务登录状态通知
        //{"mobile":"15368098198","timestamp":1476084445670,"result":false,"message":"[CALO-22001-10]-服务密码错误，请确认正确后输入。","user_id":"374791","task_id":"fdda6b30-8eba-11e6-b7e9-00163e10b2cd"}
        if (StringUtils.equals(eventName.toLowerCase(), "task")) {
            try {
                Map<String, ?> map = objectMapper.readValue(body, Map.class);
                if (map.containsKey("result")) {
                    String result = map.get("result").toString();
                    String moxieTaskId = null;
                    if (map.containsKey("task_id")) {
                        moxieTaskId = map.get("task_id") == null ? null : map.get("task_id").toString();
                    }
                    if (StringUtils.equals(result, "false")) {
                        String message = null;
                        if (map.containsKey("message")) {
                            message = map.get("message") == null ? "未知异常" : map.get("message").toString();
                        }
                        //通知状态变更为 '认证失败'
                        moxieTaskEventNoticeService.loginFail(moxieTaskId, message);
                        LOGGER.info("task event. result={}, message={}", result, message);
                    }
                    if (StringUtils.equals(result, "true")) {
                        //通知状态变更为 '认证成功'
                        moxieTaskEventNoticeService.loginSuccess(moxieTaskId);
                        LOGGER.info("task event. result={}", result);
                    }
                }
            } catch (Exception e) {
                LOGGER.error("body convert to object error", e);
            }
        }

        //任务过程中的失败
        //运营商的格式{"mobile":"13429801680","timestamp":1474641874728,"result":false,"message":"系统繁忙，请稍后再试","user_id":"1111","task_id":"3e9ff350-819c-11e6-b7fe-00163e004a23"}
        if (StringUtils.equals(eventName.toLowerCase(), "task.fail")) {
            try {
                Map<String, ?> map = objectMapper.readValue(body, Map.class);
                if (map.containsKey("result") && map.containsKey("message")) {
                    String moxieTaskId = null;
                    if (map.containsKey("task_id")) {
                        moxieTaskId = map.get("task_id") == null ? null : map.get("task_id").toString();
                    }
                    String result = map.get("result").toString();
                    String message = map.get("message") == null ? "未知异常" : map.get("message").toString();
                    if (StringUtils.equals(result, "false")) {
                        //通知状态变更为 '任务采集失败'
                        moxieTaskEventNoticeService.taskFail(moxieTaskId, message);
                        LOGGER.info("task fail event. result={}, message={}, task_id={}", result, message, moxieTaskId);
                    }
                }
            } catch (Exception e) {
                LOGGER.error("body convert to object error", e);
            }
        }

        //任务完成的通知处理，其中qq联系人的通知为sns，其它的都为bill
        if (StringUtils.equals(eventName.toLowerCase(), "bill") || StringUtils.equals(eventName.toLowerCase(), "allbill") || StringUtils.equals(eventName.toLowerCase(), "sns")) {

            //通知状态变更为 '认证完成'
            try {
                Map<String, ?> map = objectMapper.readValue(body, Map.class);
                if (map.containsKey("result") && map.containsKey("task_id")) {
                    String result = map.get("result").toString();
                    String moxieTaskId = map.get("task_id") == null ? null : map.get("task_id").toString();
                    if (StringUtils.equals(result, "true")) {
                        moxieTaskEventNoticeService.bill(moxieTaskId);
                        LOGGER.info("bill event. result={}, task_id={}", result, moxieTaskId);
                    }
                }
            } catch (Exception e) {
                LOGGER.error("body convert to object error", e);
            }

        }

        if (StringUtils.equals(eventName.toLowerCase(), "report")) {
            try {
//                Object object = objectMapper.readValue(body, EventTypeUtil.getReportEventType(eventType.toLowerCase()));
//                eventBus.post(object);
            } catch (Exception e) {
                LOGGER.error("body convert to object error", e);
            }
        }


        writeMessage(httpServletResponse, HttpServletResponse.SC_CREATED, "default eventtype");
    }

    private void writeMessage(HttpServletResponse response, int status, String content) {
        response.setStatus(status);
        try {
            PrintWriter printWriter = response.getWriter();
            printWriter.write(content);
        } catch (IOException ignored) {
        }
    }

}
