package com.treefinance.saas.grapserver.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author guoguoyun
 * @date Created in 2018/10/18下午4:40
 */
public final class TongdunDataResolver {

    private TongdunDataResolver() {}

    public static String encode(Integer val) {
        if (val == null) {
            return null;
        }

        int i = val & 0x0007;

        int j = (val >>> 3) & 0xffff;

        char f = (char)(i + 65);

        return Character.toString(f) + (j + 1);
    }

    public static Integer decode(String val) {
        if (StringUtils.isEmpty(val)) {
            return null;
        }

        int i = val.charAt(0) - 65;

        int j = Integer.parseInt(val.substring(1)) - 1;

        return j << 3 | (i & 0x0007);
    }

    public static Integer from(String val) {
        if (StringUtils.isEmpty(val)) {
            return 0;
        }

        String text = new BigDecimal(val).movePointRight(2).setScale(0, RoundingMode.FLOOR).toPlainString();
        int result = 0;
        for (int i = 0, length = text.length(); i < length; i++) {
            result |= ((text.charAt(i) - 48) & 0x00000007) << ((length - 1 - i) * 3);
        }

        return result - 71;
    }

    public static String to(Integer val) {
        int value = (val == null ? 0 : val) + 71;
        int result = 0;
        for (int i = 0; i < 11; i++) {
            result += ((value >> (i * 3)) & 0x00000007) * (int)Math.pow(10, i);
        }

        float output = result / 100.00f;

        return Float.toString(output);
    }

}
