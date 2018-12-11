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

import com.treefinance.toolkit.util.json.Jackson;
import org.junit.Test;

import java.util.Map;

/**
 * @author henengqiang
 * @date 2018/11/13
 */
public class UtilsTests {

    @Test
    public void JsonUtilsTest() {
        String str = "{\"test1\":\"zhangsan\",\"test2\":\"lisi\",\"test3\":\"wanger\"}";
        Map<String, String> map = Jackson.parseMap(str, String.class, String.class);
        System.out.println(map);
    }

}
