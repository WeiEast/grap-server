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

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jerry
 * @date 02:14 2018/10/19
 */
public class TongdunDataResolverTest {

    @Test
    public void from() {
        for (int i = 0; i < 1000; i++) {
            String to = TongdunDataResolver.to(i);

            int actual = TongdunDataResolver.from(to);

            Assert.assertEquals(i, actual);
            System.out.println(i + " - " + to + " - " + actual);
        }
    }

}