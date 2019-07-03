package com.treefinance.saas.grapserver.biz.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.treefinance.saas.grapserver.context.config.DiamondConfig;
import com.treefinance.saas.grapserver.exception.RequestFailedException;
import com.treefinance.saas.grapserver.util.HttpClientUtils;
import com.treefinance.saas.knife.result.SimpleResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author guoguoyun
 * @date 2019/7/3上午11:08
 */
@Service
public class GfdMessageService {
    private final static Logger logger = LoggerFactory.getLogger(GfdMessageService.class);
    private final static  String code ="code";

    @Autowired
    private DiamondConfig diamondConfig;

    public SimpleResult getProtocolUrl(String uniqueld) {
        logger.info("获取协议接口 传入参数uniqueld为{}", uniqueld);
        if(StringUtils.isEmpty(uniqueld)){
            return SimpleResult.failResult("uniqueld不能为空");
        }
        String url = diamondConfig.getGfdH5taobaoUrlgetProtocol();

        Map<String, Object> params = new HashMap<>();
        List<Map<String, String>> resultList = new ArrayList<>();
        JSONObject data = new JSONObject();
        String result ;
        JSONArray protocolList = new JSONArray();
        params.put("userId", uniqueld);
        params.put("source", "H5");
        try {
            result = HttpClientUtils.doPost(url, params);
            logger.info("调用功夫贷网关获取协议接口，返回结果为{}", result);
        } catch (Exception e) {
            logger.error("调用功夫贷网关获取协议接口异常", e);
            return SimpleResult.failResult("调用获取协议接口异常");
        }
        if (result != null) {
            if (JSONObject.parseObject(result).getInteger(code) != 0) {
                logger.warn("调用功夫贷网关获取协议接口错误，错误信息为{}", JSONObject.parseObject(result).getString("errorMsg"));
                return SimpleResult.failResult(JSONObject.parseObject(result).getString("errorMsg"));
            }
            data = JSONObject.parseObject(result).getJSONObject("data");
        }
        if (data != null) {
            protocolList = data.getJSONArray("protocolList");
        }
        for (int i = 0; i < protocolList.size(); i++) {
            Map<String, String> map = new HashMap<>(2);
            map.put("name", protocolList.getJSONObject(i).getString("name"));
            map.put("url", protocolList.getJSONObject(i).getString("url"));
            resultList.add(map);
        }
        return SimpleResult.successResult(resultList);
    }

    public Object signProtocol(String uniqueld) {
        logger.info("签署协议接口 传入参数uniqueld为{}", uniqueld);
        if(StringUtils.isEmpty(uniqueld)){
            return SimpleResult.failResult("uniqueld不能为空");
        }
        String url = diamondConfig.getGfdH5taobaoUrlsignProtocol();

        Map<String, Object> params = new HashMap<>();
        params.put("userId", uniqueld);
        String result;
        params.put("source", "H5");
        try {
             result = HttpClientUtils.doPost(url, params);
            logger.info("调用功夫贷网关签署协议接口，返回结果为{}", result);
        } catch (Exception e) {
            logger.error("调用功夫贷网关签署 协议接口异常", e);
            return SimpleResult.failResult(null);
        }
        if (result != null) {
            if (JSONObject.parseObject(result).getInteger(code) != 0) {
                return SimpleResult.failResult(JSONObject.parseObject(result).getString("errorMsg"));
            }
        }
        return SimpleResult.successResult("success");
    }

}
