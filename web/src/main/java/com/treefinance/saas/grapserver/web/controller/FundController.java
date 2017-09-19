package com.treefinance.saas.grapserver.web.controller;

import com.google.common.collect.Maps;
import com.treefinance.saas.grapserver.biz.config.DiamondConfig;
import com.treefinance.saas.grapserver.biz.service.*;
import com.treefinance.saas.grapserver.biz.service.moxie.FundMoxieService;
import com.treefinance.saas.grapserver.biz.service.moxie.MoxieBusinessService;
import com.treefinance.saas.grapserver.common.enums.EBizType;
import com.treefinance.saas.grapserver.common.exception.ForbiddenException;
import com.treefinance.saas.knife.result.SimpleResult;
import com.treefinance.saas.grapserver.common.utils.IpUtils;
import com.treefinance.saas.grapserver.dao.entity.TaskAttribute;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping(value = {"/h5/fund"})
public class FundController {
    private static final Logger logger = LoggerFactory.getLogger(FundController.class);

    @Autowired
    private TaskService taskService;
    @Autowired
    private MerchantConfigService merchantConfigService;
    @Autowired
    private TaskLicenseService taskLicenseService;
    @Autowired
    private DiamondConfig diamondConfig;
    @Autowired
    private AppBizLicenseService appBizLicenseService;
    @Autowired
    private TaskAttributeService taskAttributeService;
    @Autowired
    private FundMoxieService fundMoxieService;
    @Autowired
    private MoxieBusinessService moxieBusinessService;

    /**
     * 创建任务
     *
     * @param appid
     * @param uniqueId
     * @param coorType
     * @param deviceInfo
     * @param extra
     * @param request
     * @return
     * @throws ForbiddenException
     * @throws IOException
     */
    @RequestMapping(value = "/start", method = {RequestMethod.POST})
    public Object createTask(@RequestParam("appid") String appid,
                             @RequestParam("uniqueId") String uniqueId,
                             @RequestParam(name = "coorType", required = false) String coorType,
                             @RequestParam("deviceInfo") String deviceInfo,
                             @RequestParam(name = "extra", required = false) String extra,
                             HttpServletRequest request) throws ForbiddenException, IOException {
        logger.info("createTask : appid={},uniqueId={},coorType={},deviceInfo={},extra={}", appid, uniqueId, coorType,
                deviceInfo, extra);
        taskLicenseService.verifyCreateTask(appid, EBizType.FUND);
        String ipAddress = IpUtils.getIpAddress(request);
        Long taskId = taskService.startTask(uniqueId, appid, EBizType.FUND, deviceInfo, ipAddress, coorType, extra);
        Map<String, Object> map = Maps.newHashMap();
        map.put("taskid", String.valueOf(taskId));
        map.put("color", merchantConfigService.getColorConfig(appid));
        map.put("title", diamondConfig.getSdkTitle(EBizType.FUND));
        return SimpleResult.successResult(map);
    }

    /**
     * 商户配置接口
     *
     * @param appid
     * @param taskId
     * @return
     */
    @RequestMapping(value = "/config", method = {RequestMethod.POST})
    public Object getConfig(@RequestParam String appid, @RequestParam("taskid") Long taskId, HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isBlank(appid) || taskId == null) {
            throw new IllegalArgumentException("Parameter 'appid' or 'taskid' is incorrect.");
        }
        Object result = moxieBusinessService.queryCityList();
        Map<String, Object> map = Maps.newHashMap();
        map.put("config", result);
        map.put("license", appBizLicenseService.isShowLicense(appid, EBizType.OPERATOR.getText()));
        return SimpleResult.successResult(map);
    }

    /**
     * 登录配置接口
     *
     * @param appid
     * @param taskId
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/login/config", method = {RequestMethod.POST})
    public Object getLoginConfig(@RequestParam String appid,
                                 @RequestParam("taskid") Long taskId,
                                 @RequestParam("area_code") String areaCode,
                                 HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isBlank(appid) || taskId == null || StringUtils.isBlank(areaCode)) {
            throw new IllegalArgumentException("Parameter is incorrect.");
        }
        Object loginElements = fundMoxieService.queryLoginElementsEx(areaCode);
        Object information = fundMoxieService.queryInformation(areaCode);
        Map<String, Object> map = Maps.newHashMap();
        map.put("information", information);
        map.put("loginElements", loginElements);
        return SimpleResult.successResult(map);
    }

    /**
     * 登录接口
     *
     * @param appId
     * @param taskId
     * @param uniqueId
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/login/submit", method = {RequestMethod.POST})
    public Object getLoginSubmit(@RequestParam("appid") String appId,
                                 @RequestParam("taskid") Long taskId,
                                 @RequestParam("uniqueid") String uniqueId,
                                 @RequestParam("area_code") String areaCode,
                                 @RequestParam("account") String account,
                                 @RequestParam(value = "password", required = false) String password,
                                 @RequestParam("login_type") String loginType,
                                 @RequestParam(value = "id_card", required = false) String idCard,
                                 @RequestParam(value = "mobile", required = false) String mobile,
                                 @RequestParam(value = "real_name", required = false) String realName,
                                 @RequestParam(value = "sub_area", required = false) String subArea,
                                 @RequestParam(value = "loan_account", required = false) String loanAccount,
                                 @RequestParam(value = "loan_password", required = false) String loanPassword,
                                 @RequestParam(value = "corp_account", required = false) String corpAccount,
                                 @RequestParam(value = "corp_name", required = false) String corpName,
                                 @RequestParam("origin") String origin,
                                 @RequestParam(value = "ip", required = false) String ip,
                                 HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isBlank(appId) || taskId == null || StringUtils.isBlank(uniqueId) || StringUtils.isBlank(areaCode)
                || StringUtils.isBlank(account) || StringUtils.isBlank(loginType) || StringUtils.isBlank(origin)) {
            throw new IllegalArgumentException("Parameter is incorrect.");
        }

        Map<String, Object> map = Maps.newHashMap();
        TaskAttribute attribute = taskAttributeService.findByName(taskId, "moxie-taskId", false);
        if (attribute != null && StringUtils.isNotBlank(attribute.getValue())) {
            logger.info("taskId={}已生成魔蝎任务id,执行查询指令", taskId);
            map = moxieBusinessService.queryLoginStatusFromDirective(taskId);
            return SimpleResult.successResult(map);
        }

        //TODO task表中的accountNo和website需要记录吗?
        String moxieId = fundMoxieService.createTasks(uniqueId, areaCode, account, password, loginType, idCard, mobile,
                realName, subArea, loanAccount, loanPassword, corpAccount, corpName, origin, ip);
        if (StringUtils.isBlank(moxieId)) {
            logger.info("魔蝎创建公积金采集任务失败");
            return SimpleResult.failResult("魔蝎创建公积金采集任务失败");
        }
        taskAttributeService.insertOrUpdateSelective(taskId, "moxie-taskId", moxieId);
        map.put("directive", "waiting");
        map.put("information", "请等待");
        return SimpleResult.successResult(map);
    }

    /**
     * 输入图片验证码/短信
     *
     * @param taskId
     * @return
     */
    @RequestMapping(value = "/verifycode", method = {RequestMethod.POST})
    public Object submitTaskInput(@RequestParam("taskid") Long taskId,
                                  @RequestParam("verifycode") String input) {
        if (taskId == null || StringUtils.isBlank(input)) {
            throw new IllegalArgumentException("Parameter is incorrect.");
        }
        TaskAttribute attribute = taskAttributeService.findByName(taskId, "moxie-taskId", false);
        if (attribute == null) {
            logger.info("taskId={}在任务属性表中未找到对应的魔蝎任务id", taskId);
            return SimpleResult.failResult("任务查询失败");
        }
        String moxieTaskId = attribute.getValue();
        fundMoxieService.submitTaskInput(moxieTaskId, input);
        return SimpleResult.successResult(null);
    }


}
