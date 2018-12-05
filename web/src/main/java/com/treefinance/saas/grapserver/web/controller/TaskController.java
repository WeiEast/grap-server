package com.treefinance.saas.grapserver.web.controller;

import com.alibaba.fastjson.JSON;
import com.datatrees.spider.share.api.SpiderTaskApi;
import com.google.common.collect.Maps;
import com.treefinance.saas.grapserver.biz.mq.model.DirectiveMessage;
import com.treefinance.saas.grapserver.biz.service.*;
import com.treefinance.saas.grapserver.common.enums.EBizType;
import com.treefinance.saas.grapserver.common.enums.EDirective;
import com.treefinance.saas.grapserver.common.enums.EOperatorCodeType;
import com.treefinance.saas.grapserver.common.model.dto.TaskDTO;
import com.treefinance.saas.grapserver.common.utils.JsonUtils;
import com.treefinance.saas.grapserver.biz.dto.MerchantBaseInfo;
import com.treefinance.saas.knife.result.SimpleResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.ValidationException;
import java.util.Map;

/**
 * @author luyuan on 2017/4/26.
 */
@RestController
@RequestMapping(value = {"/task", "/h5/task", "grap/h5/task", "/grap/task"})
public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    private TaskConfigService taskConfigService;
    @Autowired
    private TaskService taskServiceImpl;
    @Autowired
    private TaskTimeService taskTimeService;
    @Autowired
    private TaskNextDirectiveService taskNextDirectiveService;
    @Autowired
    private SpiderTaskApi spiderTaskApi;
    @Autowired
    private MerchantConfigService merchantConfigService;
    @Autowired
    private MerchantBaseInfoService merchantBaseInfoService;
    @Autowired
    private AppBizLicenseService appBizLicenseService;
    @Autowired
    private TaskBuryPointLogService taskBuryPointLogService;
    @Autowired
    private TaskBuryPointSpecialCodeService taskBuryPointSpecialCodeService;

    /**
     * 获取配置,电商在用
     *
     * @param appid
     * @param type  分类
     * @param id    过滤task_support表中的id字段
     * @param style
     * @param name  过滤task_support表中的type字段
     * @return
     */
    @RequestMapping(value = "/config", method = {RequestMethod.GET, RequestMethod.POST})
    public Object getConfig(@RequestParam String appid,
                            @RequestParam String type,
                            @RequestParam(value = "id", required = false) Integer id,
                            @RequestParam(value = "style", required = false) String style,
                            @RequestParam(value = "name", required = false) String name) {
        if (StringUtils.isBlank(type)) {
            throw new IllegalArgumentException("Parameter 'type' is incorrect.");
        }
        Map colorMap = merchantConfigService.getColorConfig(appid, style);
        Object defaultConfig = taskConfigService.getTaskConfig(type, id, name);
        Map<String, Object> map = Maps.newHashMap();
        map.put("config", defaultConfig);
        map.put("color", colorMap);
        map.put("license", appBizLicenseService.isShowLicense(appid, type));
        map.put("licenseTemplate", appBizLicenseService.getLicenseTemplate(appid, type));
        map.putAll(appBizLicenseService.isShowQuestionnaireOrFeedback(appid, type));
        return new SimpleResult<>(map);
    }

    /**
     * 获取滚动消息提示或其他类型消息提示
     *
     * @param appId 商户appId
     * @param type  业务类型
     */
    @RequestMapping(value = "/tips", method = {RequestMethod.POST})
    public Object getAgreement(@RequestParam("appid") String appId, @RequestParam String type) {
        if (StringUtils.isBlank(type)) {
            throw new IllegalArgumentException("Parameter 'type' is incorrect.");
        }
        EBizType bizType = EBizType.of(type);
        Object result = merchantConfigService.getTips(appId, bizType != null ? bizType.getCode() : null);
        return new SimpleResult<>(result);
    }

    @RequestMapping(value = "/agreement", method = {RequestMethod.GET, RequestMethod.POST})
    public Object getAgreement(@RequestParam String appid, @RequestParam Long taskid) {
        if (StringUtils.isBlank(appid)) {
            throw new IllegalArgumentException("Parameter 'type' is incorrect.");
        }
        MerchantBaseInfo merchantBaseInfo = merchantBaseInfoService.getMerchantBaseInfoByAppId(appid);
        Map<String, Object> map = Maps.newHashMap();
        map.put("chname", merchantBaseInfo.getChName());
        map.put("company", merchantBaseInfo.getCompany());
        map.put("bussiness", merchantBaseInfo.getBussiness());
        map.put("bussiness2", merchantBaseInfo.getBussiness2());
        TaskDTO taskDTO = taskServiceImpl.getById(taskid);
        if (taskDTO != null) {
            map.put("uniqueId", taskDTO.getUniqueId());
        } else {
            map.put("uniqueId", "");
        }
        return new SimpleResult<>(map);
    }

    /**
     * 轮询任务执行指令
     */
    @RequestMapping(value = "/next_directive", method = {RequestMethod.POST})
    public Object nextDirective(@RequestParam("taskid") Long taskid) throws Exception {
        String content = taskNextDirectiveService.getNextDirective(taskid);
        Map<String, Object> map = Maps.newHashMap();
        // 刚登陆成功,未收到任何指令
        if (StringUtils.isEmpty(content)) {
            // 轮询过程中，判断任务是否超时
            if (taskTimeService.isTaskTimeout(taskid)) {
                // 异步处理任务超时
                taskTimeService.handleTaskTimeout(taskid);
            }
            map.put("directive", "waiting");
            map.put("information", "请等待");
        } else {
            DirectiveMessage directiveMessage = JSON.parseObject(content, DirectiveMessage.class);
            //登陆成功后,已收到指令并指令操作已处理完毕,如输入短信验证码
            if (StringUtils.equalsIgnoreCase(directiveMessage.getDirective(), "waiting")) {
                // 轮询过程中，判断任务是否超时
                if (taskTimeService.isTaskTimeout(taskid)) {
                    // 异步处理任务超时
                    taskTimeService.handleTaskTimeout(taskid);
                }
            }
            map.put("directive", directiveMessage.getDirective());
            map.put("directiveId", directiveMessage.getDirectiveId());

            // 仅任务成功或回调失败时，转JSON处理
            if (EDirective.TASK_SUCCESS.getText().equals(directiveMessage.getDirective()) ||
                    EDirective.TASK_FAIL.getText().equals(directiveMessage.getDirective()) ||
                    EDirective.CALLBACK_FAIL.getText().equals(directiveMessage.getDirective())) {
                map.put("information", JSON.parse(directiveMessage.getRemark()));
            } else {
                map.put("information", directiveMessage.getRemark());
            }
        }
        logger.info("taskId={}下一指令信息={}", taskid, map);
        return new SimpleResult<>(map);
    }


    /**
     * 发送验证码
     */
    @RequestMapping(value = "/verification/code", method = {RequestMethod.POST})
    public Object verifyCode(@RequestParam() String directiveId,
                             @RequestParam() Long taskid,
                             @RequestParam() String type,
                             @RequestParam() String code,
                             @RequestParam(value = "extra", required = false) String extra) throws Exception {
        logger.info("taskId={}输入验证信息,directiveId={},type={},code={},extra={}",
                taskid, directiveId, type, code, extra);
        taskNextDirectiveService.deleteNextDirective(taskid);
        spiderTaskApi.importCrawlCode(directiveId, taskid, EOperatorCodeType.getCode(type), code, JsonUtils.toMap(extra, String.class, String.class));
        return new SimpleResult<>();
    }

    /**
     * 取消爬取任务
     */
    @RequestMapping(value = "/cancel", method = {RequestMethod.POST})
    public Object cancelTask(@RequestParam Long taskid) throws Exception {
        taskServiceImpl.cancelTask(taskid);
        return new SimpleResult<>();
    }

    /**
     * 埋点记录
     */
    @RequestMapping(value = "/bury/point/log", method = {RequestMethod.POST})
    public Object buryPointLog(@RequestParam("taskid") Long taskId,
                               @RequestParam("appid") String appId,
                               @RequestParam("code") String code,
                               @RequestParam(value = "extra", required = false) String extra) {
        logger.info("记录埋点:taskid={},appid={},code={},extra={}", taskId, appId, code, extra);
        if (taskId == null) {
            throw new ValidationException("参数taskid为空");
        }
        if (StringUtils.isBlank(appId)) {
            throw new ValidationException("参数appid为空");
        }
        if (StringUtils.isBlank(code)) {
            throw new ValidationException("参数code为空");
        }
        taskBuryPointLogService.pushTaskBuryPointLog(taskId, appId, code);
        taskBuryPointSpecialCodeService.doProcess(code, taskId, appId, extra);
        return SimpleResult.successResult(null);
    }

}
