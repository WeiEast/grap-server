package com.treefinance.saas.grapserver.biz.service;

import com.google.common.collect.Lists;
import com.treefinance.saas.assistant.httpinvoker.HttpInvoker;
import com.treefinance.saas.assistant.httpinvoker.domain.HttpConfig;
import com.treefinance.saas.assistant.httpinvoker.domain.HttpData;
import com.treefinance.saas.grapserver.biz.config.DiamondConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by haojiahong on 2017/9/13.
 */
@Service
public class FundMoxieService {

    @Autowired
    private DiamondConfig diamondConfig;

    private static HttpInvoker HTTP_INVOKER = new HttpInvoker(20, "moxie");

    public Object queryAllFundConfig(HttpServletRequest request, HttpServletResponse response) {
        String url = diamondConfig.getMoxieUrlFundGetCityList();
        HttpConfig config = new HttpConfig();
        config.setMaxTimeout(5);
        config.setExcludeParams(Lists.newArrayList("appid"));
        config.setExcludeHeaders(Lists.newArrayList("Content-Length"));

        HttpData data = HTTP_INVOKER.invokeAndGetResponse(url, config, request, response);
        System.out.println(data.getBody());
        return null;
    }
}
