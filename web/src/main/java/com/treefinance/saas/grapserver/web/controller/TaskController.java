package com.treefinance.saas.grapserver.web.controller;

import com.alibaba.fastjson.JSON;
import com.datatrees.rawdatacentral.api.CrawlerService;
import com.google.common.collect.Maps;
import com.treefinance.saas.grapserver.biz.mq.model.DirectiveMessage;
import com.treefinance.saas.grapserver.biz.service.*;
import com.treefinance.saas.grapserver.biz.service.directive.DirectiveService;
import com.treefinance.saas.grapserver.biz.service.moxie.MoxieBusinessService;
import com.treefinance.saas.grapserver.biz.service.moxie.directive.MoxieDirectiveService;
import com.treefinance.saas.grapserver.common.enums.EDirective;
import com.treefinance.saas.grapserver.common.enums.EOperatorCodeType;
import com.treefinance.saas.grapserver.common.enums.moxie.EMoxieDirective;
import com.treefinance.saas.grapserver.common.model.Result;
import com.treefinance.saas.grapserver.common.model.dto.DirectiveDTO;
import com.treefinance.saas.grapserver.common.model.dto.TaskDTO;
import com.treefinance.saas.grapserver.common.model.dto.moxie.MoxieDirectiveDTO;
import com.treefinance.saas.grapserver.dao.entity.MerchantBaseInfo;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by luyuan on 2017/4/26.
 */

@RestController
@RequestMapping(value = {"/task", "/h5/task"})
public class TaskController {
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    private TaskConfigService taskConfigService;
    @Autowired
    private TaskServiceImpl taskServiceImpl;
    @Autowired
    private TaskTimeService taskTimeService;
    @Autowired
    private TaskNextDirectiveService taskNextDirectiveService;
    @Autowired
    private CrawlerService crawlerService;
    @Autowired
    private MerchantConfigService merchantConfigService;
    @Autowired
    private MerchantBaseInfoService merchantBaseInfoService;
    @Autowired
    private DirectiveService directiveService;
    @Autowired
    private MoxieDirectiveService moxieDirectiveService;
    @Autowired
    private MoxieBusinessService moxieBusinessService;
    @Autowired
    private AppBizLicenseService appBizLicenseService;

    /**
     * 获取配置,电商在用
     *
     * @param appid
     * @param type
     * @return
     */
    @RequestMapping(value = "/config", method = {RequestMethod.GET, RequestMethod.POST})
    public Object getConfig(@RequestParam String appid, @RequestParam String type) {
        if (StringUtils.isBlank(type)) {
            throw new IllegalArgumentException("Parameter 'type' is incorrect.");
        }
        Map<String, Object> colorMap = merchantConfigService.getColorConfig(appid);
        Object defaultConfig = taskConfigService.getTaskConfig(type);
        Map<String, Object> map = Maps.newHashMap();
        map.put("config", defaultConfig);
        map.put("color", colorMap);
        map.put("license", appBizLicenseService.isShowLicense(appid, type));
        return new Result<>(map);
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
        return new Result<>(map);
    }

    /**
     * 轮询任务执行指令
     *
     * @param taskid
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/moxie/next_directive", method = {RequestMethod.POST})
    public Object nextMoxieDirective(@RequestParam("taskid") Long taskid) throws Exception {

        //判断是否需要验证码
        Map<String, Object> result = moxieBusinessService.requireCaptcha(taskid);
        if (!MapUtils.isEmpty(result)) {
            return result;
        }
        String content = taskNextDirectiveService.getNextDirective(taskid);
        Map<String, Object> map = Maps.newHashMap();
        if (StringUtils.isEmpty(content)) {
            // 轮询过程中，判断任务是否超时
//            if (taskServiceImpl.isTaskTimeout(taskid)) {
//                // 异步处理任务超时
//                taskTimeService.handleTaskTimeout(taskid);
//            }
            map.put("directive", "waiting");
            map.put("information", "请等待");
        } else {
            MoxieDirectiveDTO directiveMessage = JSON.parseObject(content, MoxieDirectiveDTO.class);
            map.put("directive", directiveMessage.getDirective());
//            map.put("directiveId", directiveMessage.getDirectiveId());

            // 仅任务成功或回调失败时，转JSON处理
            if (EDirective.TASK_SUCCESS.getText().equals(directiveMessage.getDirective()) ||
                    EDirective.TASK_FAIL.getText().equals(directiveMessage.getDirective()) ||
                    EDirective.CALLBACK_FAIL.getText().equals(directiveMessage.getDirective())) {
                map.put("information", JSON.parse(directiveMessage.getRemark()));
            } else {
                map.put("information", directiveMessage.getRemark());
            }
            //taskNextDirectiveService.deleteNextDirective(taskid);
        }
        logger.info("taskId={}下一指令信息={}", taskid, map);
        return new Result<>(map);
    }


    /**
     * 轮询任务执行指令(魔蝎公积金)
     *
     * @param taskid
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/next_directive", method = {RequestMethod.POST})
    public Object nextDirective(@RequestParam("taskid") Long taskid) throws Exception {
        String content = taskNextDirectiveService.getNextDirective(taskid);
        Map<String, Object> map = Maps.newHashMap();
        if (StringUtils.isEmpty(content)) {
            // 轮询过程中，判断任务是否超时
            if (taskServiceImpl.isTaskTimeout(taskid)) {
                // 异步处理任务超时
                taskTimeService.handleTaskTimeout(taskid);
            }
            map.put("directive", "waiting");
            map.put("information", "请等待");
        } else {
            DirectiveMessage directiveMessage = JSON.parseObject(content, DirectiveMessage.class);
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
            //taskNextDirectiveService.deleteNextDirective(taskid);
        }
        logger.info("taskId={}下一指令信息={}", taskid, map);
        return new Result<>(map);
    }


    /**
     * 发送验证码
     *
     * @param taskid
     * @param type
     * @param code
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/verification/code", method = {RequestMethod.POST})
    public Object verifyCode(@RequestParam() String directiveId,
                             @RequestParam() Long taskid,
                             @RequestParam() String type,
                             @RequestParam() String code) throws Exception {
        taskNextDirectiveService.deleteNextDirective(taskid, null);
        crawlerService.importCrawlCode(directiveId, taskid, EOperatorCodeType.getCode(type), code, null);
        return new Result<>();
    }

    /**
     * 取消爬取任务
     *
     * @param taskid
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/cancel", method = {RequestMethod.POST})
    public Object cancelTask(@RequestParam Long taskid) throws Exception {
        logger.info("取消任务 : taskId={} ", taskid);
        if (taskServiceImpl.isDoingTask(taskid)) {
            logger.info("取消正在执行任务 : taskId={} ", taskid);
            DirectiveDTO cancelDirective = new DirectiveDTO();
            cancelDirective.setTaskId(taskid);
            cancelDirective.setDirective(EDirective.TASK_CANCEL.getText());
            directiveService.process(cancelDirective);
//            crawlerService.cancel(taskid, null);
        }
        return new Result<>();
    }

    /**
     * 取消任务(魔蝎公积金)
     *
     * @param taskid
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/moxie/cancel", method = {RequestMethod.POST})
    public Object cancelMoxieTask(@RequestParam Long taskid) throws Exception {
        logger.info("取消魔蝎任务 : taskId={} ", taskid);
        if (taskServiceImpl.isDoingTask(taskid)) {
            logger.info("取消魔蝎正在执行任务 : taskId={} ", taskid);
            MoxieDirectiveDTO cancelDirective = new MoxieDirectiveDTO();
            cancelDirective.setTaskId(taskid);
            cancelDirective.setDirective(EMoxieDirective.TASK_CANCEL.getText());
            moxieDirectiveService.process(cancelDirective);
        }
        return new Result<>();
    }
}
