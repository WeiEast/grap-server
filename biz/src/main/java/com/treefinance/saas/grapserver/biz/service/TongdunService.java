package com.treefinance.saas.grapserver.biz.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.treefinance.saas.grapserver.biz.domain.AppLicense;
import com.treefinance.saas.grapserver.common.enums.ETaskStatus;
import com.treefinance.saas.grapserver.common.enums.ETaskStep;
import com.treefinance.saas.grapserver.common.enums.ETongDunSuiShouData;
import com.treefinance.saas.grapserver.common.enums.ETongdunData;
import com.treefinance.saas.grapserver.common.enums.ETongdunDetailData;
import com.treefinance.saas.grapserver.common.enums.ETongdunExtraData;
import com.treefinance.saas.grapserver.common.enums.ETongdunType;
import com.treefinance.saas.grapserver.common.model.TongdunDetailDTO;
import com.treefinance.saas.grapserver.common.model.dto.carinfo.CarInfoCollectTaskLogDTO;
import com.treefinance.saas.grapserver.common.request.TongdunRequest;
import com.treefinance.saas.grapserver.common.result.SaasResult;
import com.treefinance.saas.grapserver.common.result.TongdunData;
import com.treefinance.saas.grapserver.common.result.TongdunDetailData;
import com.treefinance.saas.grapserver.common.result.TongdunDetailResult;
import com.treefinance.saas.grapserver.common.result.TongdunDetailTieshuResult;
import com.treefinance.saas.grapserver.context.component.AbstractService;
import com.treefinance.saas.grapserver.context.config.DiamondConfig;
import com.treefinance.saas.grapserver.util.HttpClientUtils;
import com.treefinance.saas.grapserver.util.TongdunDataResolver;
import com.treefinance.saas.taskcenter.facade.request.CarInfoCollectTaskLogRequest;
import com.treefinance.saas.taskcenter.facade.service.CarInfoFacade;
import com.treefinance.saas.taskcenter.facade.service.TaskFacade;
import com.treefinance.saas.taskcenter.facade.service.TaskLogFacade;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author guoguoyun
 * @date 2018/10/17下午2:22
 */
@Service
public class TongdunService extends AbstractService {

    @Autowired
    private TaskFacade taskFacade;
    @Autowired
    private CarInfoFacade carInfoFacade;
    @Autowired
    private TaskLogFacade taskLogFacade;
    @Autowired
    private TaskLogService taskLogService;
    @Autowired
    private LicenseService licenseService;
    @Autowired
    private DiamondConfig diamondConfig;

    public Object processCollectTask(Long taskId, String appId, TongdunRequest tongdunRequest) {
        String url = diamondConfig.getTongdunUrlCollect();
        Map<String, Object> map = new HashMap<>(1);
        map.put("data", JSON.toJSONString(tongdunRequest));
        String httpResult;
        try {
            httpResult = HttpClientUtils.doPost(url, map);
        } catch (Exception e) {
            logger.error("调用功夫贷同盾采集任务异常:taskId={},tongdunRequset={}", taskId, tongdunRequest, e);
            processFailCollectTask(taskId, "调用功夫贷同盾采集任务异常");
            return SaasResult.failResult("Unexpected exception!");
        }

        JSONObject result = null;
        try {
            result = JSON.parseObject(httpResult);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        if (result == null) {
            logger.error("调用功夫贷同盾采集任务返回值中任务日志信息存在问题:taskId={},tongdunRequest={},httpResult={}", taskId, tongdunRequest, httpResult);
            processFailCollectTask(taskId, "调用功夫贷同盾采集任务返回值中任务日志信息存在问题");
            // 错误日志中
            return SaasResult.failResult("Unexpected exception!");
        }
        // 获取规则评分
        Map<String, Integer> saasRuleScoreDTOMap = getSaasRuleScoreMapFromJson(result);

        // 获取关联手机 身份证 邮箱信息
        JSONObject associated3MDTO = result.getJSONObject("saasAssociated3MDTO");
        // 获取详细数值
        JSONObject summary = result.getJSONObject("saasSummaryDTO");
        ETongdunData[] values = ETongdunData.values();
        List<TongdunData> tongdunDataList = new ArrayList<>(values.length);
        try {
            for (ETongdunData item : values) {
                TongdunData tongdunData = new TongdunData();
                tongdunData.setId(item.getName());
                tongdunData.setValue(TongdunDataResolver.to(summary.getInteger(item.getText())));
                tongdunData.setScore(TongdunDataResolver.to(saasRuleScoreDTOMap.get(item.getText())));
                tongdunData.setDetail(getTongdunDetailData(item, associated3MDTO));
                tongdunDataList.add(tongdunData);
            }

        } catch (Exception e) {
            logger.error("调用功夫贷同盾采集任务返回值中结果存在问题{}", httpResult, e);
            return SaasResult.failResult("查询不到数据!");
        }

        AppLicense license = licenseService.getAppLicense(appId);
        processSuccessCollectTask(taskId, "任务成功");
        return SaasResult.successEncryptByRSAResult(tongdunDataList, license.getServerPublicKey());
    }

    private TongdunDetailData getTongdunDetailData(ETongdunData item, JSONObject associated3MDTO) {
        TongdunDetailData detail = null;
        if (ETongdunData.MI_MOBILE_3W.equals(item)) {
            List<String> list = getList(associated3MDTO, "phoneAssociatedIdentity3M");
            if (!list.isEmpty()) {
                detail = new TongdunDetailData();
                detail.setIdcardNos(list);
            }

        } else if (ETongdunData.ME_IDCARD_3M.equals(item)) {
            List<String> list = getList(associated3MDTO, "identityAssociatedMail3M");
            if (!list.isEmpty()) {
                detail = new TongdunDetailData();
                detail.setMails(list);
            }
        } else if (ETongdunData.MM_IDCARD_3M.equals(item)) {
            List<String> list = getList(associated3MDTO, "identityAssociatedPhone3M");
            if (!list.isEmpty()) {
                detail = new TongdunDetailData();
                detail.setMobiles(list);
            }
        }
        return detail;
    }

    private List<String> getList(JSONObject associated3MDTO, String identityAssociatedMail3M) {
        String value = associated3MDTO.getString(identityAssociatedMail3M);
        if (StringUtils.isEmpty(value)) {
            return Collections.emptyList();
        }
        return Arrays.asList(value.split(","));
    }

    public Object processCollectTieshuDetailTask(Long taskId, String appId, TongdunRequest tongdunRequest) {
        String url = diamondConfig.getTongdunDetailUrlCollect();
        JSONObject data = JSON.parseObject(JSON.toJSONString(tongdunRequest));
        Map<String, Object> map = new HashMap<>(1);
        map.put("data", data);
        String httpResult;
        try {
            httpResult = HttpClientUtils.doPost(url, map);
        } catch (Exception e) {
            logger.error("调用功夫贷同盾采集详细任务异常:taskId={},tongdunRequset={}", taskId, tongdunRequest, e);
            processFailCollectTask(taskId, "调用功夫贷同盾采集详细任务异常");
            return SaasResult.failResult("Unexpected exception!");
        }

        JSONObject result = null;
        try {
            result = JSON.parseObject(httpResult);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        if (result == null) {
            logger.error("调用功夫贷同盾采集详细任务返回值中任务日志信息存在问题:taskId={},tongdunRequest={},httpResult={}", taskId, tongdunRequest, httpResult);
            processFailCollectTask(taskId, "调用功夫贷同盾采集详细任务返回值中任务日志信息存在问题");
            // 错误日志中
            return SaasResult.failResult("Unexpected exception!");
        }

        List<Object> resultList = new ArrayList<>(6);
        JSONObject detail = result.getJSONObject("details");

        // 获取关联手机 身份证 邮箱信息
        JSONObject associated3MDTO = result.getJSONObject("saasAssociated3MDTO");

        // 获取详细数值
        JSONObject summary = result.getJSONObject("saasSummaryDTO");
        ETongdunType[] types = ETongdunType.values();
        List<TongdunDetailTieshuResult> tongdunDataList = new ArrayList<>(5);
        List<TongdunDetailTieshuResult> newtongdunDataList = new ArrayList<>(3);
        try {
            for (int i = 1; i < 6; i++) {
                if (summary.getInteger(ETongdunData.getText((byte)i)) != 0) {
                    TongdunDetailTieshuResult tongdunDetailResult = new TongdunDetailTieshuResult();
                    JSONObject item = detail.getJSONObject(ETongdunData.getText((byte)i));
                    tongdunDetailResult.setId(ETongdunData.getName((byte)i));
                    tongdunDetailResult.setValue(TongdunDataResolver.to(summary.getInteger(ETongdunData.getText((byte)i))));
                    Map<String, Map> firstmap = new HashMap<>();
                    for (ETongdunType eTongdunType : types) {

                        Map<String, String> secondmap = new HashMap<>();
                        JSONObject jsonType;
                        if (!Objects.isNull(item.getJSONObject(eTongdunType.getText()))) {
                            jsonType = item.getJSONObject(eTongdunType.getText());
                        } else if (!Objects.isNull(item.getJSONObject(eTongdunType.getSecondtext()))) {
                            jsonType = item.getJSONObject(eTongdunType.getSecondtext());
                        } else {
                            continue;
                        }
                        for (ETongdunDetailData eTongdunDetailData : ETongdunDetailData.values()) {

                            if (!Objects.isNull(jsonType.get(eTongdunDetailData.getText()))) {
                                secondmap.put(eTongdunDetailData.getName(), TongdunDataResolver.to(jsonType.getInteger(eTongdunDetailData.getText())));
                            } else {
                                secondmap.put(ETongdunDetailData.LevelZ.getName(), TongdunDataResolver.to(jsonType.getInteger(eTongdunDetailData.getText())));
                            }
                        }
                        firstmap.put(eTongdunType.getName(), secondmap);

                    }

                    tongdunDetailResult.setDetails(firstmap);
                    tongdunDataList.add(tongdunDetailResult);
                }
            }

            for (int i = 10; i < 13; i++) {
                TongdunDetailTieshuResult tongdunDetailResult = new TongdunDetailTieshuResult();
                tongdunDetailResult.setId(ETongdunData.getName((byte)i));
                tongdunDetailResult.setDataDetails(getTongdunDetailData(ETongdunData.getETonddunData((byte)i), associated3MDTO));
                newtongdunDataList.add(tongdunDetailResult);

            }
            // 获取黑名单
            Map blackMap = new HashMap(2);
            blackMap.put("id", "IS_BLACK");
            blackMap.put("value", summary.get("isHitDiscreditPolicy"));
            resultList.addAll(tongdunDataList);
            resultList.addAll(newtongdunDataList);
            resultList.add(blackMap);

        } catch (Exception e) {
            return SaasResult.failResult("查询不到数据!");
        }

        AppLicense license = licenseService.getAppLicense(appId);
        processSuccessCollectTask(taskId, "任务成功");
        return SaasResult.successEncryptByRSAResult(resultList, license.getServerPublicKey());

    }

    public Object processCollectDetailTask(Long taskId, String appId, TongdunRequest tongdunRequest) {
        String url = diamondConfig.getTongdunDetailUrlCollect();
        JSONObject data = JSON.parseObject(JSON.toJSONString(tongdunRequest));
        Map<String, Object> map = new HashMap<>(1);
        map.put("data", data);
        String httpResult;
        try {
            httpResult = HttpClientUtils.doPost(url, map);
        } catch (Exception e) {
            logger.error("调用功夫贷同盾采集详细任务异常:taskId={},tongdunRequset={}", taskId, tongdunRequest, e);
            processFailCollectTask(taskId, "调用功夫贷同盾采集详细任务异常");
            return SaasResult.failResult("Unexpected exception!");
        }

        JSONObject result = null;
        try {
            result = JSON.parseObject(httpResult);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        if (result == null) {
            logger.error("调用功夫贷同盾采集详细任务返回值中任务日志信息存在问题:taskId={},tongdunRequest={},httpResult={}", taskId, tongdunRequest, httpResult);
            processFailCollectTask(taskId, "调用功夫贷同盾采集详细任务返回值中任务日志信息存在问题");
            // 错误日志中
            return SaasResult.failResult("Unexpected exception!");
        }

        List<Object> resultList = new ArrayList<>(6);
        JSONObject detail = result.getJSONObject("details");

        // 获取详细数值
        JSONObject summary = result.getJSONObject("saasSummaryDTO");
        ETongdunType[] types = ETongdunType.values();
        List<TongdunDetailResult> tongdunDataList = new ArrayList<>(5);
        try {
            for (int i = 1; i < 6; i++) {
                if (summary.getInteger(ETongdunData.getText((byte)i)) != 0) {
                    TongdunDetailResult tongdunDetailResult = new TongdunDetailResult();
                    JSONObject item = detail.getJSONObject(ETongdunData.getText((byte)i));
                    tongdunDetailResult.setId(ETongdunData.getName((byte)i));
                    tongdunDetailResult.setValue(TongdunDataResolver.to(summary.getInteger(ETongdunData.getText((byte)i))));
                    Map<String, Map> firstmap = new HashMap<>();
                    for (ETongdunType eTongdunType : types) {

                        Map<String, String> secondmap = new HashMap<>();
                        JSONObject jsonType;
                        if (!Objects.isNull(item.getJSONObject(eTongdunType.getText()))) {
                            jsonType = item.getJSONObject(eTongdunType.getText());
                        } else if (!Objects.isNull(item.getJSONObject(eTongdunType.getSecondtext()))) {
                            jsonType = item.getJSONObject(eTongdunType.getSecondtext());
                        } else {
                            continue;
                        }
                        for (ETongdunDetailData eTongdunDetailData : ETongdunDetailData.values()) {

                            if (!Objects.isNull(jsonType.get(eTongdunDetailData.getText()))) {
                                secondmap.put(eTongdunDetailData.getName(), TongdunDataResolver.to(jsonType.getInteger(eTongdunDetailData.getText())));
                            } else {
                                secondmap.put(ETongdunDetailData.LevelZ.getName(), TongdunDataResolver.to(jsonType.getInteger(eTongdunDetailData.getText())));
                            }
                        }
                        firstmap.put(eTongdunType.getName(), secondmap);

                    }
                    tongdunDetailResult.setDetails(firstmap);
                    tongdunDataList.add(tongdunDetailResult);
                }
            }

            // 获取黑名单
            Map blackMap = new HashMap(2);
            blackMap.put("id", "IS_BLACK");
            blackMap.put("value", summary.get("isHitDiscreditPolicy"));
            resultList.addAll(tongdunDataList);
            resultList.add(blackMap);

        } catch (Exception e) {
            return SaasResult.failResult("查询不到数据!");
        }

        AppLicense license = licenseService.getAppLicense(appId);
        processSuccessCollectTask(taskId, "任务成功");
        return SaasResult.successEncryptByRSAResult(resultList, license.getServerPublicKey());
    }

    public Object processCollectDetailTaskV1(Long taskId, String appId, TongdunRequest tongdunRequest) {
        String url = diamondConfig.getTongdunDetailUrlCollect();
        JSONObject data = JSON.parseObject(JSON.toJSONString(tongdunRequest));

        Map<String, Object> map = new HashMap<>(1);
        map.put("data", data);
        String httpResult;
        try {
            httpResult = HttpClientUtils.doPost(url, map);
        } catch (Exception e) {
            logger.error("调用功夫贷同盾采集详细任务异常:taskId={},tongdunRequset={}", taskId, tongdunRequest, e);
            processFailCollectTask(taskId, "调用功夫贷同盾采集任务异常");
            return SaasResult.failResult("Unexpected exception!");
        }

        JSONObject result = null;
        try {
            result = JSON.parseObject(httpResult);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        if (result == null) {
            logger.error("调用功夫贷同盾采集详细任务返回值中任务日志信息存在问题:taskId={},tongdunRequest={},httpResult={}", taskId, tongdunRequest, httpResult);
            processFailCollectTask(taskId, "调用功夫贷同盾采集详细任务返回值中任务日志信息存在问题");
            // 错误日志中
            return SaasResult.failResult("Unexpected exception!");
        }

        List<Object> resultList = new ArrayList<>(6);
        JSONObject detail = result.getJSONObject("details");

        // 获取详细数值
        JSONObject summary = result.getJSONObject("saasSummaryDTO");
        // 获取全部详细数值
        JSONArray detailDTOS = result.getJSONArray("detailDTOS");
        List<TongdunDetailDTO> tongdunDetailDTOS = detailDTOS.toJavaList(TongdunDetailDTO.class);

        ETongdunType[] types = ETongdunType.values();
        List<TongdunDetailResult> tongdunDataList = new ArrayList<>();
        try {
            // 直接返回从同盾输出的字段
            for (int i = 1; i < 6; i++) {

                if (summary.getInteger(ETongDunSuiShouData.getText((byte)i)) != 0) {
                    TongdunDetailResult tongdunDetailResult = new TongdunDetailResult();
                    JSONObject item = detail.getJSONObject(ETongDunSuiShouData.getText((byte)i));
                    tongdunDetailResult.setId(ETongDunSuiShouData.getName((byte)i));
                    tongdunDetailResult.setValue(TongdunDataResolver.to(summary.getInteger(ETongDunSuiShouData.getText((byte)i))));

                    Map<String, Map> firstmap = new HashMap<>();

                    for (ETongdunType eTongdunType : types) {

                        Map<String, String> secondmap = new HashMap<>();
                        JSONObject jsonType;
                        if (!Objects.isNull(item.getJSONObject(eTongdunType.getText()))) {
                            jsonType = item.getJSONObject(eTongdunType.getText());
                        } else if (!Objects.isNull(item.getJSONObject(eTongdunType.getSecondtext()))) {
                            jsonType = item.getJSONObject(eTongdunType.getSecondtext());
                        } else {
                            continue;
                        }

                        for (ETongdunDetailData eTongdunDetailData : ETongdunDetailData.values()) {

                            if (!Objects.isNull(jsonType.get(eTongdunDetailData.getText()))) {
                                secondmap.put(eTongdunDetailData.getName(), TongdunDataResolver.to(jsonType.getInteger(eTongdunDetailData.getText())));
                            } else {
                                secondmap.put(ETongdunDetailData.LevelZ.getName(), TongdunDataResolver.to(jsonType.getInteger(eTongdunDetailData.getText())));
                            }
                        }
                        firstmap.put(eTongdunType.getName(), secondmap);

                    }

                    tongdunDetailResult.setDetails(firstmap);
                    tongdunDataList.add(tongdunDetailResult);
                }
            }

            for (int i = 6; i < 13; i++) {
                TongdunDetailResult tongdunDetailResult = new TongdunDetailResult();
                Map mapString = new HashMap<>();
                Map<String, Map> mapResult = new HashMap<>();
                tongdunDetailResult.setId(ETongDunSuiShouData.getName((byte)i));
                tongdunDetailResult.setValue(TongdunDataResolver.to(summary.getInteger(ETongDunSuiShouData.getText((byte)i))));
                for (int j = 0; j < tongdunDetailDTOS.size(); j++) {
                    if (tongdunDetailDTOS.get(j).getRuleName().contains(ETongDunSuiShouData.getValue((byte)i))) {
                        String ruleHitDetail = tongdunDetailDTOS.get(j).getRuleHitDetail();
                        JSONObject jsonObject = JSONObject.parseObject(ruleHitDetail.substring(1, ruleHitDetail.length() - 1));
                        mapString = jsonObject.toJavaObject(Map.class);
                        if (i < 8) {
                            mapString = convertMap(mapString, "设备或身份证或手机号申请次数过多");

                        } else if (i >= 8 && i < 11) {
                            mapString = convertMap(mapString, "身份证关联设备数");
                        } else if (i == 11) {
                            mapString = convertMap(mapString, "手机关联身份证数");

                        } else if (i > 11) {
                            mapString = convertMap(mapString, "3个月身份证关联多个申请信息");
                        }
                    }
                }
                mapResult.put("detail", mapString);
                tongdunDetailResult.setDetails(mapResult);
                tongdunDataList.add(tongdunDetailResult);
            }

            // 处理返回需要累计的字段
            TongdunDetailResult tongdunDetailResult = tongdunDataList.get(11);
            tongdunDetailResult
                .setValue(TongdunDataResolver.to(summary.getInteger(ETongDunSuiShouData.getText((byte)12)) + summary.getInteger(ETongDunSuiShouData.getText((byte)13))));
            tongdunDataList.set(11, tongdunDetailResult);

            // 加入新增的随手需要额外字段
            tongdunDataList = getTongdunExtraList(summary, tongdunDetailDTOS, tongdunDataList);

            // 获取黑名单
            Map blackMap = new HashMap(2);
            blackMap.put("id", "IS_BLACK");
            blackMap.put("value", summary.get("isHitDiscreditPolicy"));
            resultList.addAll(tongdunDataList);
            resultList.add(blackMap);

        } catch (Exception e) {
            return SaasResult.failResult("查询不到数据!");
        }

        AppLicense license = licenseService.getAppLicense(appId);
        processSuccessCollectTask(taskId, "任务成功");
        return SaasResult.successEncryptByRSAResult(resultList, license.getServerPublicKey());

    }

    private List<TongdunDetailResult> getTongdunExtraList(JSONObject summary, List<TongdunDetailDTO> tongdunDetailDTOS, List<TongdunDetailResult> tongdunDataList) {
        List<Byte> detailCode = new ArrayList<>();
        detailCode.add((byte)6);
        detailCode.add((byte)7);
        detailCode.add((byte)10);

        for (ETongdunExtraData eTongdunExtraData : ETongdunExtraData.values()) {
            TongdunDetailResult tongdunData = new TongdunDetailResult();
            tongdunData.setId(eTongdunExtraData.getName());
            if (StringUtils.isEmpty(summary.getString(eTongdunExtraData.getText()))) {
                tongdunData.setValue("false");
            } else {
                tongdunData.setValue(summary.getString(eTongdunExtraData.getText()));
            }
            Map<String, Map> mapResult = new HashMap<>();
            Map mapString = new HashMap<>();

            if (detailCode.contains(eTongdunExtraData.getCode())) {
                for (int j = 0; j < tongdunDetailDTOS.size(); j++) {
                    if (tongdunDetailDTOS.get(j).getRuleName().contains(eTongdunExtraData.getValue())) {
                        String ruleHitDetail = tongdunDetailDTOS.get(j).getRuleHitDetail();

                        JSONObject jsonObject = JSONObject.parseObject(ruleHitDetail.substring(1, ruleHitDetail.length() - 1));

                        if (eTongdunExtraData.getCode() == 6) {
                            JSONArray lostDetail = new JSONArray();
                            if (!ObjectUtils.isEmpty(jsonObject.getJSONArray("失信详情展示"))) {
                                lostDetail = jsonObject.getJSONArray("失信详情展示");
                            }
                            String overdueCounts = "";
                            String overdueAmounts = "";
                            String overdueDays = "";
                            String loseCredibilityTime = "";
                            String matchField = "";
                            String loseCredibilityType = "";
                            if (!ObjectUtils.isEmpty(lostDetail)) {
                                for (int k = 0; k < lostDetail.size(); k++) {
                                    if (!ObjectUtils.isEmpty(JSONObject.parseObject(lostDetail.get(k).toString()).get("逾期笔数"))) {
                                        overdueCounts = overdueCounts + JSONObject.parseObject(lostDetail.get(k).toString()).get("逾期笔数");
                                    }
                                    if (!ObjectUtils.isEmpty(JSONObject.parseObject(lostDetail.get(k).toString()).get("逾期金额"))) {
                                        overdueAmounts = overdueAmounts + JSONObject.parseObject(lostDetail.get(k).toString()).get("逾期金额");
                                    }
                                    if (!ObjectUtils.isEmpty(JSONObject.parseObject(lostDetail.get(k).toString()).get("逾期天数"))) {
                                        overdueDays = overdueDays + JSONObject.parseObject(lostDetail.get(k).toString()).get("逾期天数");
                                    }
                                }
                            }
                            if (!ObjectUtils.isEmpty(jsonObject.get("失信次数"))) {
                                loseCredibilityTime = jsonObject.get("失信次数").toString();
                            }
                            if (!ObjectUtils.isEmpty(jsonObject.get("匹配字段"))) {
                                matchField = jsonObject.get("匹配字段").toString();
                            }
                            if (!ObjectUtils.isEmpty(jsonObject.get("失信类型"))) {
                                loseCredibilityType = jsonObject.get("失信类型").toString();
                            }
                            mapString.put("overdueCounts", overdueCounts);
                            mapString.put("overdueAmounts", overdueAmounts);
                            mapString.put("overdueDays", overdueDays);
                            mapString.put("loseCredibilityTime", loseCredibilityTime);
                            mapString.put("matchField", matchField);
                            mapString.put("loseCredibilityType", loseCredibilityType);
                            mapString.remove("描述");
                        }
                        if (eTongdunExtraData.getCode() == 7) {

                            String name = "";
                            String cheatType = "";
                            String idCard = "";

                            if (!ObjectUtils.isEmpty(jsonObject.get("姓名"))) {
                                name = jsonObject.get("姓名").toString();
                            }
                            if (!ObjectUtils.isEmpty(jsonObject.get("欺诈类型"))) {
                                cheatType = jsonObject.get("欺诈类型").toString();
                            }
                            if (!ObjectUtils.isEmpty(jsonObject.get("身份证"))) {
                                idCard = jsonObject.get("身份证").toString();
                            }
                            mapString.put("name", name);
                            mapString.put("cheatType", cheatType);
                            mapString.put("idCard", idCard);
                            mapString.remove("描述");

                        }
                        if (eTongdunExtraData.getCode() == 10) {
                            JSONArray lostDetail = new JSONArray();
                            if (!ObjectUtils.isEmpty(jsonObject.getJSONArray("借款人手机"))) {
                                lostDetail = jsonObject.getJSONArray("借款人手机");
                            }
                            String cheatType = "";
                            String lenderMobile = "";
                            if (!ObjectUtils.isEmpty(lostDetail)) {
                                for (int k = 0; k < lostDetail.size(); k++) {
                                    if (!ObjectUtils.isEmpty(JSONObject.parseObject(lostDetail.get(k).toString()).get("欺诈类型"))) {
                                        if (StringUtils.isNotEmpty(cheatType)) {
                                            cheatType = cheatType + "," + JSONObject.parseObject(lostDetail.get(k).toString()).get("欺诈类型");
                                        } else {
                                            cheatType = JSONObject.parseObject(lostDetail.get(k).toString()).get("欺诈类型").toString();

                                        }
                                    }
                                }
                            }
                            if (!ObjectUtils.isEmpty(JSONObject.parseObject(lostDetail.get(0).toString()).get("借款人手机"))) {
                                lenderMobile = JSONObject.parseObject(lostDetail.get(0).toString()).get("借款人手机").toString();

                                mapString.put("cheatType", cheatType);
                                mapString.put("lenderMobile", lenderMobile);
                                mapString.remove("描述");
                            }
                        }
                    }
                    if (!ObjectUtils.isEmpty(mapString)) {
                        mapResult.put("detail", mapString);
                        tongdunData.setDetails(mapResult);
                    }
                }
                tongdunDataList.add(tongdunData);
            }
        }
        return tongdunDataList;
    }

    private Map convertMap(Map mapString, String name) {
        if (("设备或身份证或手机号申请次数过多").equals(name)) {
            if (!ObjectUtils.isEmpty(mapString.get("借款人身份证出现次数"))) {
                mapString.put("lenderIdCardOccurTime", mapString.get("借款人身份证出现次数"));
                mapString.remove("借款人身份证出现次数");
                mapString.remove("描述");
            }
            if (!ObjectUtils.isEmpty(mapString.get("设备ID出现次数"))) {
                mapString.put("deviceOccurTime", mapString.get("设备ID出现次数"));
                mapString.remove("设备ID出现次数");
                mapString.remove("描述");
            }

        }
        if (("手机关联身份证数").equals(name)) {
            if (!ObjectUtils.isEmpty(mapString.get("借款人手机关联借款人身份证数目"))) {
                mapString.put("associatedLenderIdcardList", mapString.get("关联借款人身份证列表"));
                mapString.remove("借款人手机关联借款人身份证数目");
                mapString.remove("关联借款人身份证列表");
                mapString.remove("描述");
            }

        }
        if (("身份证关联设备数").equals(name)) {
            if (!ObjectUtils.isEmpty(mapString.get("借款人身份证关联设备ID数目"))) {
                mapString.remove("借款人身份证关联设备ID数目");
            } else if (!ObjectUtils.isEmpty(mapString.get("账户身份证关联设备ID数目"))) {
                mapString.remove("账户身份证关联设备ID数目");

            }
            mapString.put("associatedDeviceIdList", mapString.get("关联设备ID列表"));
            mapString.remove("关联设备ID列表");
            mapString.remove("描述");

        }
        if (("3个月身份证关联多个申请信息").equals(name)) {
            if (!ObjectUtils.isEmpty(mapString.get("账户身份证关联登录手机数目"))) {
                mapString.put("associatedLoginMobilCount", TongdunDataResolver.to((Integer)mapString.get("账户身份证关联登录手机数目")));
                mapString.put("associatedLoginMobileList", mapString.get("关联登录手机列表"));
                mapString.remove("关联登录手机列表");
                mapString.remove("账户身份证关联登录手机数目");
            } else if (!ObjectUtils.isEmpty(mapString.get("账户身份证关联登录邮箱数目"))) {
                mapString.put("associatedLoginEmailCount", TongdunDataResolver.to((Integer)mapString.get("账户身份证关联登录邮箱数目")));
                mapString.put("associatedLoginEmailList", mapString.get("关联登录邮箱列表"));
                mapString.remove("关联登录邮箱列表");
                mapString.remove("账户身份证关联登录邮箱数目");
            } else if (!ObjectUtils.isEmpty(mapString.get("借款人身份证关联借款人手机数目"))) {
                mapString.put("associatedLenderMobileCount", TongdunDataResolver.to((Integer)mapString.get("借款人身份证关联借款人手机数目")));
                mapString.put("associatedLenderMobileList", mapString.get("关联借款人手机列表"));
                mapString.remove("关联借款人手机列表");
                mapString.remove("借款人身份证关联借款人手机数目");
            } else if (!ObjectUtils.isEmpty(mapString.get("借款人身份证关联借款人邮箱数目"))) {
                mapString.put("associatedLenderEmailCount", TongdunDataResolver.to((Integer)mapString.get("借款人身份证关联借款人邮箱数目")));
                mapString.put("associatedLenderEmailList", mapString.get("关联借款人邮箱列表"));
                mapString.remove("关联借款人邮箱列表");
                mapString.remove("借款人身份证关联借款人邮箱数目");

            }
            mapString.remove("描述");

        }
        return mapString;

    }

    public void updateCollectTaskStatusAndTaskLogAndSendMonitor(Long taskId, List<CarInfoCollectTaskLogDTO> logList) {
        List<CarInfoCollectTaskLogRequest> carInfoCollectTaskLogRequestList = convert(logList, CarInfoCollectTaskLogRequest.class);
        carInfoFacade.updateCollectTaskStatusAndTaskLogAndSendMonitor(taskId, carInfoCollectTaskLogRequestList);
    }

    private void processFailCollectTask(Long taskId, String failMsg) {
        List<CarInfoCollectTaskLogDTO> carInfoCollectTaskLogDTOList = Lists.newArrayList();
        carInfoCollectTaskLogDTOList.add(new CarInfoCollectTaskLogDTO(ETaskStep.TASK_FAIL.getText(), failMsg, new Date()));
        this.updateCollectTaskStatusAndTaskLogAndSendMonitor(taskId, carInfoCollectTaskLogDTOList);
    }

    private Boolean processSuccessCollectTask(Long taskId, String resultLog) {
        List<CarInfoCollectTaskLogDTO> logList = Lists.newArrayList();
        logList.add(new CarInfoCollectTaskLogDTO(ETaskStep.TASK_SUCCESS.getText(), resultLog, new Date()));
        this.updateCollectTaskStatusAndTaskLogAndSendMonitor(taskId, logList);
        for (CarInfoCollectTaskLogDTO log : logList) {
            if (StringUtils.equalsIgnoreCase(log.getMsg(), ETaskStep.TASK_SUCCESS.getText())) {
                return true;
            }
        }
        return false;
    }

    private Map<String, Integer> getSaasRuleScoreMapFromJson(JSONObject result) {
        Map<String, Integer> saasRuleScoreDTOMap = new HashMap<>(8);
        JSONArray scores = result.getJSONArray("saasRuleScoreDTO");
        for (int i = 0, size = scores.size(); i < size; i++) {
            JSONObject item = scores.getJSONObject(i);
            saasRuleScoreDTOMap.put(item.getString("ruleName"), item.getInteger("policyScore"));
        }
        return saasRuleScoreDTOMap;
    }
}