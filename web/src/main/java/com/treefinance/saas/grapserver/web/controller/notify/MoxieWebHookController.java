package com.treefinance.saas.grapserver.web.controller.notify;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.treefinance.saas.grapserver.biz.service.moxie.MoxieTaskEventNoticeService;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.collections.MapUtils;
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
 * @author haojiahong on 2017/9/14.
 */
@RestController
@RequestMapping(value = "/grap/moxie/webhook")
public class MoxieWebHookController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MoxieWebHookController.class);

    private static final String HEADER_MOXIE_EVENT = "X-Moxie-Event";

    private static final String HEADER_MOXIE_TYPE = "X-Moxie-Type";

    private static final String HEADER_MOXIE_SIGNATURE = "X-Moxie-Signature";

    @Autowired
    private MoxieTaskEventNoticeService moxieTaskEventNoticeService;

    /**
     * 回调接口, moxie通过此endpoint通知账单更新和任务状态更新
     */
    @RequestMapping(value = "/notifications", method = RequestMethod.POST)
    public void notifyUpdateBill(@RequestBody Map body, ServletRequest request, ServletResponse response) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        // 事件类型：task or bill
        String eventName = httpServletRequest.getHeader(HEADER_MOXIE_EVENT);

        // 业务类型：email、bank、carrier 等
        String eventType = httpServletRequest.getHeader(HEADER_MOXIE_TYPE);

        // body签名
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
        if (MapUtils.isEmpty(body)) {
            writeMessage(httpServletResponse, HttpServletResponse.SC_BAD_REQUEST, "request body is empty");
            return;
        }
        LOGGER.info("receive moxie eventName={},body={}", eventName.toLowerCase(), JSON.toJSONString(body));
        // 任务创建通知
        if (StringUtils.equals(eventName.toLowerCase(), "task.submit")) {
            // 通知状态变更为 '认证中'
            LOGGER.info("task submit event,魔蝎回调,任务创建通知,认证中...body={}", JSON.toJSONString(body));
        }

        // ----- 任务登录状态通知 ----- //
        taskLoginStatus(body, eventName);

        // ----- 任务过程中的失败 ----- //
        taskFailed(body, eventName);

        // ----- 任务完成的通知处理，其中qq联系人的通知为sns，其它的都为bill ----- //
        taskComplete(body, eventName);

        writeMessage(httpServletResponse, HttpServletResponse.SC_CREATED, "default eventtype");
    }

    /**
     * 任务完成的通知处理，其中qq联系人的通知为sns，其它的都为bill
     */
    private void taskComplete(@RequestBody Map body, String eventName) {
        boolean isTaskComplete = StringUtils.equals(eventName.toLowerCase(), "bill")
                || StringUtils.equals(eventName.toLowerCase(), "allbill")
                || StringUtils.equals(eventName.toLowerCase(), "sns");
        if (isTaskComplete) {
            // 通知状态变更为 '认证完成'
            try {
                if (body.containsKey("result") && body.containsKey("task_id")) {
                    String result = body.get("result").toString();
                    String moxieTaskId = body.get("task_id") == null ? null : body.get("task_id").toString();
                    if (StringUtils.equals(result, "true")) {
                        moxieTaskEventNoticeService.bill(moxieTaskId);
                        LOGGER.info("bill event.魔蝎回调,任务完成通知,result={}, moxieTaskId={},body={}",
                                result, moxieTaskId, JSON.toJSONString(body));
                    }
                }
            } catch (Exception e) {
                LOGGER.error("body convert to object error", e);
            }
        }
    }

    /**
     * 任务过程中的失败
     *  运营商的格式{"mobile":"13429801680","timestamp":1474641874728,"result":false,"message":"系统繁忙，请稍后再试",
     * "user_id":"1111","task_id":"3e9ff350-819c-11e6-b7fe-00163e004a23"}
     */
    private void taskFailed(@RequestBody Map body, String eventName) {
        if (StringUtils.equals(eventName.toLowerCase(), "task.fail")) {
            try {
                if (body.containsKey("result") && body.containsKey("message")) {
                    String moxieTaskId = null;
                    if (body.containsKey("task_id")) {
                        moxieTaskId = body.get("task_id") == null ? null : body.get("task_id").toString();
                    }
                    String result = body.get("result").toString();
                    String message = body.get("message") == null ? "未知异常" : body.get("message").toString();
                    if (StringUtils.equals(result, "false")) {
                        // 通知状态变更为 '任务采集失败'
                        moxieTaskEventNoticeService.taskFail(moxieTaskId, message);
                        LOGGER.info("task fail event.魔蝎回调,任务过程中失败,result={}, message={}, moxieTaskId={},body={}",
                                result, message, moxieTaskId, JSON.toJSONString(body));
                    }
                }
            } catch (Exception e) {
                LOGGER.error("body convert to object error", e);
            }
        }
    }

    /**
     * 任务登录状态通知
     */
    private void taskLoginStatus(@RequestBody Map body, String eventName) {
        if (StringUtils.equals(eventName.toLowerCase(), "task")) {
            try {
                if (body.containsKey("result")) {
                    String result = body.get("result").toString();
                    String moxieTaskId = null;
                    if (body.containsKey("task_id")) {
                        moxieTaskId = body.get("task_id") == null ? null : body.get("task_id").toString();
                    }
                    if (StringUtils.equals(result, "false")) {
                        String message = null;
                        if (body.containsKey("message")) {
                            message = body.get("message") == null ? "未知异常" : body.get("message").toString();
                        }
                        // 通知状态变更为 '认证失败'
                        moxieTaskEventNoticeService.loginFail(moxieTaskId, message);
                        LOGGER.info("task event.魔蝎回调,任务登录状态通知:登录失败,moxieTaskId={},result={}, message={},body={}",
                                moxieTaskId, result, message, JSON.toJSONString(body));
                    }
                    if (StringUtils.equals(result, "true")) {
                        // 通知状态变更为 '认证成功'
                        moxieTaskEventNoticeService.loginSuccess(moxieTaskId);
                        LOGGER.info("task event. result={}", result);
                        LOGGER.info("task event.魔蝎回调,任务登录状态通知:登录成功,moxieTaskId={},result={},body={}",
                                moxieTaskId, result, JSON.toJSONString(body));
                    }
                }
            } catch (Exception e) {
                LOGGER.error("body convert to object error", e);
            }
        }
    }

    private void writeMessage(HttpServletResponse response, int status, String content) {
        LOGGER.info("执行魔蝎回调:status={},content={}", status, content);
        response.setStatus(status);
        try {
            PrintWriter printWriter = response.getWriter();
            printWriter.write(content);
        } catch (IOException e) {
            LOGGER.error("moxie webhook response error:", e);
        }
    }

}
