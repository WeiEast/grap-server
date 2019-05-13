package com.treefinance.saas.grapserver.biz.service;

import com.alibaba.fastjson.JSON;
import com.datatrees.spider.extra.api.EducationApi;
import com.datatrees.spider.share.domain.CommonPluginParam;
import com.datatrees.spider.share.domain.http.HttpResult;
import com.google.common.collect.Maps;
import com.treefinance.saas.grapserver.biz.domain.ChsiUserInfo;
import com.treefinance.saas.grapserver.biz.dto.TaskAttribute;
import com.treefinance.saas.grapserver.common.enums.EBizType;
import com.treefinance.saas.grapserver.exception.CrawlerBizException;
import com.treefinance.saas.grapserver.context.Constants;
import com.treefinance.saas.grapserver.facade.enums.ETaskAttribute;
import com.treefinance.saas.grapserver.manager.TaskManager;
import com.treefinance.saas.grapserver.share.cache.redis.RedisDao;
import com.treefinance.saas.grapserver.share.cache.redis.RedisKeyUtils;
import com.treefinance.saas.knife.result.SimpleResult;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 学信网
 * 
 * @author haojiahong on 2017/12/11.
 */
@Service
public class DiplomaLoginSimulationService {

    private static final Logger logger = LoggerFactory.getLogger(DiplomaLoginSimulationService.class);

    @Autowired
    private EducationApi educationApi;
    @Autowired
    private TaskManager taskManager;
    @Autowired
    private MerchantConfigService merchantConfigService;
    @Autowired
    private TaskAttributeService taskAttributeService;
    @Autowired
    private AppBizLicenseService appBizLicenseService;
    @Autowired
    private TaskTimeService taskTimeService;
    @Autowired
    private RedisDao redisDao;

    /**
     * 登陆初始化
     */
    public Object loginInit(CommonPluginParam educationParam) {
        Map<String, Object> lockMap = Maps.newHashMap();
        String key = RedisKeyUtils.genRedisLockKey(educationParam.getTaskId(), "diploma_login_init");
        try {
            lockMap = redisDao.acquireLock(key, 60 * 1000L);
            if (lockMap != null) {
                HttpResult<Object> result;
                try {
                    result = educationApi.loginInit(educationParam);
                } catch (Exception e) {
                    logger.error("学信网:调用爬数登陆初始化异常,param={}", JSON.toJSONString(educationParam), e);
                    throw e;
                }
                if (!result.getStatus()) {
                    logger.info("学信网:调用爬数登陆初始化失败,param={},result={}", JSON.toJSONString(educationParam),
                        JSON.toJSONString(result));
                    throw new CrawlerBizException(result.getMessage());
                }
                return SimpleResult.successResult(result.getData());
            }
            throw new CrawlerBizException(Constants.REDIS_LOCK_ERROR_MSG);
        } finally {
            redisDao.releaseLock(key, lockMap, 60 * 1000L);
        }
    }

    /**
     * 登陆
     */
    public Object loginSubmit(CommonPluginParam param) {
        Map<String, Object> lockMap = Maps.newHashMap();
        String key = RedisKeyUtils.genLoginLockKey(param.getTaskId());
        try {
            lockMap = redisDao.acquireLock(key, 60 * 1000L);
            if (lockMap != null) {
                HttpResult<Object> result;
                try {
                    result = educationApi.loginSubmit(param);
                } catch (Exception e) {
                    logger.error("学信网:调用爬数登陆异常,param={}", JSON.toJSONString(param), e);
                    throw e;
                }
                if (!result.getStatus()) {
                    logger.info("学信网:调用爬数登陆失败,param={},result={}", JSON.toJSONString(param), JSON.toJSONString(result));
                    if (StringUtils.isNotBlank(result.getMessage())) {
                        throw new CrawlerBizException(result.getMessage());
                    } else {
                        throw new CrawlerBizException("登陆失败,请重试");
                    }
                }
                Long taskId = param.getTaskId();
                String website = param.getWebsiteName();
                String loginName = param.getUsername();
                taskManager.setAccountNoAndWebsite(taskId, loginName, website);
                taskTimeService.updateLoginTime(taskId, new Date());
                return SimpleResult.successResult(result.getData());
            }
            throw new CrawlerBizException(Constants.REDIS_LOCK_ERROR_MSG);
        } finally {
            redisDao.releaseLock(key, lockMap, 60 * 1000L);
        }
    }

    /**
     * 注册初始化
     */
    public Object registerInit(CommonPluginParam educationParam) {
        Map<String, Object> lockMap = Maps.newHashMap();
        String key = RedisKeyUtils.genRedisLockKey(educationParam.getTaskId(), "diploma_register_init");
        try {
            lockMap = redisDao.acquireLock(key, 60 * 1000L);
            if (lockMap != null) {
                HttpResult<Object> result;
                try {
                    result = educationApi.registerInit(educationParam);
                } catch (Exception e) {
                    logger.error("学信网:调用爬数注册初始化异常,param={}", JSON.toJSONString(educationParam), e);
                    throw e;
                }
                if (!result.getStatus()) {
                    logger.info("学信网:调用爬数注册初始化失败,param={},result={}", JSON.toJSONString(educationParam),
                        JSON.toJSONString(result));
                    throw new CrawlerBizException(result.getMessage());
                }
                return SimpleResult.successResult(result.getData());
            }
            throw new CrawlerBizException(Constants.REDIS_LOCK_ERROR_MSG);
        } finally {
            redisDao.releaseLock(key, lockMap, 60 * 1000L);
        }
    }

    /**
     * 注册时刷新图片验证码
     */
    public Object registerRefreshPicCode(CommonPluginParam param) {
        HttpResult<Object> result;
        try {
            result = educationApi.registerRefreshPicCode(param);
        } catch (Exception e) {
            logger.error("学信网:调用爬数注册时刷新图片验证码异常,param={}", JSON.toJSONString(param), e);
            throw e;
        }
        if (!result.getStatus()) {
            logger.info("学信网:调用爬数注册时刷新图片验证码失败,param={},result={}", JSON.toJSONString(param), JSON.toJSONString(result));
            throw new CrawlerBizException(result.getMessage());
        }
        return SimpleResult.successResult(result.getData());
    }

    /**
     * 注册时验证图片验证码并发送短信
     */
    public Object registerValidatePicCodeAndSendSmsCode(CommonPluginParam param) {
        HttpResult<Object> result;
        try {
            result = educationApi.registerValidatePicCodeAndSendSmsCode(param);
        } catch (Exception e) {
            logger.error("学信网:调用爬数注册时验证图片验证码并发送短信异常,param={}", JSON.toJSONString(param), e);
            throw e;
        }
        if (!result.getStatus()) {
            logger.info("学信网:调用爬数注册时验证图片验证码并发送短信失败,param={},result={}", JSON.toJSONString(param),
                JSON.toJSONString(result));
            throw new CrawlerBizException(result.getMessage());
        }
        return SimpleResult.successResult(result.getData());
    }

    /**
     * 注册提交
     */
    public Object registerSubmit(CommonPluginParam param) {
        Map<String, Object> lockMap = Maps.newHashMap();
        String key = RedisKeyUtils.genRedisLockKey(param.getTaskId(), "diploma_register_submit");
        try {
            lockMap = redisDao.acquireLock(key, 60 * 1000L);
            if (lockMap != null) {
                HttpResult<Object> result;
                try {
                    result = educationApi.registerSubmit(param);
                } catch (Exception e) {
                    logger.error("学信网:调用爬数注册提交异常,param={}", JSON.toJSONString(param), e);
                    throw e;
                }
                if (!result.getStatus()) {
                    logger.info("学信网:调用爬数注册提交失败,param={},result={}", JSON.toJSONString(param),
                        JSON.toJSONString(result));
                    throw new CrawlerBizException(result.getMessage());
                }
                return SimpleResult.successResult(result.getData());
            }
            throw new CrawlerBizException(Constants.REDIS_LOCK_ERROR_MSG);
        } finally {
            redisDao.releaseLock(key, lockMap, 60 * 1000L);
        }
    }

    public ChsiUserInfo checkRegister(Long taskId,String code) {

        try {
            ChsiUserInfo userInfo = getUserInfoByCode(code);
            if (userInfo == null) {
                userInfo = getUserInfoByTaskId(taskId);
                if (userInfo == null) {
                    logger.error("学信网检查用户注册异常,用户信息为空");
                    throw new CrawlerBizException("用户信息为空");
                }

            }else {
                setUserInfo(taskId,userInfo);
            }
            if (userInfo.getMobile()==null) {
                logger.error("学信网检查用户注册异常,手机号码为空手机号码为空");
                throw new CrawlerBizException("手机号码为空");
            }
            String action = educationApi.checkRegister(taskId, Long.parseLong(userInfo.getMobile()));
            userInfo.setAction(action);
            return userInfo;
        } catch (Exception e) {
            logger.error("学信息网检查用户状态异常", e);
            throw new CrawlerBizException("chsi check user register error");
        }
    }

    private void setUserInfo(Long taskId, ChsiUserInfo userInfo) {

        taskAttributeService.insertOrUpdateSelective(taskId, ETaskAttribute.NAME.getAttribute(),
            userInfo.getName());
        taskAttributeService.insertOrUpdateSelective(taskId, ETaskAttribute.MOBILE.getAttribute(),
            userInfo.getMobile());
        taskAttributeService.insertOrUpdateSelective(taskId, ETaskAttribute.ID_CARD.getAttribute(),
            userInfo.getIdCard());
    }

    private ChsiUserInfo getUserInfoByCode(String code) {
        try {
            String key = RedisKeyUtils.genChsiUserInfoKey(code);
            Map<String, String> userInfoMap = redisDao.getHash(key);
            if (userInfoMap.isEmpty()) {
                return null;
            }
            ChsiUserInfo userInfo = new ChsiUserInfo();
            userInfo.setMobile(userInfoMap.get("mobile"));
            userInfo.setIdCard(userInfoMap.get("idCard"));
            userInfo.setName(userInfoMap.get("name"));
            logger.info(userInfo.toString());
            return userInfo;
        } catch (Exception e) {
            logger.error("get userInfo from redis error", e);
            return null;
        }
    }

    private ChsiUserInfo getUserInfoByTaskId(Long taskId) {
        Map<String, TaskAttribute> taskAttributeMap =
            taskAttributeService.findByNames(taskId, true, ETaskAttribute.MOBILE.getAttribute(),
                ETaskAttribute.NAME.getAttribute(), ETaskAttribute.ID_CARD.getAttribute());
        logger.info(taskAttributeMap.toString());

        if (MapUtils.isNotEmpty(taskAttributeMap)) {
            // code获取用户信息并保存
            TaskAttribute name = taskAttributeMap.get(ETaskAttribute.NAME.getAttribute());
            TaskAttribute mobile = taskAttributeMap.get(ETaskAttribute.MOBILE.getAttribute());
            TaskAttribute idCard = taskAttributeMap.get(ETaskAttribute.ID_CARD.getAttribute());
            ChsiUserInfo userInfo = new ChsiUserInfo();
            if (idCard == null && name == null && mobile ==null) {
                return null;
            }
            if (name != null) {
                userInfo.setName(name.getValue());

            }
            if (mobile != null) {
                userInfo.setMobile(mobile.getValue());
            }
            if (idCard != null) {
                userInfo.setIdCard(idCard.getValue());
            }
            logger.info(userInfo.toString());
            return userInfo;
        }
        return null;
    }

    /**
     * 获取配置
     */
    public Object getConfig(String appId, String style) {
        Map<String, String> colorMap = merchantConfigService.getColorConfig(appId, style);
        Map<String, Object> map = Maps.newHashMap();
        map.put("color", colorMap);
        map.put("license", appBizLicenseService.isShowLicense(appId, EBizType.DIPLOMA.getText()));
        return SimpleResult.successResult(map);
    }

    /**
     * 获取合作方属性值引用
     */
    public Object getThirdPartyReference(Long taskId) {
        // 通过code获取用户信息
        ChsiUserInfo userInfo = getUserInfoByTaskId(taskId);
        if (userInfo != null) {
            return SimpleResult.successResult(userInfo);
        }
        //过程修改 但返回参数沿用之前

        return SimpleResult.failResult("获取用户信息失败");
    }

    public String saveUserInfo(ChsiUserInfo userInfo) {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String key = RedisKeyUtils.genChsiUserInfoKey(uuid);
        Map<String, String> userInfoMap = new HashMap<>();
        userInfoMap.put("name", userInfo.getName());
        userInfoMap.put("idCard", userInfo.getIdCard());
        userInfoMap.put("mobile", userInfo.getMobile());
        try {
            redisDao.putHash(key, userInfoMap);
            redisDao.expire(key, 30, TimeUnit.MINUTES);
            return uuid;
        } catch (Exception e) {
            logger.error("put chsi userInfo to redis fail", e);
            throw new CrawlerBizException("学信网用户信息保存失败");
        }
    }

    public Object initUpdatePwd(CommonPluginParam param) {

        try {
            HttpResult<Object> result = educationApi.initUpdatePwd(param);
            if (result.getStatus()) {
                return SimpleResult.successResult(result.getData());
            }
            return SimpleResult.failResult(result.getMessage());
        } catch (Exception e) {
            logger.error("chsi pwd update init error,param:{}", param, e);
            throw new CrawlerBizException("chsi pwd update init error");
        }
    }

    public Object updatePwdSubmitAccount(CommonPluginParam param) {

        try {
            HttpResult<Object> result = educationApi.updatePwdSubmitAccount(param);
            if (result.getStatus()) {
                return SimpleResult.successResult(result.getData());
            }
            return SimpleResult.failResult(result.getMessage());
        } catch (Exception e) {
            logger.error("chsi update pwd submit account error,param:{}", param, e);
            throw new CrawlerBizException("chsi update pwd submit account error");
        }
    }

    public Object updatePwdSubmitInfo(CommonPluginParam param) {
        try {
            HttpResult<Object> result = educationApi.updatePwdSubmitInfo(param);
            if (result.getStatus()) {
                return SimpleResult.successResult("验证通过");
            }
            return SimpleResult.failResult(result.getMessage());
        } catch (Exception e) {
            logger.error("chsi update pwd submit info error,param:{}", param, e);
            throw new CrawlerBizException("chsi update pwd submit info error");
        }
    }

    public Object updatePwdSubmit(CommonPluginParam param) {

        try {
            HttpResult<Object> result = educationApi.updatePwdSubmit(param);
            if (result.getStatus()) {
                return SimpleResult.successResult("密码重置成功");
            }
            return SimpleResult.failResult(result.getMessage());
        } catch (Exception e) {
            logger.error("chsi update pwd submit error,param:{}", param, e);
            throw new CrawlerBizException("update pwd error");
        }
    }

    public Object passportCaptcha(CommonPluginParam param) {
        try {
            HttpResult<Object> result = educationApi.getPassportCaptcha(param);
            if (result.getStatus()) {
                return SimpleResult.successResult(result.getData());
            }
            return SimpleResult.failResult(result.getMessage());
        } catch (Exception e) {
            logger.error("chsi get passport captcha error,param:{}", param, e);
            throw new CrawlerBizException("get passport catptcha error");
        }
    }

    public Object userInfoCaptcha(CommonPluginParam param) {
        try {
            HttpResult<Object> result = educationApi.userInfoCaptcha(param);
            if (result.getStatus()) {
                return SimpleResult.successResult(result.getData());
            }
            return SimpleResult.failResult(result.getMessage());
        } catch (Exception e) {
            logger.error("refresh captcha error,param:{}", param, e);
            throw new CrawlerBizException("error");
        }
    }
}
