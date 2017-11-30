package com.treefinance.saas.grapserver.biz.service;

import com.alibaba.fastjson.JSON;
import com.datatrees.rawdatacentral.api.CrawlerOperatorService;
import com.datatrees.rawdatacentral.domain.operator.OperatorCatalogue;
import com.datatrees.rawdatacentral.domain.operator.OperatorParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.google.common.collect.Maps;
import com.treefinance.commonservice.facade.mobileattribution.IMobileAttributionService;
import com.treefinance.commonservice.facade.mobileattribution.MobileAttributionDTO;
import com.treefinance.saas.grapserver.common.enums.EBizType;
import com.treefinance.saas.grapserver.common.exception.CrawlerBizException;
import com.treefinance.saas.grapserver.dao.entity.TaskAttribute;
import com.treefinance.saas.grapserver.facade.enums.ETaskAttribute;
import com.treefinance.saas.knife.result.SimpleResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by haojiahong on 2017/11/29.
 */
@Service
public class OperatorExtendLoginService {

    private static final Logger logger = LoggerFactory.getLogger(OperatorExtendLoginService.class);

    @Autowired
    private MerchantConfigService merchantConfigService;
    @Autowired
    private CrawlerOperatorService crawlerOperatorService;
    @Autowired
    private TaskAttributeService taskAttributeService;
    @Autowired
    private AppBizLicenseService appBizLicenseService;
    @Autowired
    private IMobileAttributionService mobileAttributionService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskTimeService taskTimeService;

    /**
     * 获取运营商配置
     *
     * @param appId
     * @param taskId
     * @return
     */
    public Map<String, Object> getConfig(String appId, Long taskId) {
        Map<String, Object> colorMap = merchantConfigService.getColorConfig(appId);
        HttpResult<List<OperatorCatalogue>> result;
        try {
            result = crawlerOperatorService.queryAllConfig();
        } catch (Exception e) {
            logger.error("运营商:调用爬数获取所有登录运营商配置异常,taskId={}", taskId, e);
            throw e;
        }
        if (!result.getStatus()) {
            logger.info("运营商:调用爬数获取所有登录运营商配置失败,taskId={},result={}", taskId, JSON.toJSONString(result));
            throw new CrawlerBizException(result.getMessage());
        }
        TaskAttribute taskAttribute = taskAttributeService.findByName(taskId, "mobile", true);
        Map<String, Object> map = Maps.newHashMap();
        map.put("config", result.getData());
        map.put("color", colorMap);
        map.put("license", appBizLicenseService.isShowLicense(appId, EBizType.OPERATOR.getText()));
        if (taskAttribute != null) {
            map.put(taskAttribute.getName(), taskAttribute.getValue());
        }
        return map;
    }

    /**
     * 根据输入号码查找该号码的归属地
     *
     * @param mobile
     * @return
     */
    public Map<String, Object> getMobileAttribution(String mobile) {
        MobileAttributionDTO mobileAttributionDTO = mobileAttributionService.lookupMobileAttribution(mobile);
        Map<String, Object> map = Maps.newHashMap();
        if (mobileAttributionDTO != null) {
            String operator = this
                    .getOperator(mobileAttributionDTO.getTelOperator().getName(), mobileAttributionDTO.getProvince());
            map.put("attribution", operator);
            map.put("virtual", mobileAttributionDTO.getVNO());
        } else {
            logger.debug("mobile={},号码的归属地解析失败");
            map.put("attribution", "");
            map.put("virtual", "");
        }
        return map;
    }


    private String getOperator(String operatorName, String province) {
        operatorName = StringUtils.remove(operatorName, "中国");
        province = province.replace("省", "").replace("市", "").replace("自治区", "").replace("维吾尔", "").replace("壮族", "")
                .replace("回族", "");
        return province + operatorName;
    }

    /**
     * 登陆初始化,获取基本信息
     *
     * @param operatorParam
     */
    public Object prepare(OperatorParam operatorParam) {
        HttpResult<Map<String, Object>> result;
        try {
            result = crawlerOperatorService.init(operatorParam);
        } catch (Exception e) {
            logger.error("运营商:调用爬数登陆初始化,获取基本信息异常,operatorParam={}", JSON.toJSONString(operatorParam), e);
            throw e;
        }
        if (!result.getStatus()) {
            logger.info("运营商:调用爬数登陆初始化,获取基本信息失败,operatorParam={},result={}",
                    JSON.toJSONString(operatorParam), JSON.toJSONString(result));
            throw new CrawlerBizException(result.getMessage());
        }
        Long taskId = operatorParam.getTaskId();
        String groupCode = operatorParam.getGroupCode();
        String groupName = operatorParam.getGroupName();
        String websiteName = operatorParam.getWebsiteName();
        String accountNo = operatorParam.getMobile().toString();
        taskAttributeService.insertOrUpdateSelective(taskId, ETaskAttribute.OPERATOR_GROUP_CODE.getAttribute(), groupCode);
        taskAttributeService.insertOrUpdateSelective(taskId, ETaskAttribute.OPERATOR_GROUP_NAME.getAttribute(), groupName);
        taskService.updateTask(taskId, accountNo, websiteName);
        return SimpleResult.successResult(result.getData());
    }

    /**
     * 刷新图片验证码
     *
     * @param operatorParam
     * @return
     */
    public Object refreshPicCode(OperatorParam operatorParam) {
        HttpResult<Map<String, Object>> result;
        try {
            result = crawlerOperatorService.refeshPicCode(operatorParam);
        } catch (Exception e) {
            logger.error("运营商:调用爬数刷新图片验证码异常,operatorParam={}", JSON.toJSONString(operatorParam), e);
            throw e;
        }
        if (!result.getStatus()) {
            logger.info("运营商:调用爬数刷新图片验证码失败,operatorParam={},result={}",
                    JSON.toJSONString(operatorParam), JSON.toJSONString(result));
            throw new CrawlerBizException(result.getMessage());
        }
        return SimpleResult.successResult(result.getData());
    }

    /**
     * 刷新短信验证码
     *
     * @param operatorParam
     * @return
     */
    public Object refreshSmsCode(OperatorParam operatorParam) {
        HttpResult<Map<String, Object>> result;
        try {
            result = crawlerOperatorService.refeshSmsCode(operatorParam);
        } catch (Exception e) {
            logger.error("运营商:调用爬数刷新短信验证码异常,operatorParam={}", JSON.toJSONString(operatorParam), e);
            throw e;
        }
        if (!result.getStatus()) {
            logger.info("运营商:调用爬数刷新短信验证码失败,operatorParam={},result={}",
                    JSON.toJSONString(operatorParam), JSON.toJSONString(result));
            throw new CrawlerBizException(result.getMessage());
        }
        return SimpleResult.successResult(result.getData());
    }

    /**
     * 登陆
     *
     * @param operatorParam
     * @return
     */
    public Object login(OperatorParam operatorParam) {
        HttpResult<Map<String, Object>> result;
        try {
            result = crawlerOperatorService.submit(operatorParam);
        } catch (Exception e) {
            logger.error("运营商:调用爬数运营商登陆异常,operatorParam={}", JSON.toJSONString(operatorParam), e);
            throw e;
        }
        if (!result.getStatus()) {
            logger.info("运营商:调用爬数运营商登陆失败,operatorParam={},result={}",
                    JSON.toJSONString(operatorParam), JSON.toJSONString(result));
            if (StringUtils.isNotBlank(result.getMessage())) {
                throw new CrawlerBizException(result.getMessage());
            } else {
                throw new CrawlerBizException("登陆失败,请重试");
            }
        }
        Long taskId = operatorParam.getTaskId();
        taskTimeService.updateLoginTime(taskId, new Date());
        return SimpleResult.successResult(result.getData());
    }
}
