package com.treefinance.saas.grapserver.common.utils;

import com.treefinance.toolkit.util.http.client.MoreHttp;
import com.treefinance.toolkit.util.http.client.MoreHttpFactory;
import com.treefinance.toolkit.util.http.exception.HttpException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 数据下载器
 *
 * @author hanif
 */
public class RemoteDataDownloadUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteDataDownloadUtils.class);

    private static final MoreHttp CLIENT = MoreHttpFactory.createCustom();

    private RemoteDataDownloadUtils() {}

    public static <T> T download(String url, Class<T> clazz) throws HttpException {
        return CLIENT.get(url, StringUtils.EMPTY, clazz);
    }

}
