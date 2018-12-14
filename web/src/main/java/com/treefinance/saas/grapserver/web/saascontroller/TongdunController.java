package com.treefinance.saas.grapserver.web.saascontroller;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.grapserver.biz.service.TongdunService;
import com.treefinance.saas.grapserver.common.request.TongdunRequest;
import com.treefinance.saas.grapserver.context.component.AbstractController;
import com.treefinance.saas.grapserver.util.JudgeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.bind.ValidationException;

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
    private TongdunService tongdunService;

    /**
     * 同盾数据查询（大岚定制）
     */
    @RequestMapping(value = "/query", method = {RequestMethod.POST})
    public Object collect(@RequestParam("appid") String appId, @RequestParam("name") String name,
        @RequestParam("idcard") String idcard, @RequestParam("mobile") String mobile,
        @RequestParam(value = "email", required = false) String email) throws ValidationException {
        if (!JudgeUtils.isIdCard(idcard)) {
            throw new ValidationException("身份证号不合法");
        }
        if (!JudgeUtils.isCellNumber(mobile)) {
            throw new ValidationException("手机号不合法");
        }
        logger.info("同盾信息采集,输入参数:appid={},name={},idcard={},mobile={}", appId, name, idcard, mobile);
        TongdunRequest tongdunRequest = new TongdunRequest();
        tongdunRequest.setUserName(name);
        tongdunRequest.setIdCard(idcard);
        tongdunRequest.setTelNum(mobile);
        if (StringUtils.isNotBlank(email)) {
            if (!JudgeUtils.isEmail(email)) {
                throw new ValidationException("邮箱不合法");
            }
            tongdunRequest.setAccountEmail(email);
            logger.info("同盾信息采集,输入参数:email={}", email);
        }
        Long taskId = tongdunService.startCollectTask(appId, tongdunRequest);
        Object result = tongdunService.processCollectTask(taskId, appId, tongdunRequest);
        logger.info("同盾信息采集,返回结果:result={},taskId={},appid={}", JSON.toJSONString(result), taskId, appId);
        return result;
    }

    /**
     * 同盾数据查询（随手定制）
     *
     */
    @RequestMapping(value = "/query/detail", method = {RequestMethod.POST})
    public Object collectDetail(@RequestParam("appid") String appId, @RequestParam("name") String name,
        @RequestParam("idcard") String idcard, @RequestParam("mobile") String mobile,
        @RequestParam(value = "email", required = false) String email) throws ValidationException {
        if (!JudgeUtils.isIdCard(idcard)) {
            throw new ValidationException("身份证号不合法");
        }
        if (!JudgeUtils.isCellNumber(mobile)) {
            throw new ValidationException("手机号不合法");
        }

        logger.info("同盾详细信息采集,输入参数:appid={},name={},idcard={},mobile={}", appId, name, idcard, mobile);
        TongdunRequest tongdunRequest = new TongdunRequest();
        tongdunRequest.setUserName(name);
        tongdunRequest.setIdCard(idcard);
        tongdunRequest.setTelNum(mobile);
        if (StringUtils.isNotBlank(email)) {
            if (!JudgeUtils.isEmail(email)) {
                throw new ValidationException("邮箱不合法");
            }
            tongdunRequest.setAccountEmail(email);
            logger.info("同盾详细信息采集,输入参数:email={}", email);
        }
        Long taskId = tongdunService.startCollectDetailTask(appId, tongdunRequest);
        Object result = tongdunService.processCollectDetailTask(taskId, appId, tongdunRequest);
        logger.info("同盾详细信息采集,返回结果:result={},taskId={},appid={}", JSON.toJSONString(result), taskId, appId);
        return result;
    }
}
