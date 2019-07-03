package com.treefinance.saas.grapserver.biz.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.treefinance.saas.grapserver.util.HttpClientUtils;
import com.treefinance.saas.knife.result.SimpleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public List<Map<String, String>> getProtocolUrl(String uniqueld) {
        String url = "http://test.91gfd.com.cn/gongfudaiv2/basics/idcard/peopleBankCredit/getProtocol";

        Map<String, Object> params = new HashMap<>();
        List<Map<String, String>> resultList = new ArrayList<>();
        String result = null;
        params.put("userId", uniqueld);
        params.put("source", "H5");
        try {
            result = HttpClientUtils.doPost(url, params);
        } catch (Exception e) {
            logger.error("调用功夫贷网关获取协议接口异常，异常信息为{}", e);
            return resultList;
        }
        try {
            JSONObject data = JSONObject.parseObject(result).getJSONObject("data");
            JSONArray protocolList = data.getJSONArray("protocolList");
            for (int i = 0; i < protocolList.size(); i++) {
                Map<String, String> map = new HashMap<>(2);
                map.put("name", protocolList.getJSONObject(i).getString("name"));
                map.put("url", protocolList.getJSONObject(i).getString("url"));
                resultList.add(map);
            }
        } catch (NullPointerException e) {
            logger.error("解析协议出现空指针异常，接口返回结果信息为{}", result);
        }

        return resultList;
    }

    public Object signProtocol(String uniqueld) {
        String url = "http://test.91gfd.com.cn/gongfudaiv2/basics/idcard/peopleBankCredit/signProtocol";

        Map<String, Object> params = new HashMap<>();
        params.put("userId", uniqueld);
        params.put("source", "H5");
        try {
            String result = HttpClientUtils.doPost(url, params);
        } catch (Exception e) {
            logger.error("调用功夫贷网关获取协议接口异常，异常信息为{}",e);
            return SimpleResult.failResult(null);
        }
        return SimpleResult.successResult("success");
    }

}
