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

package com.treefinance.saas.grapserver.biz.config;

import com.github.diamond.client.extend.annotation.DAttribute;
import com.github.diamond.client.extend.annotation.DResource;
import org.springframework.stereotype.Component;

/**
 * @author Jerry
 * @since 16:21 02/05/2017
 */
@DResource
@Component
public class SystemConfig {

    @DAttribute(key = "system.running.test")
    private boolean testMode;

    /**
     * 全局APPID
     */
    @DAttribute(key = "system.h5.global.appid")
    private String h5GlobalAppId;

    public boolean isTestMode() {
        return testMode;
    }

    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }

    public String getH5GlobalAppId() {
        return h5GlobalAppId;
    }

    public void setH5GlobalAppId(String h5GlobalAppId) {
        this.h5GlobalAppId = h5GlobalAppId;
    }
}
