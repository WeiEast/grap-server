/*
 * Copyright © 2015 - 2017 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.treefinance.saas.grapserver.util;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.treefinance.saas.grapserver.common.exception.RequestFailedException;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * httpclient 调用工具类
 * 
 * @author yh-treefinance on 2017/5/17.
 */
public class HttpClientUtils {

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);

    /**
     * 连接池
     */
    private static PoolingHttpClientConnectionManager connMgr;

    /**
     * 超时时间
     */
    private static final int MAX_TIMEOUT = 3000;

    static {
        // 采用绕过验证的方式处理https请求
        SSLContext sslcontext;
        try {
            sslcontext = createIgnoreVerifySSL();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // 设置协议http和https对应的处理socket链接工厂的对象
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create().register("http", PlainConnectionSocketFactory.INSTANCE)
            .register("https", new SSLConnectionSocketFactory(sslcontext)).build();
        // 设置连接池
        connMgr = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        // 设置连接池大小
        connMgr.setMaxTotal(500);
        connMgr.setDefaultMaxPerRoute(500);
    }

    /**
     * 绕过验证
     */
    public static SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sc = SSLContext.getInstance("SSLv3");
        // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] paramArrayOfX509Certificate, String paramString) {}

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] paramArrayOfX509Certificate, String paramString) {}

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        sc.init(null, new TrustManager[] {trustManager}, null);
        return sc;
    }

    /**
     * 获取默认配置
     */
    private static RequestConfig getDefaultConfig() {
        return RequestConfig.custom().setCookieSpec(CookieSpecs.BEST_MATCH).setExpectContinueEnabled(true).setStaleConnectionCheckEnabled(true)
            .setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST)).setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC)).build();
    }

    /**
     * 获取默认配置
     */
    private static RequestConfig getBaseConfig() {
        RequestConfig defaultRequestConfig = getDefaultConfig();
        return RequestConfig.copy(defaultRequestConfig).setSocketTimeout(MAX_TIMEOUT).setConnectTimeout(MAX_TIMEOUT).setConnectionRequestTimeout(MAX_TIMEOUT).build();
    }

    /**
     * 获取默认配置
     * 
     * @param timeOut 超时时间
     */
    private static RequestConfig getConfigWithTimeOut(int timeOut) {
        RequestConfig defaultRequestConfig = getDefaultConfig();
        return RequestConfig.copy(defaultRequestConfig).setSocketTimeout(timeOut).setConnectTimeout(timeOut).setConnectionRequestTimeout(timeOut).build();
    }

    /**
     * 获取默认client
     */
    private static CloseableHttpClient getClient() {
        return HttpClients.custom().setConnectionManager(connMgr).setRetryHandler((e, n, c) -> false).build();
    }

    /**
     * 获取重试默认client
     */
    private static CloseableHttpClient getRetryClient(String url, Byte retryTimes) {

        return HttpClients.custom().setConnectionManager(connMgr).setRetryHandler((e, n, c) -> {
            logger.info("request {} failed : error={}, retry {} （max {} times）...", url, e.getMessage(), n, retryTimes);
            return retryTimes != null && retryTimes.intValue() > n;
        }).build();
    }

    /**
     * 发送 GET 请求（HTTP），不带输入数据
     */
    public static String doGet(String url) {
        return doGet(url, Maps.newHashMap());
    }

    /**
     * @return 方法出现异常时可能会返回【null】
     */
    public static String doGetWithHeaders(String url, Map<String, String> headers) {
        return doGetWithHeaders(url, Maps.newHashMap(), headers);
    }

    public static String doGetWithHeaders(String url, HashMap<String, Object> params, Map<String, String> headers) {
        long start = System.currentTimeMillis();

        List<String> paramList = Lists.newArrayList();
        for (String key : params.keySet()) {
            paramList.add(key + "=" + params.get(key));
        }
        String apiUrl = url + (url.contains("?") ? "&" : "?") + Joiner.on("&").join(paramList);
        String result = null;
        CloseableHttpClient httpclient = getClient();
        CloseableHttpResponse response = null;
        int statusCode = 0;
        try {
            HttpGet httpGet = new HttpGet(apiUrl);
            httpGet.setConfig(getBaseConfig());

            List<Header> headerList = Lists.newArrayList();
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                Header header = new BasicHeader(entry.getKey(), entry.getValue());
                headerList.add(header);
            }
            Header[] headerArray = new Header[headerList.size()];
            headerArray = headerList.toArray(headerArray);
            httpGet.setHeaders(headerArray);

            response = httpclient.execute(httpGet);
            statusCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream ins = entity.getContent();
                result = IOUtils.toString(ins, "UTF-8");
            }
            if (statusCode != HttpStatus.SC_OK) {
                throw new RequestFailedException(apiUrl, statusCode, result);
            }
        } catch (IOException e) {
            throw new RequestFailedException(apiUrl, statusCode, result, e);
        } finally {
            if (logger.isInfoEnabled()) {
                logger.info(" doGet completed: url={}, params={}, statusCode={} , result={} , cost {} ms ", url, JSON.toJSONString(params), statusCode, result,
                    (System.currentTimeMillis() - start));
            }
            closeResponse(response);
        }
        return result;
    }

    /**
     * 发送 GET 请求（HTTP），K-V形式
     * 
     * @param url 请求路径
     * @param params 请求参数
     * @return responseEntity
     */
    public static String doGet(String url, Map<String, Object> params) {
        long start = System.currentTimeMillis();

        List<String> paramList = Lists.newArrayList();
        for (String key : params.keySet()) {
            paramList.add(key + "=" + params.get(key));
        }
        String apiUrl = url + (url.contains("?") ? "&" : "?") + Joiner.on("&").join(paramList);
        String result = null;
        CloseableHttpClient httpclient = getClient();
        CloseableHttpResponse response = null;
        int statusCode = 0;
        try {
            HttpGet httpGet = new HttpGet(apiUrl);
            httpGet.setConfig(getBaseConfig());
            response = httpclient.execute(httpGet);
            statusCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream ins = entity.getContent();
                result = IOUtils.toString(ins, "UTF-8");
            }
            if (statusCode != HttpStatus.SC_OK) {
                throw new RequestFailedException(apiUrl, statusCode, result);
            }
        } catch (IOException e) {
            throw new RequestFailedException(apiUrl, statusCode, result, e);
        } finally {
            if (logger.isInfoEnabled()) {
                logger.info(" doGet completed: url={}, params={}, statusCode={} , result={} , cost {} ms ", url, JSON.toJSONString(params), statusCode, result,
                    (System.currentTimeMillis() - start));
            }
            closeResponse(response);
        }
        return result;
    }

    /**
     * 发送 GET 请求（HTTP），K-V形式
     * 
     * @param url 请求路径
     * @param params 请求参数
     * @return responseEntity
     */
    public static String doGetWithTimeoutAndRetryTimes(String url, Byte timeOut, Byte retryTimes, Map<String, Object> params) {
        long start = System.currentTimeMillis();

        List<String> paramList = Lists.newArrayList();
        for (String key : params.keySet()) {
            paramList.add(key + "=" + params.get(key));
        }
        String apiUrl = url + (url.contains("?") ? "&" : "?") + Joiner.on("&").join(paramList);
        String result = "";
        CloseableHttpClient httpclient = getRetryClient(apiUrl, retryTimes);
        CloseableHttpResponse response = null;
        int statusCode = 0;
        try {
            HttpGet httpGet = new HttpGet(apiUrl);
            httpGet.setConfig(getConfigWithTimeOut(timeOut * 1000));
            response = httpclient.execute(httpGet);
            statusCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream ins = entity.getContent();
                result = IOUtils.toString(ins, "UTF-8");
            }
            if (statusCode != HttpStatus.SC_OK) {
                throw new RequestFailedException(url, statusCode, result);
            }
        } catch (IOException e) {
            logger.error("doGet exception:url={}, params={}, statusCode={} , result={}", apiUrl, JSON.toJSONString(params), statusCode, result, e);
            throw new RequestFailedException(url, statusCode, result, e);
        } finally {
            if (logger.isInfoEnabled()) {
                logger.info(" doGet completed: url={}, params={}, statusCode={} , result={} , cost {} ms ", apiUrl, JSON.toJSONString(params), statusCode, result,
                    (System.currentTimeMillis() - start));
            }
            closeResponse(response);
        }
        return result;
    }

    /**
     * 发送 POST 请求（HTTP）
     */
    public static String doPost(String url) {
        return doPost(url, Maps.newHashMap());
    }

    /**
     * 发送 POST 请求（HTTP）
     * 
     * @param url 请求路径
     * @param timeOut 秒
     * @param retryTimes 重试次数
     * @param params 参数
     * @return responseEntity，方法出现异常时返回【""】（空字符串）
     */
    public static String doPostWithTimeoutAndRetryTimes(String url, Byte timeOut, Byte retryTimes, Map<String, Object> params) {
        long start = System.currentTimeMillis();
        CloseableHttpClient httpClient = getRetryClient(url, retryTimes);
        String result = "";
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;

        int statusCode = 0;
        StringBuilder paramsBu = new StringBuilder();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            paramsBu.append(entry.getKey()).append("=").append(entry.getValue());
        }
        try {
            httpPost.setConfig(getConfigWithTimeOut(timeOut * 1000));
            StringEntity stringEntity = new StringEntity(paramsBu.toString(), "UTF-8");
            stringEntity.setContentType("application/x-www-form-urlencoded");

            httpPost.setEntity(stringEntity);
            response = httpClient.execute(httpPost);

            statusCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                result = EntityUtils.toString(entity, "UTF-8");
            }
            if (statusCode != HttpStatus.SC_OK) {
                throw new RequestFailedException(url, statusCode, result);
            }
        } catch (IOException e) {
            logger.error("doPostWithTimeoutAndRetryTimes exception: url={},timeout={}s,retryTimes={},params={},statusCode={},result={}", url, timeOut, retryTimes,
                JSON.toJSONString(params), statusCode, result, e);
            throw new RequestFailedException(url, statusCode, result, e);
        } finally {
            if (logger.isInfoEnabled()) {
                logger.info(" doPostWithTimeoutAndRetryTimes completed: url={},timeout={}s,retryTimes={},params={},statusCode={},result={},cost {} ms ", url, timeOut, retryTimes,
                    JSON.toJSONString(params), statusCode, result, (System.currentTimeMillis() - start));
            }
            closeResponse(response);
        }
        return result;

    }

    /**
     * 发送 POST 请求（HTTP），K-V形式
     * 
     * @param url 请求路径
     * @param params 参数map
     * @return responseEntity
     */
    public static String doPost(String url, Map<String, Object> params) {
        long start = System.currentTimeMillis();
        CloseableHttpClient httpClient = getClient();
        String result = null;
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;

        int statusCode = 0;
        try {
            httpPost.setConfig(getBaseConfig());
            List<NameValuePair> pairList = new ArrayList<>(params.size());
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue().toString());
                pairList.add(pair);
            }
            httpPost.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName("UTF-8")));
            response = httpClient.execute(httpPost);

            statusCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                result = EntityUtils.toString(entity, "UTF-8");
            }
            if (statusCode != HttpStatus.SC_OK) {
                throw new RequestFailedException(url, statusCode, result);
            }
        } catch (IOException e) {
            throw new RequestFailedException(url, statusCode, result, e);
        } finally {
            if (logger.isInfoEnabled()) {
                logger.info(" doPost completed: url={}, data={}, statusCode={} ,result={}, cost {} ms ", url, JSON.toJSONString(params), statusCode, result,
                    (System.currentTimeMillis() - start));
            }
            closeResponse(response);
        }
        return result;
    }

    /**
     * 发送 POST 请求（HTTP），K-V形式
     * 
     * @param url 请求路径
     * @param params 参数map
     * @return responseEntity
     */
    public static String doPostWithHeaders(String url, Map<String, Object> params, Map<String, String> headers) {
        long start = System.currentTimeMillis();
        CloseableHttpClient httpClient = getClient();
        String result = null;
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;

        int statusCode = 0;
        try {
            httpPost.setConfig(getBaseConfig());
            String json = JSON.toJSONString(params);
            StringEntity s = new StringEntity(json, ContentType.APPLICATION_JSON);
            s.setContentEncoding("UTF-8");
            List<Header> headerList = Lists.newArrayList();
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                Header header = new BasicHeader(entry.getKey(), entry.getValue());
                headerList.add(header);
            }
            httpPost.setEntity(s);
            Header[] headerArray = new Header[headerList.size()];
            headerArray = headerList.toArray(headerArray);
            httpPost.setHeaders(headerArray);
            response = httpClient.execute(httpPost);

            statusCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                result = EntityUtils.toString(entity, "UTF-8");
            }
            if (statusCode != HttpStatus.SC_OK) {
                throw new RequestFailedException(url, statusCode, result);
            }
        } catch (IOException e) {
            throw new RequestFailedException(url, statusCode, result, e);
        } finally {
            if (logger.isInfoEnabled()) {
                logger.info(" doPost completed: url={}, params={}, statusCode={} ,result={}, cost {} ms ", url, JSON.toJSONString(params), statusCode, result,
                    (System.currentTimeMillis() - start));
            }
            closeResponse(response);
        }
        return result;
    }

    /**
     * 发送 POST 请求（HTTP），JSON形式
     *
     * @param url 请求路径
     * @param data json对象
     * @return responseEntity
     */
    public static String doPost(String url, Object data) {
        long start = System.currentTimeMillis();
        CloseableHttpClient httpClient = getClient();
        String httpStr = null;
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;

        int statusCode = 0;
        try {
            httpPost.setConfig(getBaseConfig());
            // 解决中文乱码问题
            StringEntity stringEntity = new StringEntity(data.toString(), "UTF-8");
            stringEntity.setContentEncoding("UTF-8");
            stringEntity.setContentType("application/json");
            httpPost.setEntity(stringEntity);
            response = httpClient.execute(httpPost);

            statusCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                httpStr = EntityUtils.toString(entity, "UTF-8");
            }
            if (statusCode != HttpStatus.SC_OK) {
                throw new RequestFailedException(url, statusCode, httpStr);
            }
        } catch (IOException e) {
            throw new RequestFailedException(url, statusCode, httpStr, e);
        } finally {
            if (logger.isInfoEnabled()) {
                logger.info(" doPost completed: url={}, data={}, statusCode={} ,result={}, cost {} ms ", url, JSON.toJSONString(data), statusCode, httpStr,
                    (System.currentTimeMillis() - start));
            }
            closeResponse(response);
        }
        return httpStr;
    }

    /**
     * 关闭响应流
     */
    private static void closeResponse(CloseableHttpResponse response) {
        if (response != null) {
            try {
                EntityUtils.consume(response.getEntity());
            } catch (IOException e) {
                logger.error(" closeResponse failed", e);
            }
        }
    }

}
