package com.treefinance.saas.grapserver.web.controller;

import com.datatrees.rawdatacentral.api.CrawlerService;
import com.datatrees.rawdatacentral.domain.operator.OperatorCatalogue;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.google.common.collect.Maps;
import com.treefinance.commonservice.facade.mobileattribution.IMobileAttributionService;
import com.treefinance.commonservice.facade.mobileattribution.MobileAttributionDTO;
import com.treefinance.saas.grapserver.biz.config.DiamondConfig;
import com.treefinance.saas.grapserver.biz.service.*;
import com.treefinance.saas.grapserver.common.enums.EBizType;
import com.treefinance.saas.grapserver.common.exception.CrawlerBizException;
import com.treefinance.saas.grapserver.common.exception.ForbiddenException;
import com.treefinance.saas.grapserver.common.utils.IpUtils;
import com.treefinance.saas.grapserver.dao.entity.TaskAttribute;
import com.treefinance.saas.knife.result.SimpleResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = {"/operator", "/h5/operator", "/grap/h5/operator", "/grap/operator"})
public class OperatorController {
    private static final Logger logger = LoggerFactory.getLogger(OperatorController.class);

    @Autowired
    private IMobileAttributionService mobileAttributionService;
    @Autowired
    private OperatorLoginSimulationService operatorLoginSimulationService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private MerchantConfigService merchantConfigService;
    @Autowired
    private TaskLicenseService taskLicenseService;
    @Autowired
    private DiamondConfig diamondConfig;

    @Autowired
    private CrawlerService crawlerService;

    @Autowired
    private AppBizLicenseService appBizLicenseService;
    @Autowired
    private TaskAttributeService taskAttributeService;

//    /**
//     * 创建任务
//     *
//     * @param appid
//     * @param uniqueId
//     * @param coorType
//     * @param deviceInfo
//     * @param extra
//     * @param request
//     * @return
//     * @throws ForbiddenException
//     */
//    @RequestMapping(value = "/start", method = {RequestMethod.POST})
//    public Object createTask(@RequestParam("appid") String appid, @RequestParam("uniqueId") String uniqueId,
//                             @RequestParam(name = "coorType", required = false) String coorType,
//                             @RequestParam("deviceInfo") String deviceInfo,
//                             @RequestParam(name = "extra", required = false) String extra,
//                             HttpServletRequest request) throws ForbiddenException, IOException {
//        logger.info("createTask : appid={},uniqueId={},coorType={},deviceInfo={},extra={}", appid, uniqueId, coorType,
//                deviceInfo, extra);
//        taskLicenseService.verifyCreateTask(appid, EBizType.OPERATOR);
//        String ipAddress = IpUtils.getIpAddress(request);
//        Long taskId = taskService.startTask(uniqueId, appid, EBizType.OPERATOR, deviceInfo, ipAddress, coorType, extra);
//        Map<String, Object> map = Maps.newHashMap();
//        map.put("taskid", String.valueOf(taskId));
//        map.put("color", merchantConfigService.getColorConfig(appid));
//        map.put("title", diamondConfig.getSdkTitle(EBizType.OPERATOR));
//        return SimpleResult.successResult(map);
//    }

    @RequestMapping(value = "/start", method = {RequestMethod.POST})
    public ModelAndView createTask() {
        ModelAndView modelAndView = new ModelAndView("forward:/start");
        modelAndView.addObject("bizType", EBizType.OPERATOR.getText());
        return modelAndView;
    }

    /**
     * 运营商获取配置
     *
     * @param appid
     * @return
     */
    @RequestMapping(value = "/config", method = {RequestMethod.POST})
    public Object getConfig(@RequestParam String appid, @RequestParam("taskid") Long taskId) {
        if (StringUtils.isBlank(appid) || taskId == null) {
            throw new IllegalArgumentException("Parameter 'appid' or 'taskid' is incorrect.");
        }
        Map<String, Object> colorMap = merchantConfigService.getColorConfig(appid);
        HttpResult<List<OperatorCatalogue>> result = crawlerService.queryAllOperatorConfig();
        if (!result.getStatus()) {
            throw new CrawlerBizException("get operator config from crawlerService failure");
        }
        TaskAttribute taskAttribute = taskAttributeService.findByName(taskId, "mobile", true);
        Map<String, Object> map = Maps.newHashMap();
        map.put("config", result.getData());
        map.put("color", colorMap);
        map.put("license", appBizLicenseService.isShowLicense(appid, EBizType.OPERATOR.getText()));
        if (taskAttribute != null) {
            map.put(taskAttribute.getName(), taskAttribute.getValue());
        }
        SimpleResult resultData = SimpleResult.successResult(map);
//        logger.info("/operator/config : {}", JSON.toJSONString(resultData));
//        logger.info("/operator/config : {}", result.getData().stream().filter(operatorCatalogue -> !"联通".equals(operatorCatalogue.getCatalogue())).collect(Collectors.toList()).getClass());
        return resultData;
    }

    /**
     * 根据输入号码查找该号码的归属地
     *
     * @param mobile
     * @return
     */
    @RequestMapping(value = "/mobile/attribution", method = {RequestMethod.POST})
    public Object mobileAttribution(@RequestParam("mobile") String mobile) {
        MobileAttributionDTO mobileAttributionDTO = mobileAttributionService.lookupMobileAttribution(mobile);
        Map<String, Object> map = Maps.newHashMap();
        if (mobileAttributionDTO != null) {
            String operator = operatorLoginSimulationService
                    .getOperator(mobileAttributionDTO.getTelOperator().getName(), mobileAttributionDTO.getProvince());
            map.put("attribution", operator);
            map.put("virtual", mobileAttributionDTO.getVNO());
        } else {
            logger.debug("mobile={},号码的归属地解析失败");
            map.put("attribution", "");
            map.put("virtual", "");
        }
        return SimpleResult.successResult(map);
    }

    /**
     * 进入模拟登录页的时候通知服务端准备登录
     *
     * @param taskid      任务ID
     * @param websiteName 爬虫提供
     * @param groupCode   爬虫提供
     * @param groupName   爬虫提供
     * @return
     */
    @RequestMapping(value = "/loginpage/prepare", method = {RequestMethod.POST})
    public Object prepare(@RequestParam("taskid") Long taskid,
                          @RequestParam("websiteName") String websiteName,
                          @RequestParam(name = "accountNo", required = false) String accountNo,
                          @RequestParam(name = "groupCode", required = false) String groupCode,
                          @RequestParam(name = "groupName", required = false) String groupName) {
        logger.info("模拟登录: 用户打开登陆页,taskid={},websiteName={},groupCode={},groupName={}", taskid, websiteName, groupCode, groupName);
        operatorLoginSimulationService.prepare(taskid, websiteName, accountNo, groupCode, groupName);
        return SimpleResult.successResult(null);
    }

    /**
     * 模拟登录页请求验证码
     *
     * @param taskid   任务id
     * @param type     验证码类型: SMS:发送短信验证码到手机 IMG:刷新图片验证码 QR:刷新二维码 (目前只有短信验证码和图片验证码)
     * @param username 用户名,例如:手机号
     * @param password 密码,例如:服务密码
     * @return
     */
    @RequestMapping(value = "/loginpage/captcha", method = {RequestMethod.POST})
    public Object captcha(@RequestParam("taskid") Long taskid, @RequestParam("type") String type,
                          @RequestParam(value = "username", required = false) String username,
                          @RequestParam(value = "password", required = false) String password) {
        logger.info("模拟登录: 请求验证码,taskid={},type={},mobile={},password={}", taskid, type, username, password);
        Object obj = operatorLoginSimulationService.refreshCode(taskid, type, username, password);
        return SimpleResult.successResult(obj);
    }

    /**
     * 登录
     *
     * @param taskid         任务id
     * @param username       用户名,例如:手机号
     * @param password       密码,例如:服务密码
     * @param code           图片验证码
     * @param randomPassword 短信验证码
     * @return
     */
    @RequestMapping(value = "/loginpage/submit", method = {RequestMethod.POST})
    public Object login(@RequestParam("taskid") Long taskid, @RequestParam("username") String username,
                        @RequestParam("accountNo") String accountNo,
                        @RequestParam("website") String website,
                        @RequestParam("password") String password,
                        @RequestParam(value = "code", required = false) String code,
                        @RequestParam(value = "randomPassword", required = false) String randomPassword,
                        @RequestParam(value = "realName", required = false) String realName,
                        @RequestParam(value = "idCard", required = false) String idCard,
                        HttpServletResponse response) {
        logger.info("模拟登录: 请求验证码,taskid={},username={},code={},randomPassword={}", taskid, username, code,
                randomPassword);
        HttpResult<String> httpResult;
        try {
            Map<String, String> extra = Maps.newHashMap();
            extra.put("realName", realName);
            extra.put("idCard", idCard);
            httpResult = operatorLoginSimulationService.login(taskid, username, password, accountNo, website, code, randomPassword, extra);
            if (httpResult != null && httpResult.getStatus()) {
                return SimpleResult.successResult(httpResult.getData());
            } else {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                return SimpleResult.failResult(httpResult.getData(), httpResult.getMessage());
            }
        } catch (Exception e) {
            logger.error("登陆失败，taskId=" + taskid, e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return SimpleResult.failResult("登陆失败，taskId=" + taskid);
        }
    }
}
