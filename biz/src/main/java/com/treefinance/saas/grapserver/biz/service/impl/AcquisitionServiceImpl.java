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

package com.treefinance.saas.grapserver.biz.service.impl;

import com.treefinance.saas.grapserver.biz.service.AcquisitionService;
import com.treefinance.saas.grapserver.context.component.AbstractService;
import com.treefinance.saas.grapserver.manager.AcquisitionManager;
import com.treefinance.saas.grapserver.manager.param.AcquisitionEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Jerry
 * @date 2018/12/12 01:37
 */
@Service("acquisitionService")
public class AcquisitionServiceImpl extends AbstractService implements AcquisitionService {
    @Autowired
    private AcquisitionManager acquisitionManager;

    @Override
    public void acquisition(Long taskId, String header, String cookie, String url, String website, String accountNo, String topic) {
        acquisition(taskId, header, cookie, url, website, accountNo, topic, null);
    }

    @Override
    public void acquisition(Long taskId, String header, String cookie, String url, String website, String accountNo, String topic, String extra) {
        logger.info("acquisition >>> taskId={}, header={}, cookie={}, url={}, website={}, accountNo={}, topic={}, extra={}", taskId, header, cookie, url,
                website, accountNo, topic, extra);
        AcquisitionEntry entry = new AcquisitionEntry();
        entry.setTaskId(taskId);
        entry.setWebsite(website);
        entry.setHeader(header);
        entry.setCookie(cookie);
        entry.setUrl(url);
        entry.setAccountNo(accountNo);
        entry.setTopic(topic);
        entry.setExtra(extra);

        acquisitionManager.acquire(entry);
    }
}
