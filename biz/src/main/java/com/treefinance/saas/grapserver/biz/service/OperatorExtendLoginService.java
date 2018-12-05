package com.treefinance.saas.grapserver.biz.service;

import com.alibaba.fastjson.JSON;
import com.datatrees.spider.operator.api.OperatorApi;
import com.datatrees.spider.operator.domain.OperatorGroup;
import com.datatrees.spider.operator.domain.OperatorLoginConfig;
import com.datatrees.spider.operator.domain.OperatorParam;
import com.datatrees.spider.share.domain.http.HttpResult;
import com.google.common.collect.Maps;
import com.treefinance.commonservice.facade.mobileattribution.IMobileAttributionService;
import com.treefinance.commonservice.facade.mobileattribution.MobileAttributionDTO;
import com.treefinance.saas.grapserver.biz.cache.RedisDao;
import com.treefinance.saas.grapserver.common.enums.EBizType;
import com.treefinance.saas.grapserver.common.exception.CrawlerBizException;
import com.treefinance.saas.grapserver.common.model.Constants;
import com.treefinance.saas.grapserver.common.utils.RedisKeyUtils;
import com.treefinance.saas.grapserver.biz.dto.TaskAttribute;
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
 * @author haojiahong on 2017/11/29.
 */
@Service
public class OperatorExtendLoginService {

    private static final Logger logger = LoggerFactory.getLogger(OperatorExtendLoginService.class);

    @Autowired
    private MerchantConfigService merchantConfigService;
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
    @Autowired
    private RedisDao redisDao;
    @Autowired
    private OperatorApi operatorApi;

    /**
     * 根据输入号码查找该号码的归属地
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
        province = province.replace("省", "")
                .replace("市", "")
                .replace("自治区", "")
                .replace("维吾尔", "")
                .replace("壮族", "")
                .replace("回族", "");
        return province + operatorName;
    }

    /**
     * 登陆初始化,获取基本信息
     */
    public Object prepare(OperatorParam operatorParam) {
        HttpResult<Map<String, Object>> result;
        try {
            result = operatorApi.init(operatorParam);
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
     */
    public Object refreshPicCode(OperatorParam operatorParam) {
        HttpResult<Map<String, Object>> result;
        try {
            result = operatorApi.refeshPicCode(operatorParam);
        } catch (Exception e) {
            logger.error("运营商:调用爬数刷新图片验证码异常,operatorParam={}", JSON.toJSONString(operatorParam), e);
            throw e;
        }
        if (!result.getStatus()) {
            logger.info("运营商:调用爬数刷新图片验证码失败,operatorParam={},result={}",
                    JSON.toJSONString(operatorParam), JSON.toJSONString(result));
            throw new CrawlerBizException(result.getResponseCode(), result.getMessage());
        }
        return SimpleResult.successResult(result.getData());
    }

    /**
     * 刷新短信验证码
     */
    public Object refreshSmsCode(OperatorParam operatorParam) {
        HttpResult<Map<String, Object>> result;
        try {
            result = operatorApi.refeshSmsCode(operatorParam);
        } catch (Exception e) {
            logger.error("运营商:调用爬数刷新短信验证码异常,operatorParam={}", JSON.toJSONString(operatorParam), e);
            throw e;
        }
        if (!result.getStatus()) {
            logger.info("运营商:调用爬数刷新短信验证码失败,operatorParam={},result={}",
                    JSON.toJSONString(operatorParam), JSON.toJSONString(result));
            throw new CrawlerBizException(result.getResponseCode(), result.getMessage());
        }
        return SimpleResult.successResult(result.getData());
    }

    /**
     * 登陆
     */
    public Object login(OperatorParam operatorParam) {
        Map<String, Object> lockMap = Maps.newHashMap();
        try {
            lockMap = redisDao.acquireLock(RedisKeyUtils.genLoginLockKey(operatorParam.getTaskId()), 60 * 1000L);
            if (lockMap != null) {
                HttpResult<Map<String, Object>> result;
                try {
                    result = operatorApi.submit(operatorParam);
                } catch (Exception e) {
                    logger.error("运营商:调用爬数运营商登陆异常,operatorParam={}", JSON.toJSONString(operatorParam), e);
                    throw e;
                }
                if (!result.getStatus()) {
                    logger.info("运营商:调用爬数运营商登陆失败,operatorParam={},result={}",
                            JSON.toJSONString(operatorParam), JSON.toJSONString(result));
                    if (StringUtils.isNotBlank(result.getMessage())) {
                        throw new CrawlerBizException(result.getResponseCode(), result.getMessage());
                    } else {
                        throw new CrawlerBizException("登陆失败,请重试");
                    }
                }
                Long taskId = operatorParam.getTaskId();
                taskTimeService.updateLoginTime(taskId, new Date());
                return SimpleResult.successResult(result.getData());
            }
            throw new CrawlerBizException(Constants.REDIS_LOCK_ERROR_MSG);
        } finally {
            redisDao.releaseLock(RedisKeyUtils.genLoginLockKey(operatorParam.getTaskId()), lockMap, 60 * 1000L);
        }

    }

    /**
     * 获取商户配置与运营商分组信息
     */
    public Object getConfigAndGroups(String appId, Long taskId, String style) {
        Map colorMap = merchantConfigService.getColorConfig(appId, style);
        HttpResult<List<Map<String, List<OperatorGroup>>>> result;
        try {
            result = operatorApi.queryGroups();
        } catch (Exception e) {
            logger.error("运营商:调用爬数查询运营商分组信息异常", e);
            throw e;
        }
        if (!result.getStatus()) {
            logger.info("运营商:调用爬数查询运营商分组信息失败,result={}", JSON.toJSONString(result));
            throw new CrawlerBizException(result.getMessage());
        }
        TaskAttribute taskAttribute = taskAttributeService.findByName(taskId, "mobile", true);
        Map<String, Object> map = Maps.newHashMap();
        map.put("groups", result.getData());
        map.put("color", colorMap);
        map.put("license", appBizLicenseService.isShowLicense(appId, EBizType.OPERATOR.getText()));
        map.put("licenseTemplate", appBizLicenseService.getLicenseTemplate(appId, EBizType.OPERATOR.getText()));
        map.putAll(appBizLicenseService.isShowQuestionnaireOrFeedback(appId, EBizType.OPERATOR.getText()));
        if (taskAttribute != null) {
            map.put(taskAttribute.getName(), taskAttribute.getValue());
        }
        return SimpleResult.successResult(map);
    }

    /**
     * 获取运营商登陆配置信息
     */
    public Object preLoginConfig(OperatorParam operatorParam) {
        HttpResult<OperatorLoginConfig> result;
        try {
            result = operatorApi.preLogin(operatorParam);
        } catch (Exception e) {
            logger.error("运营商:调用爬数获取运营商登陆配置信息异常,operatorParam={}", JSON.toJSONString(operatorParam), e);
            throw e;
        }
        if (!result.getStatus()) {
            logger.info("运营商:调用爬数获取运营商登陆配置信息失败,operatorParam={},result={}",
                    JSON.toJSONString(operatorParam), JSON.toJSONString(result));
            throw new CrawlerBizException(result.getResponseCode(), result.getMessage());
        }
        Long taskId = operatorParam.getTaskId();
        if (result.getData() != null && result.getData().getMobile() > 0
                && StringUtils.isNotBlank(result.getData().getWebsiteName())) {
            String accountNo = String.valueOf(result.getData().getMobile());
            String websiteName = result.getData().getWebsiteName();
            taskService.updateTask(taskId, accountNo, websiteName);
        } else {
            logger.info("运营商:调用爬数获取运营商accountNo,websiteName为空,taskId={},operatorParam={},result={}",
                    taskId, JSON.toJSONString(operatorParam), JSON.toJSONString(result));
        }
        String groupCode = operatorParam.getGroupCode();
        String groupName = operatorParam.getGroupName();
        taskAttributeService.insertOrUpdateSelective(taskId, ETaskAttribute.OPERATOR_GROUP_CODE.getAttribute(), groupCode);
        taskAttributeService.insertOrUpdateSelective(taskId, ETaskAttribute.OPERATOR_GROUP_NAME.getAttribute(), groupName);
        return SimpleResult.successResult(result.getData());
    }

}
