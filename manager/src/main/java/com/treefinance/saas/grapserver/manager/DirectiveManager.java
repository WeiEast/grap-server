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

import com.treefinance.saas.grapserver.manager.domain.DirectiveBO;

import javax.annotation.Nonnull;

/**
 * @author Jerry
 * @date 2018/12/11 00:36
 */
public interface DirectiveManager {

    DirectiveBO queryNextDirective(@Nonnull Long taskId);

    /**
     * 删除指令 数据库中的指令是只插入的,所以这里的删除指插入waiting指令
     */
    void deleteDirective(Long taskId);

    /**
     * 删除指令 数据库中的指令是只插入的,所以这里的删除指插入waiting指令
     */
    void deleteDirective(Long taskId, String directive);
}
