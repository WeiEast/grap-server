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

package com.treefinance.saas.grapserver.web.controller;

import com.google.common.collect.ImmutableMap;
import com.treefinance.saas.knife.result.SimpleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Jerry
 * @since 17:15 26/04/2017
 */
@RestController
public class TestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestController.class);

    @RequestMapping("/test")
    public SimpleResult testCrypto(@RequestParam("appid") String appId, String info) {
        LOGGER.info("appId: {}", appId);
        LOGGER.info("information: {}", info);
        ImmutableMap<String, String> data = ImmutableMap
                .of("result", "I has received your information:" + info);
        return new SimpleResult<>(data);
    }

}
