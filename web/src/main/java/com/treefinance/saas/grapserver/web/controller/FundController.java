package com.treefinance.saas.grapserver.web.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.treefinance.saas.grapserver.biz.service.AppBizLicenseService;
import com.treefinance.saas.grapserver.biz.service.TaskAttributeService;
import com.treefinance.saas.grapserver.biz.service.TaskNextDirectiveService;
import com.treefinance.saas.grapserver.biz.service.moxie.FundMoxieService;
import com.treefinance.saas.grapserver.biz.service.moxie.MoxieBusinessService;
import com.treefinance.saas.grapserver.biz.service.moxie.MoxieTimeoutService;
import com.treefinance.saas.grapserver.common.enums.EBizType;
import com.treefinance.saas.grapserver.common.enums.moxie.EMoxieDirective;
import com.treefinance.saas.grapserver.common.model.dto.moxie.MoxieDirectiveDTO;
import com.treefinance.saas.grapserver.common.model.dto.moxie.MoxieLoginParamsDTO;
import com.treefinance.saas.grapserver.common.model.vo.moxie.MoxieCityInfoVO;
import com.treefinance.saas.grapserver.dao.entity.TaskAttribute;
import com.treefinance.saas.grapserver.facade.enums.ETaskAttribute;
import com.treefinance.saas.knife.result.SimpleResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

/**
 * @author hanif
 * @date created this description at 2018/11/12
 */
@RestController
@RequestMapping(value = {"/grap/h5/fund"})
public class FundController {

    private static final Logger logger = LoggerFactory.getLogger(FundController.class);

    @Autowired
    private AppBizLicenseService appBizLicenseService;
    @Autowired
    private TaskAttributeService taskAttributeService;
    @Autowired
    private FundMoxieService fundMoxieService;
    @Autowired
    private MoxieBusinessService moxieBusinessService;
    @Autowired
    private TaskNextDirectiveService taskNextDirectiveService;
    @Autowired
    private MoxieTimeoutService moxieTimeoutService;


    @RequestMapping(value = "/start", method = {RequestMethod.POST})
    public ModelAndView createTask() {
        ModelAndView modelAndView = new ModelAndView("forward:/start");
        modelAndView.addObject("bizType", EBizType.FUND.getText());
        return modelAndView;
    }

    /**
     * 商户配置接口
     */
    @RequestMapping(value = "/config", method = {RequestMethod.POST})
    public Object getConfig(@RequestParam String appid, @RequestParam("taskid") Long taskId) {
        if (StringUtils.isBlank(appid) || taskId == null) {
            logger.error("商户配置接口,输入参数appid,taskid为空appid={},taskid={}", appid, taskId);
            throw new IllegalArgumentException("Parameter 'appid' or 'taskid' is incorrect.");
        }
        List<MoxieCityInfoVO> result = moxieBusinessService.getCityList();
        Map<String, Object> map = Maps.newHashMap();
        map.put("config", result);
        map.put("license", appBizLicenseService.isShowLicense(appid, EBizType.FUND.getText()));
        map.put("licenseTemplate", appBizLicenseService.getLicenseTemplate(appid, EBizType.FUND.getText()));
        map.put("currentProvince", "");
        map.putAll(appBizLicenseService.isShowQuestionnaireOrFeedback(appid, EBizType.FUND.getText()));
        return SimpleResult.successResult(map);
    }

    @RequestMapping(value = "/current/province", method = RequestMethod.POST)
    public Object getCurrentProvince(@RequestParam String appid,
                                     @RequestParam("latitude") Double latitude,
                                     @RequestParam("longitude") Double longitude) {
        if (StringUtils.isBlank(appid) || latitude == null || longitude == null) {
            logger.error("公积金获取当前身份输入参数为空,appid={},latitude={},longitude={}", appid, latitude, longitude);
            throw new IllegalArgumentException("Parameter is incorrect.");
        }
        Object result = moxieBusinessService.getCurrentProvince(latitude, longitude);
        return SimpleResult.successResult(result);
    }

    /**
     * 登录配置接口
     */
    @RequestMapping(value = "/login/config", method = {RequestMethod.POST})
    public Object getLoginConfig(@RequestParam String appid,
                                 @RequestParam("taskid") Long taskId,
                                 @RequestParam("area_code") String areaCode) {
        if (StringUtils.isBlank(appid) || taskId == null || StringUtils.isBlank(areaCode)) {
            logger.error("公积金获取登录配置输入参数为空,appid={},taskid={},area_code={}", appid, taskId, areaCode);
            throw new IllegalArgumentException("Parameter is incorrect.");
        }
        Object loginElements = fundMoxieService.queryLoginElementsEx(areaCode);
        Object information = fundMoxieService.queryInformation(areaCode);
        Map<String, Object> map = Maps.newHashMap();
        map.put("information", information);
        map.put("loginElements", loginElements);
        logger.info("公积金taskId={}获取登录配置data={}", taskId, JSON.toJSONString(map));
        return SimpleResult.successResult(map);
    }

    /**
     * 登录接口
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
                                 @RequestParam(value = "ip", required = false) String ip) {
        if (StringUtils.isBlank(appId) || taskId == null || StringUtils.isBlank(uniqueId) || StringUtils.isBlank(areaCode)
                || StringUtils.isBlank(account) || StringUtils.isBlank(loginType) || StringUtils.isBlank(origin)) {
            logger.error("公积金登录,输入参数为空,appid={},taskid={},uniqueid={},area_code={},account={},login_type={},origin={}",
                    appId, taskId, uniqueId, areaCode, account, loginType, origin);
            throw new IllegalArgumentException("Parameter is incorrect.");
        }

        MoxieLoginParamsDTO moxieLoginParamsDTO = new MoxieLoginParamsDTO(String.valueOf(taskId), areaCode, account, password, loginType, idCard, mobile,
                realName, subArea, loanAccount, loanPassword, corpAccount, corpName, origin, ip);
        Map<String, Object> map = moxieBusinessService.createMoxieTask(taskId, moxieLoginParamsDTO);
        logger.info("taskId={}公积金登录轮询,下一指令信息={}", taskId, JSON.toJSONString(map));
        return SimpleResult.successResult(map);
    }

    /**
     * 输入图片验证码/短信
     */
    @RequestMapping(value = "/verifycode", method = {RequestMethod.POST})
    public Object submitTaskInput(@RequestParam("taskid") Long taskId, @RequestParam("verifycode") String input) {
        if (taskId == null || StringUtils.isBlank(input)) {
            logger.info("taskId={}公积金输入验证码输入参数为空,input={}", input);
            throw new IllegalArgumentException("Parameter is incorrect.");
        }
        TaskAttribute attribute = taskAttributeService.findByName(taskId, ETaskAttribute.FUND_MOXIE_TASKID.getAttribute(), false);
        if (attribute == null) {
            logger.info("taskId={}在任务属性表中未找到对应的魔蝎任务id", taskId);
            return SimpleResult.failResult("任务查询失败");
        }
        String moxieTaskId = attribute.getValue();
        moxieBusinessService.verifyCodeInput(taskId, moxieTaskId, input);
        return SimpleResult.successResult(true);
    }

    /**
     * 轮询任务执行指令(魔蝎公积金)
     */
    @RequestMapping(value = "/next_directive", method = {RequestMethod.POST})
    public Object nextMoxieDirective(@RequestParam("taskid") Long taskid) throws Exception {
        String content = taskNextDirectiveService.getNextDirective(taskid);
        Map<String, Object> map = Maps.newHashMap();
        if (StringUtils.isEmpty(content)) {
            // 轮询过程中，判断任务是否超时
            if (moxieTimeoutService.isTaskTimeout(taskid)) {
                // 异步处理任务超时
                moxieTimeoutService.handleTaskTimeout(taskid);
            }
            map.put("directive", "waiting");
            map.put("information", "请等待");
        } else {
            MoxieDirectiveDTO directiveMessage = JSON.parseObject(content, MoxieDirectiveDTO.class);
            map.put("directive", directiveMessage.getDirective());
            // 仅任务成功或回调失败时，转JSON处理
            if (EMoxieDirective.TASK_SUCCESS.getText().equals(directiveMessage.getDirective()) ||
                    EMoxieDirective.TASK_FAIL.getText().equals(directiveMessage.getDirective())) {
                map.put("information", JSON.parse(directiveMessage.getRemark()));
            } else {
                map.put("information", directiveMessage.getRemark());
            }
        }
        logger.info("taskId={}公积金指令轮询,下一指令信息={}", taskid, map);
        return SimpleResult.successResult(map);
    }

}
