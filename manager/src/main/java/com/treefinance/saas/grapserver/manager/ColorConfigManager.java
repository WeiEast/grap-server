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

package com.treefinance.saas.grapserver.manager;

import com.treefinance.saas.grapserver.manager.domain.ColorConfigBO;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author guoguoyun
 * @date 2018/11/26上午11:31
 */
public interface ColorConfigManager {

    /**
     * 获取对应商户的颜色配置信息
     * 
     * @param appId 商户ID
     * @param style 颜色类型
     * @return 颜色配置信息 {@link ColorConfigBO}, or null if the dubbo service is available or the result is null.
     */
    @Nullable
    ColorConfigBO queryAppColorConfig(@Nonnull String appId, @Nullable String style);
}
