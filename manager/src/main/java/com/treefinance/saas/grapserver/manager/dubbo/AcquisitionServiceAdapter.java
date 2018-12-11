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

package com.treefinance.saas.grapserver.manager.dubbo;

import com.treefinance.saas.grapserver.context.component.RpcActionEnum;
import com.treefinance.saas.grapserver.manager.AcquisitionManager;
import com.treefinance.saas.grapserver.manager.param.AcquisitionEntry;
import com.treefinance.saas.taskcenter.facade.request.AcquisitionRequest;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;
import com.treefinance.saas.taskcenter.facade.service.AcquisitionFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;

/**
 * @author Jerry
 * @date 2018/12/12 01:17
 */
@Service
public class AcquisitionServiceAdapter extends AbstractTaskServiceAdapter implements AcquisitionManager {

    private final AcquisitionFacade acquisitionFacade;

    @Autowired
    public AcquisitionServiceAdapter(AcquisitionFacade acquisitionFacade) {
        this.acquisitionFacade = acquisitionFacade;
    }

    @Override
    public void acquire(@Nonnull AcquisitionEntry entry) {
        AcquisitionRequest request = new AcquisitionRequest();
        request.setTaskId(entry.getTaskId());
        request.setWebsite(entry.getWebsite());
        request.setHeader(entry.getHeader());
        request.setCookie(entry.getCookie());
        request.setUrl(entry.getUrl());
        request.setAccountNo(entry.getAccountNo());
        request.setTopic(entry.getTopic());

        TaskResult<Void> result = acquisitionFacade.acquisition(request);

        validateResponse(result, RpcActionEnum.ACQUISITION_ENTRY_BOOT, request);
    }
}
