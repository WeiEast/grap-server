package com.treefinance.saas.grapserver.biz.service;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.treefinance.b2b.saas.util.RemoteDataUtils;
import com.treefinance.basicservice.security.crypto.facade.EncryptionIntensityEnum;
import com.treefinance.basicservice.security.crypto.facade.ISecurityCryptoService;
import com.treefinance.saas.grapserver.context.component.AbstractService;
import com.treefinance.saas.grapserver.util.CallbackDataUtils;
import com.treefinance.saas.grapserver.context.config.DiamondConfig;
import com.treefinance.saas.grapserver.biz.domain.AppLicense;
import com.treefinance.saas.grapserver.exception.BizException;
import com.treefinance.saas.grapserver.common.model.dto.demo.fund.FundBillRecordDTO;
import com.treefinance.saas.grapserver.common.model.dto.demo.fund.FundDataDTO;
import com.treefinance.saas.grapserver.common.model.dto.demo.fund.FundLoanInfoDTO;
import com.treefinance.saas.grapserver.common.model.dto.demo.fund.FundLoanRepayRecordDTO;
import com.treefinance.saas.grapserver.common.model.dto.demo.fund.FundUserInfoDTO;
import com.treefinance.saas.grapserver.common.model.vo.demo.fund.FundBillRecordVO;
import com.treefinance.saas.grapserver.common.model.vo.demo.fund.FundLoanInfoVO;
import com.treefinance.saas.grapserver.common.model.vo.demo.fund.FundLoanRepayRecordVO;
import com.treefinance.saas.grapserver.common.model.vo.demo.fund.FundUserInfoVO;
import com.treefinance.toolkit.util.DateUtils;
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
import java.util.stream.Collectors;

/**
 * H5界面演示系统业务处理类
 * @author haojiahong on 2017/10/17.
 */
@Service
public class DemoService extends AbstractService {

    private static final Logger logger = LoggerFactory.getLogger(DemoService.class);

    @Autowired
    private LicenseService licenseService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private ISecurityCryptoService iSecurityCryptoService;
    @Autowired
    private DiamondConfig diamondConfig;

    /**
     * oss上获取数据,并做缓存
     */
    private String getData(String appId, String params) {
        AppLicense appLicense = licenseService.getAppLicense(appId);
        if (appLicense == null) {
            logger.error("获取demo数据:通过appId={}查询密钥信息空", appId);
            throw new BizException("获取数据失败,请求非法");
        }
        try {
            params = URLDecoder.decode(params, "utf-8");
            params = CallbackDataUtils.decrypt(params, appLicense.getServerPrivateKey());
            logger.info("获取demo数据:解密后的数据url为{}", JSON.toJSONString(params));
        } catch (Exception e) {
            logger.error("decryptRSAData failed", e);
            throw new BizException("获取数据失败,请求非法");
        }
        Map<String, Object> paramMap = JSON.parseObject(params);
        if (!paramMap.containsKey("dataUrl")) {
            logger.error("获取demo数据:解密后的params没有dataUrl属性 paramsMap={}", JSON.toJSONString(paramMap));
            throw new BizException("获取数据失败,请求非法");
        }
        Object dataUrl = paramMap.get("dataUrl");
        String key = appId + "_" + dataUrl;
        String data = redisTemplate.opsForValue().get(key);
        if (StringUtils.isBlank(data)) {
            try {
                byte[] result = RemoteDataUtils.download(dataUrl.toString(), byte[].class);
                // 数据体默认使用商户密钥加密
                data = CallbackDataUtils.decryptByAES(result, appLicense.getDataSecretKey());
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
        FundDataDTO fundData = JSON.parseObject(data, FundDataDTO.class);
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
        copyProperties(userInfo, vo);
        vo.setBalance(calculatePrecision(userInfo.getBalance()));
        vo.setFundBalance(calculatePrecision(userInfo.getFundBalance()));
        vo.setSubsidyBalance(calculatePrecision(userInfo.getSubsidyBalance()));
        vo.setSubsidyIncome(calculatePrecision(userInfo.getSubsidyIncome()));
        vo.setMonthlyCorporationIncome(calculatePrecision(userInfo.getMonthlyCorporationIncome()));
        vo.setMonthlyCustomerIncome(calculatePrecision(userInfo.getMonthlyCustomerIncome()));
        vo.setMonthlyTotalIncome(calculatePrecision(userInfo.getMonthlyTotalIncome()));
        vo.setBaseNumber(calculatePrecision(userInfo.getBaseNumber()));

        List<FundUserInfoVO> userInfoList = Lists.newArrayList();
        userInfoList.add(vo);
        return userInfoList;
    }

    /**
     * 获取公积金缴存记录
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
        FundDataDTO fundData = JSON.parseObject(data, FundDataDTO.class);
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
            copyProperties(dto, vo);
            vo.setIncome(calculatePrecision(dto.getIncome()));
            vo.setOutcome(calculatePrecision(dto.getOutcome()));
            vo.setSubsidyIncome(calculatePrecision(dto.getSubsidyIncome()));
            vo.setSubsidyOutcome(calculatePrecision(dto.getSubsidyOutcome()));
            vo.setBalance(calculatePrecision(dto.getBalance()));
            vo.setCorporationIncome(calculatePrecision(dto.getCorporationIncome()));
            vo.setCustomerIncome(calculatePrecision(dto.getCustomerIncome()));
            vo.setAdditionalIncome(calculatePrecision(dto.getAdditionalIncome()));
            result.add(vo);
        }
        return result;
    }

    /**
     * 获取公积金贷款信息
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
        FundDataDTO fundData = JSON.parseObject(data, FundDataDTO.class);
        List<FundLoanInfoDTO> list = fundData.getLoanInfoList();
        list = list.stream()
                .sorted((o1, o2) -> DateUtils.parse(o2.getStartDate(), "yyyy-MM-dd")
                        .compareTo(DateUtils.parse(o1.getStartDate(), "yyyy-MM-dd")))
                .collect(Collectors.toList());
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
            copyProperties(info, vo);

            vo.setLoanAmount(calculatePrecision(info.getLoanAmount()));
            vo.setMonthlyRepayAmount(calculatePrecision(info.getMonthlyRepayAmount()));
            vo.setCommercialAmount(calculatePrecision(info.getCommercialAmount()));
            vo.setRemainAmount(calculatePrecision(info.getRemainAmount()));
            vo.setOverdueCapital(calculatePrecision(info.getOverdueCapital()));
            vo.setOverdueInterest(calculatePrecision(info.getOverdueInterest()));
            vo.setOverduePenalty(calculatePrecision(info.getOverduePenalty()));
            result.add(vo);
        }
        return result;
    }

    /**
     * 获取公积金还款信息
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
        FundDataDTO fundData = JSON.parseObject(data, FundDataDTO.class);
        List<FundLoanRepayRecordDTO> list = fundData.getLoanRepayRecordList();
        list = list.stream()
                .sorted((o1, o2) -> DateUtils.parse(o2.getRepayDate(), "yyyy-MM-dd")
                        .compareTo(DateUtils.parse(o1.getRepayDate(), "yyyy-MM-dd")))
                .collect(Collectors.toList());
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
            copyProperties(dto, vo);
            vo.setRepayAmount(calculatePrecision(dto.getRepayAmount()));
            vo.setRepayCapital(calculatePrecision(dto.getRepayCapital()));
            vo.setRepayInterest(calculatePrecision(dto.getRepayInterest()));
            vo.setRepayPenalty(calculatePrecision(dto.getRepayPenalty()));
            result.add(vo);
        }
        return result;
    }

    /**
     * 计算，输入除以100，保留小数点后两位
     * @param sourceValue   输入值 -> Integer
     * @return              输入为null时返回null -> BigDecimal
     */
    private BigDecimal calculatePrecision(Integer sourceValue) {
        return sourceValue == null ? null : new BigDecimal(sourceValue)
                .divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
    }

    private Boolean checkIsDemoApp(String appId) {
        String demoAppIds = diamondConfig.getDemoH5AppIds();
        if (StringUtils.isBlank(demoAppIds)) {
            logger.error("配置中心中demo.h5.appIds属性未配置");
            return false;
        }
        List<String> demoAppIdList = Splitter.on(",").splitToList(demoAppIds);
        return demoAppIdList.contains(appId);
    }
}
