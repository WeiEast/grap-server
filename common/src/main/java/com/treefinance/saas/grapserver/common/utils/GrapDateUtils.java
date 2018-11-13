package com.treefinance.saas.grapserver.common.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author haojiahong on 2017/9/22.
 */
public class GrapDateUtils {

    /**
     * 获取当前时间的字符串(格式为yyyy-MM-dd HH:mm:ss)
     */
    public static String nowDateTimeStr() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(dateTimeFormatter);
    }

    /**
     * 获取当前时间
     */
    public static Date nowDateTime() {
        return new Date();
    }

    /**
     * 将时间字符串转换为Date
     */
    public static Date getDateByStr(String dateStr) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(dateStr, dateTimeFormatter);
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 时间转字符串(格式为yyyy-MM-dd HH:mm:ss)
     */
    public static String getDateStrByDate(Date date) {
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return localDateTime.format(dateTimeFormatter);

    }

    public static String getDateStrByDate(Date date, String format) {
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
        return localDateTime.format(dateTimeFormatter);

    }

}
