package com.treefinance.saas.grapserver.biz.service.moxie;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.treefinance.saas.grapserver.common.model.dto.moxie.MoxieCityInfoDTO;
import com.treefinance.saas.grapserver.common.model.dto.moxie.MoxieLoginParamsDTO;
import com.treefinance.saas.grapserver.util.HttpClientUtils;
import com.treefinance.saas.grapserver.context.config.DiamondConfig;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author haojiahong on 2017/9/13.
 */
@Service
public class FundMoxieService {

    @Autowired
    private DiamondConfig diamondConfig;

    /**
     * 获取城市公积金列表
     */
    public List<MoxieCityInfoDTO> queryCityList() {
        String url = diamondConfig.getMoxieUrlFundGetCityList();
        Map<String, String> headers = Maps.newHashMap();
        wrapperApiKeyHeaders(headers);
        String data = HttpClientUtils.doGetWithHeaders(url, headers);
        return JSON.parseArray(data, MoxieCityInfoDTO.class);
    }

    /**
     * 获取城市公积金列表(省市关联接口，含province)
     */
    public List<MoxieCityInfoDTO> queryCityListEx() {
        String url = diamondConfig.getMoxieUrlFundGetCityListEx();
        Map<String, String> headers = Maps.newHashMap();
        wrapperApiKeyHeaders(headers);
        String data = HttpClientUtils.doGetWithHeaders(url, headers);
        return JSON.parseArray(data, MoxieCityInfoDTO.class);
    }

    /**
     * 根据area_code获取当前公积金UI登录元素
     */
    public Object queryLoginElementsEx(String areaCode) {
        String url = diamondConfig.getMoxieUrlFundGetLoginElementsEx();
        url = url.replaceAll("\\{area_code}", areaCode);
        Map<String, String> headers = Maps.newHashMap();
        wrapperApiKeyHeaders(headers);
        String data = HttpClientUtils.doGetWithHeaders(url, headers);
        return JSONObject.parseObject(data);
    }

    /**
     * 获取公积金登录提示信息(根据area_code查询)
     */
    public Object queryInformation(String areaCode) {
        String url = diamondConfig.getMoxieUrlFundGetInformation();
        url = url.replaceAll("\\{area_code}", areaCode);
        Map<String, String> headers = Maps.newHashMap();
        wrapperApiKeyHeaders(headers);
        String data = HttpClientUtils.doGetWithHeaders(url, headers);
        return JSONObject.parseObject(data);
    }

    /**
     * 获取公积金采集任务执行状态
     */
    public Object queryTaskStatus(String moxieTaskId) {
        String url = diamondConfig.getMoxieUrlFundGetTasksStatus();
        url = url.replaceAll("\\{task_id}", moxieTaskId);
        Map<String, String> headers = Maps.newHashMap();
        wrapperApiKeyHeaders(headers);
        String data = HttpClientUtils.doGetWithHeaders(url, headers);
        return JSON.parseObject(data);
    }

    /**
     * 创建公积金采集任务
     */
    public String createTasks(MoxieLoginParamsDTO moxieLoginParams) {
        String url = diamondConfig.getMoxieUrlFundPostTasks();
        Map<String, Object> params = Maps.newHashMap();
        Map<String, String> headers = Maps.newHashMap();
        wrapperParams(params, moxieLoginParams);
        wrapperApiKeyHeaders(headers);
        String data = HttpClientUtils.doPostWithHeaders(url, params, headers);
        JSONObject result = JSON.parseObject(data);
        return result.getString("task_id");
    }

    /**
     * 输入图片验证码/短信
     */
    public void submitTaskInput(String moxieTaskId, String input) {
        String url = diamondConfig.getMoxieUrlFundPostTasksInput();
        url = url.replaceAll("\\{task_id}", moxieTaskId);
        Map<String, String> headers = Maps.newHashMap();
        wrapperApiKeyHeaders(headers);
        Map<String, Object> params = Maps.newHashMap();
        params.put("input", input);
        HttpClientUtils.doPostWithHeaders(url, params, headers);
    }

    /**
     * 根据task_id获取公积金全部信息
     */
    public String queryFunds(String moxieTaskId) {
        String url = diamondConfig.getMoxieUrlFundGetFunds();
        url = url.replaceAll("\\{task_id}", moxieTaskId);
        Map<String, String> headers = Maps.newHashMap();
        wrapperTokenHeaders(headers);
        return HttpClientUtils.doGetWithHeaders(url, headers);
    }

    /**
     * 根据task_id获取公积金全部信息(扩展接口，含area_code和city)
     */
    public String queryFundsEx(String moxieTaskId) {
        String url = diamondConfig.getMoxieUrlFundGetFundsEx();
        url = url.replaceAll("\\{task_id}", moxieTaskId);
        Map<String, String> headers = Maps.newHashMap();
        wrapperTokenHeaders(headers);
        return HttpClientUtils.doGetWithHeaders(url, headers);
    }


    private void wrapperTokenHeaders(Map<String, String> headers) {
        headers.put("Authorization", "token" + " " + diamondConfig.getMoxieFundToken());
    }

    private void wrapperApiKeyHeaders(Map<String, String> headers) {
        headers.put("Authorization", "apiKey" + " " + diamondConfig.getMoxieFundApiKey());
    }

    private void wrapperParams(Map<String, Object> params, MoxieLoginParamsDTO moxieLoginParams) {
        params.put("user_id", moxieLoginParams.getTaskId());
        params.put("area_code", moxieLoginParams.getAreaCode());
        params.put("account", moxieLoginParams.getAccount());
        params.put("login_type", moxieLoginParams.getLoginType());
        params.put("origin", moxieLoginParams.getOrigin());
        if (StringUtils.isNotBlank(moxieLoginParams.getPassword())) {
            params.put("password", moxieLoginParams.getPassword());
        }
        if (StringUtils.isNotBlank(moxieLoginParams.getIdCard())) {
            params.put("id_card", moxieLoginParams.getIdCard());
        }
        if (StringUtils.isNotBlank(moxieLoginParams.getMobile())) {
            params.put("mobile", moxieLoginParams.getMobile());
        }
        if (StringUtils.isNotBlank(moxieLoginParams.getRealName())) {
            params.put("real_name", moxieLoginParams.getRealName());
        }
        if (StringUtils.isNotBlank(moxieLoginParams.getSubArea())) {
            params.put("sub_area", moxieLoginParams.getSubArea());
        }
        if (StringUtils.isNotBlank(moxieLoginParams.getLoanAccount())) {
            params.put("loan_account", moxieLoginParams.getLoanAccount());
        }
        if (StringUtils.isNotBlank(moxieLoginParams.getLoanPassword())) {
            params.put("loan_password", moxieLoginParams.getLoanPassword());
        }
        if (StringUtils.isNotBlank(moxieLoginParams.getCorpAccount())) {
            params.put("corp_account", moxieLoginParams.getCorpAccount());
        }
        if (StringUtils.isNotBlank(moxieLoginParams.getCorpName())) {
            params.put("corp_name", moxieLoginParams.getCorpName());
        }
        if (StringUtils.isNotBlank(moxieLoginParams.getIp())) {
            params.put("ip", moxieLoginParams.getIp());
        }
    }

}
