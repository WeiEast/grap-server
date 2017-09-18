package com.treefinance.saas.grapserver.biz.service.moxie;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.treefinance.saas.grapserver.biz.config.DiamondConfig;
import com.treefinance.saas.grapserver.common.model.dto.moxie.MoxieCityInfoDTO;
import com.treefinance.saas.grapserver.common.utils.HttpClientUtils;
import com.treefinance.saas.grapserver.common.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by haojiahong on 2017/9/13.
 */
@Service
public class FundMoxieService {

    @Autowired
    private DiamondConfig diamondConfig;

    public List<MoxieCityInfoDTO> queryCityList() {
        String url = diamondConfig.getMoxieUrlFundGetCityList();
        Map<String, String> headers = Maps.newHashMap();
        wrapperHeaders(headers);
        String data = HttpClientUtils.doGetWithHeaders(url, headers);
        List<MoxieCityInfoDTO> moxieCityInfoDTOList = JsonUtils.toJavaBeanList(data, MoxieCityInfoDTO.class);
        return moxieCityInfoDTOList;
    }

    public List<MoxieCityInfoDTO> queryCityListEx() {
        String url = diamondConfig.getMoxieUrlFundGetCityListEx();
        Map<String, String> headers = Maps.newHashMap();
        wrapperHeaders(headers);
        String data = HttpClientUtils.doGetWithHeaders(url, headers);
        List<MoxieCityInfoDTO> moxieCityInfoDTOList = JsonUtils.toJavaBeanList(data, MoxieCityInfoDTO.class);
        return moxieCityInfoDTOList;

    }

    public Object queryLoginElementsEx(String areaCode) {
        String url = diamondConfig.getMoxieUrlFundGetLoginElementsEx();
        url = url.replaceAll("\\{area_code\\}", areaCode);
        Map<String, String> headers = Maps.newHashMap();
        wrapperHeaders(headers);
        String data = HttpClientUtils.doGetWithHeaders(url, headers);
        return JsonUtils.toJsonObjectList(data);
    }

    public Object queryInformation(String areaCode) {
        String url = diamondConfig.getMoxieUrlFundGetInformation();
        url = url.replaceAll("\\{area_code\\}", areaCode);
        Map<String, String> headers = Maps.newHashMap();
        wrapperHeaders(headers);
        String data = HttpClientUtils.doGetWithHeaders(url, headers);
        return JsonUtils.toJsonObject(data);
    }

    public String createTasks(String uniqueId, String areaCode, String account, String password, String loginType,
                              String idCard, String mobile, String realName, String subArea, String loanAccount,
                              String loanPassword, String corpAccount, String corpName, String origin, String ip) {
        String url = diamondConfig.getMoxieUrlFundPostTasks();
        Map<String, Object> params = Maps.newHashMap();
        Map<String, String> headers = Maps.newHashMap();
        wrapperParams(params, uniqueId, areaCode, account, password, loginType, idCard, mobile, realName, subArea,
                loanAccount, loanPassword, corpAccount, corpName, origin, ip);
        wrapperHeaders(headers);
        String data = HttpClientUtils.doPostWithHeaders(url, params, headers);
        JSONObject result = (JSONObject) JsonUtils.toJsonObject(data);
        String taskId = result.getString("task_id");
        if (taskId != null) {
            return taskId;
        }
        return null;
    }

    public Object queryTaskStatus(String moxieTaskId) {
        String url = diamondConfig.getMoxieUrlFundGetTasksStatus();
        url = url.replaceAll("\\{task_id\\}", moxieTaskId);
        Map<String, String> headers = Maps.newHashMap();
        wrapperHeaders(headers);
        String data = HttpClientUtils.doGetWithHeaders(url, headers);
        return JsonUtils.toJsonObject(data);
    }

    public void submitTaskInput(String moxieTaskId, String input) {
        String url = diamondConfig.getMoxieUrlFundPostTasksInput();
        url = url.replaceAll("\\{task_id\\}", moxieTaskId);
        Map<String, String> headers = Maps.newHashMap();
        wrapperHeaders(headers);
        Map<String, Object> params = Maps.newHashMap();
        params.put("input", input);
        HttpClientUtils.doPostWithHeaders(url, params, headers);
    }

    private void wrapperHeaders(Map<String, String> headers) {
        StringBuilder sb = new StringBuilder();
        sb.append("apiKey").append(" ").append(diamondConfig.getMoxieFundApiKey());
        headers.put("Authorization", sb.toString());
    }

    private void wrapperParams(Map<String, Object> params, String uniqueId, String areaCode, String account,
                               String password, String loginType, String idCard, String mobile, String realName,
                               String subArea, String loanAccount, String loanPassword, String corpAccount,
                               String corpName, String origin, String ip) {

        params.put("user_id", uniqueId);
        params.put("area_code", areaCode);
        params.put("account", account);
        params.put("login_type", loginType);
        params.put("origin", origin);
        if (StringUtils.isNotBlank(password)) {
            params.put("password", password);
        }
        if (StringUtils.isNotBlank(idCard)) {
            params.put("id_card", idCard);
        }
        if (StringUtils.isNotBlank(mobile)) {
            params.put("mobile", mobile);
        }
        if (StringUtils.isNotBlank(realName)) {
            params.put("real_name", realName);
        }
        if (StringUtils.isNotBlank(subArea)) {
            params.put("sub_area", subArea);
        }
        if (StringUtils.isNotBlank(loanAccount)) {
            params.put("loan_account", loanAccount);
        }
        if (StringUtils.isNotBlank(loanPassword)) {
            params.put("loan_password", loanPassword);
        }
        if (StringUtils.isNotBlank(corpAccount)) {
            params.put("corp_account", corpAccount);
        }
        if (StringUtils.isNotBlank(corpName)) {
            params.put("corp_name", corpName);
        }
        if (StringUtils.isNotBlank(ip)) {
            params.put("ip", ip);
        }

    }


}
