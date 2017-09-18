package com.treefinance.saas.grapserver.common.utils;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
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
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;

/**
 * httpclient 调用工具类
 * Created by yh-treefinance on 2017/5/17.
 */
public class HttpClientUtils {
    // 日志
    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);
    // 连接池
    private static PoolingHttpClientConnectionManager connMgr;
    // 超时时间
    private static final int MAX_TIMEOUT = 3000;

    static {
        // 设置连接池
        connMgr = new PoolingHttpClientConnectionManager();
        // 设置连接池大小
        connMgr.setMaxTotal(100);
        connMgr.setDefaultMaxPerRoute(connMgr.getMaxTotal());
    }

    /**
     * 获取默认配置
     *
     * @return
     */
    private static RequestConfig getDefaultConfig() {
        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.BEST_MATCH)
                .setExpectContinueEnabled(true)
                .setStaleConnectionCheckEnabled(true)
                .setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
                .setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC))
                .build();
        return defaultRequestConfig;
    }

    /**
     * 获取默认配置
     *
     * @return
     */
    private static RequestConfig getBaseConfig() {
        RequestConfig defaultRequestConfig = getDefaultConfig();
        RequestConfig requestConfig = RequestConfig.copy(defaultRequestConfig)
                .setSocketTimeout(MAX_TIMEOUT)
                .setConnectTimeout(MAX_TIMEOUT)
                .setConnectionRequestTimeout(MAX_TIMEOUT)
                .build();
        return requestConfig;
    }

    /**
     * 获取默认配置
     *
     * @param timeOut 超时时间
     * @return
     */
    private static RequestConfig getConfigWithTimeOut(int timeOut) {
        RequestConfig defaultRequestConfig = getDefaultConfig();
        RequestConfig requestConfig = RequestConfig.copy(defaultRequestConfig)
                .setSocketTimeout(timeOut)
                .setConnectTimeout(timeOut)
                .setConnectionRequestTimeout(timeOut)
                .build();
        return requestConfig;
    }


    /**
     * 获取默认client
     *
     * @return
     */
    private static CloseableHttpClient getClient() {
        CloseableHttpClient httpclient = HttpClients.custom().setRetryHandler((e, n, c) -> {
            return false;
        }).build();
        return httpclient;
    }

    /**
     * 获取重试默认client
     *
     * @param retryTimes
     * @return
     */
    private static CloseableHttpClient getRetryClient(String url, Byte retryTimes) {
        CloseableHttpClient httpclient = HttpClients.custom().setRetryHandler((e, n, c) -> {
            logger.info("request {} failed : error={}, retry {} （max {} times）...", url, e.getMessage(), n, retryTimes);
            if (retryTimes != null && retryTimes.intValue() > n) {
                return true;
            }
            return false;
        }).build();
        return httpclient;
    }

    /**
     * 发送 GET 请求（HTTP），不带输入数据
     *
     * @param url
     * @return
     */
    public static String doGet(String url) {
        return doGet(url, new HashMap<String, Object>());
    }

    public static String doGetWithHeaders(String url, Map<String, String> headers) {
        return doGetWithHeaders(url, new HashMap<>(), headers);
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
                InputStream instream = entity.getContent();
                result = IOUtils.toString(instream, "UTF-8");
            }
            if (statusCode != HttpStatus.SC_OK) {
                throw new RequestFailedException(apiUrl, statusCode, result);
            }
        } catch (IOException e) {
            throw new RequestFailedException(apiUrl, statusCode, result, e);
        } finally {
            if (logger.isInfoEnabled()) {
                logger.info(" doGet completed: url={}, params={}, statusCode={} , result={} , cost {} ms ",
                        url, JSON.toJSONString(params), statusCode, result, (System.currentTimeMillis() - start));
            }
            closeResponse(response);
        }
        return result;
    }

    /**
     * 发送 GET 请求（HTTP），K-V形式
     *
     * @param url
     * @param params
     * @return
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
                InputStream instream = entity.getContent();
                result = IOUtils.toString(instream, "UTF-8");
            }
            if (statusCode != HttpStatus.SC_OK) {
                throw new RequestFailedException(apiUrl, statusCode, result);
            }
        } catch (IOException e) {
            throw new RequestFailedException(apiUrl, statusCode, result, e);
        } finally {
            if (logger.isInfoEnabled()) {
                logger.info(" doGet completed: url={}, params={}, statusCode={} , result={} , cost {} ms ",
                        url, JSON.toJSONString(params), statusCode, result, (System.currentTimeMillis() - start));
            }
            closeResponse(response);
        }
        return result;
    }


    /**
     * 发送 GET 请求（HTTP），K-V形式
     *
     * @param url
     * @param params
     * @return
     */
    public static String doGetWithTimoutAndRetryTimes(String url, Byte timeOut, Byte retryTimes, Map<String, Object> params) {
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
                InputStream instream = entity.getContent();
                result = IOUtils.toString(instream, "UTF-8");
            }
            if (statusCode != HttpStatus.SC_OK) {
                throw new RequestFailedException(apiUrl, statusCode, result);
            }
        } catch (IOException e) {
            logger.error("doGet exception:url={}, params={}, statusCode={} , result={}",
                    apiUrl, JSON.toJSONString(params), statusCode, result, e);
            throw new RequestFailedException(apiUrl, statusCode, result, e);
        } finally {
            if (logger.isInfoEnabled()) {
                logger.info(" doGet completed: url={}, params={}, statusCode={} , result={} , cost {} ms ",
                        apiUrl, JSON.toJSONString(params), statusCode, result, (System.currentTimeMillis() - start));
            }
            closeResponse(response);
        }
        return result;
    }

    /**
     * 发送 POST 请求（HTTP）
     *
     * @param url
     * @return
     */
    public static String doPost(String url) {
        return doPost(url, new HashMap<String, Object>());
    }


    /**
     * 发送 POST 请求（HTTP）
     *
     * @param url
     * @param timeOut    秒
     * @param retryTimes 重试次数
     * @param params     参数
     * @return
     */
    public static String doPostWithTimoutAndRetryTimes(String url, Byte timeOut, Byte retryTimes, Map<String, Object> params) {
        long start = System.currentTimeMillis();
        CloseableHttpClient httpClient = getRetryClient(url, retryTimes);
        String result = "";
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;

        int statusCode = 0;
        StringBuffer paramsBf = new StringBuffer();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            paramsBf.append(entry.getKey()).append("=").append(entry.getValue());
        }
        try {
            httpPost.setConfig(getConfigWithTimeOut(timeOut * 1000));
//            List<NameValuePair> pairList = new ArrayList<>(params.size());
//            for (Map.Entry<String, Object> entry : params.entrySet()) {
//                NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry
//                        .getValue().toString());
//                pairList.add(pair);
//            }
//            httpPost.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName("UTF-8")));


            StringEntity stringEntity = new StringEntity(paramsBf.toString(), "UTF-8");
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
            logger.error("doPost exception: url={}, statusCode={} ,result={}", url, statusCode, result, e);
            throw new RequestFailedException(url, statusCode, result, e);
        } finally {
            if (logger.isInfoEnabled()) {
                logger.info(" doPost completed: url={}, statusCode={} ,result={}, cost {} ms ",
                        url, statusCode, result, (System.currentTimeMillis() - start));
            }
            closeResponse(response);
        }
        return result;

    }

    /**
     * 发送 POST 请求（HTTP），K-V形式
     *
     * @param url
     * @param params 参数map
     * @return
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
                NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry
                        .getValue().toString());
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
                logger.info(" doPost completed: url={}, params={}, statusCode={} ,result={}, cost {} ms ",
                        url, JSON.toJSONString(params), statusCode, result, (System.currentTimeMillis() - start));
            }
            closeResponse(response);
        }
        return result;
    }

    /**
     * 发送 POST 请求（HTTP），K-V形式
     *
     * @param url
     * @param params 参数map
     * @return
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
            String json = JsonUtils.toJsonString(params);
            StringEntity s = new StringEntity(json);
            s.setContentEncoding("UTF-8");
            s.setContentType("application/json");
//            post.setEntity(s);
//            List<NameValuePair> pairList = new ArrayList<>(params.size());
//            for (Map.Entry<String, Object> entry : params.entrySet()) {
//                NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry
//                        .getValue().toString());
//                pairList.add(pair);
//            }
            List<Header> headerList = Lists.newArrayList();
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                Header header = new BasicHeader(entry.getKey(), entry.getValue());
                headerList.add(header);
            }
//            httpPost.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName("UTF-8")));
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
                logger.info(" doPost completed: url={}, params={}, statusCode={} ,result={}, cost {} ms ",
                        url, JSON.toJSONString(params), statusCode, result, (System.currentTimeMillis() - start));
            }
            closeResponse(response);
        }
        return result;
    }

    /**
     * 发送 POST 请求（HTTP），JSON形式
     *
     * @param url
     * @param json json对象
     * @return
     */
    public static String doPost(String url, Object json) {
        long start = System.currentTimeMillis();
        CloseableHttpClient httpClient = getClient();
        String httpStr = null;
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;

        int statusCode = 0;
        try {
            httpPost.setConfig(getBaseConfig());
            StringEntity stringEntity = new StringEntity(json.toString(), "UTF-8");//解决中文乱码问题
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
                logger.info(" doPost completed: url={}, json={}, statusCode={} ,result={}, cost {} ms ",
                        url, JSON.toJSONString(json), statusCode, httpStr, (System.currentTimeMillis() - start));
            }
            closeResponse(response);
        }
        return httpStr;
    }

    /**
     * 关闭响应流
     *
     * @param response
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

    public static void main(String[] args) {
        System.out.println(HttpClientUtils.doGet("http://n171t90503.iask.in:19859/p2pactivemq/gfd/callback/result"));
//        System.out.println(HttpClientUtils.doGet("http://www.baidu.com"));
//        Map<String, Object> map = Maps.newHashMap();
//        map.put("kw", "%E7%AC%AC%E4%B8%89%E6%96%B9%E7%9A%84");
//        map.put("fr", "wwwt");
//        System.out.println(HttpClientUtils.doGet("https://tieba.baidu.com/f?", map));
    }

}
