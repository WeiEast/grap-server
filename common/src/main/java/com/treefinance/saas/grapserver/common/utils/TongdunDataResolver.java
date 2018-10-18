package com.treefinance.saas.grapserver.common.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * @author:guoguoyun
 * @date:Created in 2018/10/18下午4:40
 */
public class TongdunDataResolver {

    public  static void main(String[] args) {
        for (int i = 0; i < 1000; i++) {
            String val = encode(i);
            System.out.println(val + " - " + decode(val));
        }
    }

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

}
