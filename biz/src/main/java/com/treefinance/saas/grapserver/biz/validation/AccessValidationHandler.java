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

package com.treefinance.saas.grapserver.biz.validation;

import com.treefinance.saas.grapserver.common.enums.EBizType;

/**
 * @author Jerry
 * @date 2019-01-24 11:18
 */
public interface AccessValidationHandler {

    /**
     * 验证任务访问有效性
     * 
     * @param appId 任务ID
     * @param uniqueId 唯一ID
     * @param bizType 业务类型
     */
    void validateTask(String appId, String uniqueId, EBizType bizType);
}
