package com.treefinance.saas.grapserver.biz.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.treefinance.basicservice.security.crypto.facade.EncryptionIntensityEnum;
import com.treefinance.basicservice.security.crypto.facade.ISecurityCryptoService;
import com.treefinance.saas.grapserver.biz.common.CallbackSecureHandler;
import com.treefinance.saas.grapserver.common.exception.BizException;
import com.treefinance.saas.grapserver.common.model.dto.demo.fund.*;
import com.treefinance.saas.grapserver.common.utils.JsonUtils;
import com.treefinance.saas.grapserver.common.utils.RemoteDataDownloadUtils;
import com.treefinance.saas.grapserver.dao.entity.AppLicense;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * H5界面演示系统业务处理类
 * Created by haojiahong on 2017/10/17.
 */
@Service
public class DemoService {

    private static final Logger logger = LoggerFactory.getLogger(DemoService.class);

    @Autowired
    private CallbackSecureHandler callbackSecureHandler;
    @Autowired
    private AppLicenseService appLicenseService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private ISecurityCryptoService iSecurityCryptoService;


    /**
     * oss上获取数据,并做缓存
     *
     * @param appId
     * @param params
     * @return
     */
    private String getData(String appId, String params) {

        AppLicense appLicense = appLicenseService.getAppLicense(appId);
        if (appLicense == null) {
            logger.error("获取demo数据:通过appId={}查询密钥信息空", appId);
            throw new BizException("获取数据失败,请求非法");
        }
        try {
            params = URLDecoder.decode(params, "utf-8");
            params = callbackSecureHandler.decrypt(params, appLicense.getServerPrivateKey());
            logger.info("获取demo数据:解密后的数据url为{}", JSON.toJSONString(params));
        } catch (Exception e) {
            logger.error("decryptRSAData failed", e);
            throw new BizException("获取数据失败,请求非法");
        }
        Map<String, Object> paramMap = JsonUtils.toJavaBean(params, Map.class);
        if (!paramMap.containsKey("dataUrl")) {
            logger.error("获取demo数据:解密后的params没有dataUrl属性 paramsMap={}", JSON.toJSONString(paramMap));
            throw new BizException("获取数据失败,请求非法");
        }
        String key = appId + "_" + paramMap.get("dataUrl");
        String data = redisTemplate.opsForValue().get(key);
        if (StringUtils.isBlank(data)) {
            try {
                byte[] result = RemoteDataDownloadUtils.download(paramMap.get("dataUrl").toString(), byte[].class);
                // 数据体默认使用商户密钥加密
                data = callbackSecureHandler.decryptByAES(result, appLicense.getDataSecretKey());
            } catch (Exception e) {
                logger.error("downloadData failed", e);
                throw new BizException("获取数据失败,下载失败");
            }
            redisTemplate.opsForValue().set(key, data, 5, TimeUnit.MINUTES);
        }
        return data;
    }

    /**
     * 获取公积金用户基本信息
     *
     * @param appId
     * @param params
     * @return
     */
    public Object getFundUserInfo(String appId, String params) {
        String data = this.getData(appId, params);
        if (StringUtils.isBlank(data)) {
            return null;
        }
        FundDataDTO fundData = JsonUtils.toJavaBean(data, FundDataDTO.class);//json转化耗时
        FundUserInfoDTO userInfo = fundData.getUserInfo();
        if (userInfo == null) {
            return null;
        }
        if (StringUtils.isNotBlank(userInfo.getIdCard())) {
            userInfo.setIdCard(iSecurityCryptoService.decrypt(userInfo.getIdCard(), EncryptionIntensityEnum.NORMAL));
        }
        if (StringUtils.isNotBlank(userInfo.getMobile())) {
            userInfo.setMobile(iSecurityCryptoService.decrypt(userInfo.getMobile(), EncryptionIntensityEnum.NORMAL));
        }
        return userInfo;
    }

    /**
     * 获取公积金缴存记录
     *
     * @param appId
     * @param params
     * @param pageNum
     * @return
     */
    public Object getFundBillRecordList(String appId, String params, Integer pageNum) {
        String data = this.getData(appId, params);
        if (StringUtils.isBlank(data)) {
            return Lists.newArrayList();
        }
        FundDataDTO fundData = JsonUtils.toJavaBean(data, FundDataDTO.class);
        List<FundBillRecordDTO> list = fundData.getBillRecordList();
        int total = list.size();
        int start = pageNum * 10;
        int end = ((pageNum + 1) * 10) >= total ? total : ((pageNum + 1) * 10);
        if (start > end) {
            return Lists.newArrayList();
        }
        List<FundBillRecordDTO> subList = list.subList(start, end);
        return subList;
    }

    /**
     * 获取公积金贷款信息
     *
     * @param appId
     * @param params
     * @param pageNum
     * @return
     */
    public Object getFundLoanInfoList(String appId, String params, Integer pageNum) {
        String data = this.getData(appId, params);
        if (StringUtils.isBlank(data)) {
            return Lists.newArrayList();
        }
        FundDataDTO fundData = JsonUtils.toJavaBean(data, FundDataDTO.class);
        List<FundLoanInfoDTO> list = fundData.getLoanInfoList();
        int total = list.size();
        int start = pageNum * 10;
        int end = ((pageNum + 1) * 10) >= total ? total : ((pageNum + 1) * 10);
        if (start > end) {
            return Lists.newArrayList();
        }
        List<FundLoanInfoDTO> subList = list.subList(start, end);
        for (FundLoanInfoDTO info : subList) {
            if (StringUtils.isNotBlank(info.getIdCard())) {
                info.setIdCard(iSecurityCryptoService.decrypt(info.getIdCard(), EncryptionIntensityEnum.NORMAL));
            }
            if (StringUtils.isNotBlank(info.getPhone())) {
                info.setPhone(iSecurityCryptoService.decrypt(info.getPhone(), EncryptionIntensityEnum.NORMAL));
            }
            if (StringUtils.isNotBlank(info.getBankAccount())) {
                info.setBankAccount(iSecurityCryptoService.decrypt(info.getBankAccount(), EncryptionIntensityEnum.NORMAL));
            }
        }
        return subList;
    }

    /**
     * 获取公积金还款信息
     *
     * @param appId
     * @param params
     * @param pageNum
     * @return
     */
    public Object getFundLoanRepayRecordList(String appId, String params, Integer pageNum) {
        String data = this.getData(appId, params);
        if (StringUtils.isBlank(data)) {
            return Lists.newArrayList();
        }
        FundDataDTO fundData = JsonUtils.toJavaBean(data, FundDataDTO.class);
        List<FundLoanRepayRecordDTO> list = fundData.getLoanRepayRecordList();
        int total = list.size();
        int start = pageNum * 10;
        int end = ((pageNum + 1) * 10) >= total ? total : ((pageNum + 1) * 10);
        if (start > end) {
            return Lists.newArrayList();
        }
        List<FundLoanRepayRecordDTO> subList = list.subList(start, end);
        return subList;
    }
}
