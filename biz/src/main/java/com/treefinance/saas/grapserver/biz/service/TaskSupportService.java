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

package com.treefinance.saas.grapserver.biz.service;

import com.treefinance.saas.grapserver.common.exception.UnknownException;
import com.treefinance.saas.grapserver.common.utils.DataConverterUtils;
import com.treefinance.saas.grapserver.biz.dto.TaskSupport;
import com.treefinance.saas.taskcenter.facade.result.TaskSupportRO;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;
import com.treefinance.saas.taskcenter.facade.service.TaskSupportFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Jerry
 * @since 10:56 02/05/2017
 */
@Service
public class TaskSupportService {

    @Autowired
    private TaskSupportFacade taskSupportFacade;

    public List<TaskSupport> getSupportedList(String supportType, Integer id, String name) {
        TaskResult<List<TaskSupportRO>> rpcResult = taskSupportFacade.getSupportedList(supportType, id, name);
        if (!rpcResult.isSuccess()) {
            throw new UnknownException("调用taskcenter失败");
        }
        return DataConverterUtils.convert(rpcResult.getData(), TaskSupport.class);
    }

}
