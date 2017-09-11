package com.treefinance.saas.grapserver.common.utils;

import com.alibaba.fastjson.JSON;
import com.datatrees.toolkits.util.http.HttpClientFactory;
import com.datatrees.toolkits.util.http.ResponseException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 数据下载器
 */
public abstract class RemoteDataDownloadUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteDataDownloadUtils.class);

    private static final CloseableHttpClient CLIENT = HttpClientFactory.create();

    public static <T> T download(String url, Class<T> clazz) throws IOException {
        RequestBuilder requestBuilder = RequestBuilder.get(url).setCharset(Consts.UTF_8);
        HttpUriRequest request = requestBuilder.build();

        return CLIENT.execute(request, (response) -> {
            int status = getStatus(response);
            if (status >= 200 && status < 300) {
                return readEntity(response, clazz);
            } else {
                String body = readEntity(response);
                throw new ResponseException(request.getURI().toString(), status, body);
            }
        });
    }

    private static int getStatus(HttpResponse response) {
        return response.getStatusLine().getStatusCode();
    }

    private static <T> T readEntity(HttpResponse response, Class<T> clazz) throws IOException {
        if (clazz == null) {
            return (T) response.getEntity();
        } else if (clazz == byte[].class) {
            return clazz.cast(readToByteArray(response.getEntity()));
        } else if (clazz == String.class) {
            return clazz.cast(readEntity(response));
        } else {
            HttpEntity entity = response.getEntity();
            return entity != null ? JSON.parseObject(entity.getContent(), clazz) : null;
        }
    }

    private static String readEntity(HttpResponse response) throws IOException {
        String body = readToString(response.getEntity());
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("[HttpClient] >> response body : {}", body);
        }
        return body;
    }

    private static String readToString(HttpEntity entity) throws IOException {
        return entity == null ? "" : EntityUtils.toString(entity, Consts.UTF_8);
    }

    private static byte[] readToByteArray(HttpEntity entity) throws IOException {
        return entity == null ? ArrayUtils.EMPTY_BYTE_ARRAY : EntityUtils.toByteArray(entity);
    }
}
