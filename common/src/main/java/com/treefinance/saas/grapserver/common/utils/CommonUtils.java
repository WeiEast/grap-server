/**
 * Copyright © 2017 Treefinance All Rights Reserved
 */
package com.treefinance.saas.grapserver.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chenjh on 2017/7/5.
 * <p>
 * 公共的一些工具类
 */
public class CommonUtils {

    /**
     * 字符串正则匹配
     *
     * @param dest
     * @param regex
     * @return
     */
    public static boolean regexMatch(String dest, String regex) {
        if (StringUtils.isEmpty(dest) || StringUtils.isEmpty(regex)) {
            return false;
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(dest);
        return matcher.matches();
    }

    /**
     * 日期格式化
     *
     * @param date
     * @return
     */
    public static String date2Str(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }
}
