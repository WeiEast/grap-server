package com.treefinance.saas.grapserver.web.saascontroller;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.grapserver.biz.service.TaskService;
import com.treefinance.saas.grapserver.biz.service.TongdunService;
import com.treefinance.saas.grapserver.common.enums.EBizType;
import com.treefinance.saas.grapserver.common.request.TongdunRequest;
import com.treefinance.saas.grapserver.context.component.AbstractController;
import com.treefinance.saas.grapserver.util.JudgeUtils;
import com.treefinance.saas.grapserver.web.RequestChecker;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 同盾数据服务接口
 * 
 * @author guoguoyun
 * @date 2018/10/17下午2:13
 */
@RestController
@RequestMapping(value = {"/loan/special/ss"})
public class TongdunController extends AbstractController {

    @Autowired
    private TaskService taskService;
    @Autowired
    private TongdunService tongdunService;

    /**
     * 同盾数据查询（大岚定制）
     */
    @RequestMapping(value = "/query", method = {RequestMethod.POST})
    public Object collect(@RequestParam("appid") String appId, @RequestParam("name") String name, @RequestParam("idcard") String idcard, @RequestParam("mobile") String mobile,
        @RequestParam(value = "email", required = false) String email) {
        validate(name, idcard, mobile, email);

        Long taskId = initial(appId, name, idcard, mobile, EBizType.TONGDUN);

        TongdunRequest tongdunRequest = buildRequest(name, idcard, mobile, email);

        Object result = tongdunService.processCollectTask(taskId, appId, tongdunRequest);
        logger.info("同盾信息采集,返回结果:result={},taskId={},appid={}", JSON.toJSONString(result), taskId, appId);
        return result;
    }

    private TongdunRequest buildRequest(String name, String idcard, String mobile, String email) {
        TongdunRequest tongdunRequest = new TongdunRequest();
        tongdunRequest.setUserName(name);
        tongdunRequest.setIdCard(idcard);
        tongdunRequest.setTelNum(mobile);
        if (StringUtils.isNotEmpty(email)) {
            tongdunRequest.setAccountEmail(email);
        }
        return tongdunRequest;
    }

    private void validate(String name, String idcard, String mobile, String email) {
        RequestChecker.notEmpty("name", name);
        RequestChecker.isTrue(JudgeUtils.isIdCard(idcard), "身份证号不合法");
        RequestChecker.isTrue(JudgeUtils.isCellNumber(mobile), "手机号不合法");
        if (StringUtils.isNotEmpty(email)) {
            RequestChecker.isTrue(JudgeUtils.isEmail(email), "邮箱不合法");
        }
    }

    private Long initial(String appId, String name, String idcard, String mobile, EBizType bizType) {
        logger.info("同盾信息输出, appid={},name={},idcard={},mobile={},type={}", appId, name, idcard, mobile, bizType);
        // 创建任务, 使用身份证号当作uniqueId
        return taskService.createTask(appId, idcard, bizType);
    }

    /**
     * 同盾数据查询（随手定制）
     *
     */
    @RequestMapping(value = "/query/detail", method = {RequestMethod.POST})
    public Object collectDetail(@RequestParam("appid") String appId, @RequestParam("name") String name, @RequestParam("idcard") String idcard,
        @RequestParam("mobile") String mobile, @RequestParam(value = "email", required = false) String email) {
        validate(name, idcard, mobile, email);

        Long taskId = initial(appId, name, idcard, mobile, EBizType.TONGDUN_KANIU);

        TongdunRequest tongdunRequest = buildRequest(name, idcard, mobile, email);

        Object result = tongdunService.processCollectDetailTask(taskId, appId, tongdunRequest);
        logger.info("同盾详细信息采集,返回结果:result={},taskId={},appid={}", JSON.toJSONString(result), taskId, appId);
        return result;
    }

    /**
     * 同盾详细信息采集(随手 V1.0版本)
     *
     */
    @RequestMapping(value = "/query/detail/v1", method = {RequestMethod.POST})
    public Object collectDetailV1(@RequestParam("appid") String appId, @RequestParam("name") String name, @RequestParam("idcard") String idcard,
        @RequestParam("mobile") String mobile, @RequestParam(value = "email", required = false) String email) {
        validate(name, idcard, mobile, email);

        Long taskId = initial(appId, name, idcard, mobile, EBizType.TONGDUN_KANIU);

        TongdunRequest tongdunRequest = buildRequest(name, idcard, mobile, email);

        Object result = tongdunService.processCollectDetailTaskV1(taskId, appId, tongdunRequest);
        logger.info("随手V1.0信息采集,返回结果:result={},taskId={},appid={}", JSON.toJSONString(result), taskId, appId);
        return result;
    }

    /**
     * 铁树信用同盾信息采集
     *
     */
    @RequestMapping(value = "/query/tieshu", method = {RequestMethod.POST})
    public Object collectTieshuDetail(@RequestParam("appid") String appId, @RequestParam("name") String name, @RequestParam("idcard") String idcard,
        @RequestParam("mobile") String mobile, @RequestParam(value = "email", required = false) String email) {
        validate(name, idcard, mobile, email);

        Long taskId = initial(appId, name, idcard, mobile, EBizType.TONGDUN_TIESHU);

        TongdunRequest tongdunRequest = buildRequest(name, idcard, mobile, email);

        Object result = tongdunService.processCollectTieshuDetailTask(taskId, appId, tongdunRequest);
        logger.info("铁树信用同盾信息采集,返回结果:result={},taskId={},appid={}", JSON.toJSONString(result), taskId, appId);
        return result;
    }

}
