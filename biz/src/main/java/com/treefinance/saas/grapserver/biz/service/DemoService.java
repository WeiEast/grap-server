package com.treefinance.saas.grapserver.biz.service;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.treefinance.basicservice.security.crypto.facade.EncryptionIntensityEnum;
import com.treefinance.basicservice.security.crypto.facade.ISecurityCryptoService;
import com.treefinance.saas.grapserver.biz.common.CallbackSecureHandler;
import com.treefinance.saas.grapserver.biz.config.DiamondConfig;
import com.treefinance.saas.grapserver.common.exception.BizException;
import com.treefinance.saas.grapserver.common.model.dto.demo.fund.*;
import com.treefinance.saas.grapserver.common.model.vo.demo.fund.FundBillRecordVO;
import com.treefinance.saas.grapserver.common.model.vo.demo.fund.FundLoanInfoVO;
import com.treefinance.saas.grapserver.common.model.vo.demo.fund.FundLoanRepayRecordVO;
import com.treefinance.saas.grapserver.common.model.vo.demo.fund.FundUserInfoVO;
import com.treefinance.saas.grapserver.common.utils.BeanUtils;
import com.treefinance.saas.grapserver.common.utils.JsonUtils;
import com.treefinance.saas.grapserver.common.utils.RemoteDataDownloadUtils;
import com.treefinance.saas.grapserver.dao.entity.AppLicense;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
    @Autowired
    private DiamondConfig diamondConfig;


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
        if (!checkIsDemoApp(appId)) {
            logger.error("此商户appId={}没有权限访问demo信息页面,demoAppIds={}", appId, diamondConfig.getDemoH5AppIds());
            throw new BizException("获取数据失败,非法请求");
        }
        String data = this.getData(appId, params);
        if (StringUtils.isBlank(data)) {
            return Lists.newArrayList();
        }
        FundDataDTO fundData = JsonUtils.toJavaBean(data, FundDataDTO.class);
        FundUserInfoDTO userInfo = fundData.getUserInfo();
        if (userInfo == null) {
            return Lists.newArrayList();
        }
        if (StringUtils.isNotBlank(userInfo.getIdCard())) {
            userInfo.setIdCard(iSecurityCryptoService.decrypt(userInfo.getIdCard(), EncryptionIntensityEnum.NORMAL));
        }
        if (StringUtils.isNotBlank(userInfo.getMobile())) {
            userInfo.setMobile(iSecurityCryptoService.decrypt(userInfo.getMobile(), EncryptionIntensityEnum.NORMAL));
        }
        FundUserInfoVO vo = new FundUserInfoVO();
        BeanUtils.copyProperties(userInfo, vo);
        if (userInfo.getBalance() != null) {
            vo.setBalance(new BigDecimal(userInfo.getBalance()).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP));
        }
        if (userInfo.getFundBalance() != null) {
            vo.setFundBalance(new BigDecimal(userInfo.getFundBalance()).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP));
        }
        if (userInfo.getSubsidyBalance() != null) {
            vo.setSubsidyBalance(new BigDecimal(userInfo.getSubsidyBalance()).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP));
        }
        if (userInfo.getSubsidyIncome() != null) {
            vo.setSubsidyIncome(new BigDecimal(userInfo.getSubsidyIncome()).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP));
        }
        if (userInfo.getMonthlyCorporationIncome() != null) {
            vo.setMonthlyCorporationIncome(new BigDecimal(userInfo.getMonthlyCorporationIncome()).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP));
        }
        if (userInfo.getMonthlyCustomerIncome() != null) {
            vo.setMonthlyCustomerIncome(new BigDecimal(userInfo.getMonthlyCustomerIncome()).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP));
        }
        if (userInfo.getMonthlyTotalIncome() != null) {
            vo.setMonthlyTotalIncome(new BigDecimal(userInfo.getMonthlyTotalIncome()).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP));
        }
        if (userInfo.getBaseNumber() != null) {
            vo.setBaseNumber(new BigDecimal(userInfo.getBaseNumber()).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP));
        }

        List<FundUserInfoVO> userInfoList = Lists.newArrayList();
        userInfoList.add(vo);
        return userInfoList;
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
        if (!checkIsDemoApp(appId)) {
            logger.error("此商户appId={}没有权限访问demo信息页面,demoAppIds={}", appId, diamondConfig.getDemoH5AppIds());
            throw new BizException("获取数据失败,非法请求");
        }
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
        List<FundBillRecordVO> result = Lists.newArrayList();
        for (FundBillRecordDTO dto : subList) {
            FundBillRecordVO vo = new FundBillRecordVO();
            BeanUtils.copyProperties(dto, vo);
            if (dto.getOutcome() != null) {
                vo.setOutcome(new BigDecimal(dto.getOutcome()).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP));
            }
            if (dto.getIncome() != null) {
                vo.setIncome(new BigDecimal(dto.getIncome()).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP));
            }
            if (dto.getSubsidyIncome() != null) {
                vo.setSubsidyIncome(new BigDecimal(dto.getSubsidyIncome()).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP));
            }
            if (dto.getSubsidyOutcome() != null) {
                vo.setSubsidyOutcome(new BigDecimal(dto.getSubsidyOutcome()).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP));
            }
            if (dto.getBalance() != null) {
                vo.setBalance(new BigDecimal(dto.getBalance()).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP));
            }
            if (dto.getCorporationIncome() != null) {
                vo.setCorporationIncome(new BigDecimal(dto.getCorporationIncome()).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP));
            }
            if (dto.getCustomerIncome() != null) {
                vo.setCustomerIncome(new BigDecimal(dto.getCustomerIncome()).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP));
            }
            if (dto.getAdditionalIncome() != null) {
                vo.setAdditionalIncome(new BigDecimal(dto.getAdditionalIncome()).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP));
            }
            result.add(vo);
        }
        return result;
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
        if (!checkIsDemoApp(appId)) {
            logger.error("此商户appId={}没有权限访问demo信息页面,demoAppIds={}", appId, diamondConfig.getDemoH5AppIds());
            throw new BizException("获取数据失败,非法请求");
        }
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
        List<FundLoanInfoVO> result = Lists.newArrayList();
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

            FundLoanInfoVO vo = new FundLoanInfoVO();
            BeanUtils.copyProperties(info, vo);
            if (info.getLoanAmount() != null) {
                vo.setLoanAmount(new BigDecimal(info.getLoanAmount()).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP));
            }
            if (info.getMonthlyRepayAmount() != null) {
                vo.setMonthlyRepayAmount(new BigDecimal(info.getMonthlyRepayAmount()).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP));
            }
            if (info.getCommercialAmount() != null) {
                vo.setCommercialAmount(new BigDecimal(info.getCommercialAmount()).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP));
            }
            if (info.getRemainAmount() != null) {
                vo.setRemainAmount(new BigDecimal(info.getRemainAmount()).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP));
            }
            if (info.getOverdueCapital() != null) {
                vo.setOverdueCapital(new BigDecimal(info.getOverdueCapital()).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP));
            }
            if (info.getOverdueInterest() != null) {
                vo.setOverdueInterest(new BigDecimal(info.getOverdueInterest()).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP));
            }
            if (info.getOverduePenalty() != null) {
                vo.setOverduePenalty(new BigDecimal(info.getOverduePenalty()).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP));
            }
            result.add(vo);

        }
        return result;
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
        if (!checkIsDemoApp(appId)) {
            logger.error("此商户appId={}没有权限访问demo信息页面,demoAppIds={}", appId, diamondConfig.getDemoH5AppIds());
            throw new BizException("获取数据失败,非法请求");
        }
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
        List<FundLoanRepayRecordVO> result = Lists.newArrayList();
        for (FundLoanRepayRecordDTO dto : subList) {
            FundLoanRepayRecordVO vo = new FundLoanRepayRecordVO();
            BeanUtils.copyProperties(dto, vo);
            if (dto.getRepayAmount() != null) {
                vo.setRepayAmount(new BigDecimal(dto.getRepayAmount()).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP));
            }
            if (dto.getRepayCapital() != null) {
                vo.setRepayCapital(new BigDecimal(dto.getRepayCapital()).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP));
            }
            if (dto.getRepayInterest() != null) {
                vo.setRepayInterest(new BigDecimal(dto.getRepayInterest()).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP));
            }
            if (dto.getRepayPenalty() != null) {
                vo.setRepayPenalty(new BigDecimal(dto.getRepayPenalty()).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP));
            }
            result.add(vo);
        }
        return result;
    }

    private Boolean checkIsDemoApp(String appId) {
        String demoAppIds = diamondConfig.getDemoH5AppIds();
        if (StringUtils.isBlank(demoAppIds)) {
            logger.error("配置中心中demo.h5.appIds属性未配置");
            return false;
        }
        List<String> demoAppIdList = Splitter.on(",").splitToList(demoAppIds);
        boolean flag = demoAppIdList.contains(appId);
        return flag;
    }
}
