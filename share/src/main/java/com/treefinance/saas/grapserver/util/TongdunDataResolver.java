/*
 * Copyright © 2015 - 2017 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.treefinance.saas.grapserver.util;

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
