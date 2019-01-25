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

package com.treefinance.saas.grapserver.web.saascontroller;

import com.treefinance.saas.grapserver.biz.service.TaskService;
import com.treefinance.saas.grapserver.biz.service.WebDetectService;
import com.treefinance.saas.grapserver.common.enums.EBizType;
import com.treefinance.saas.grapserver.web.RequestChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author guimeichao
 * @date 2018/12/12
 */

/**
 * 舆情监控信息处理
 */
@RestController
@RequestMapping("/internal/webdetect")
public class WebDetectController {

    @Autowired
    private WebDetectService webDetectService;
    @Autowired
    private TaskService taskService;

    /**
     * 发起任务
     * 
     * @param appid
     * @param uniqueId
     * @param platform 平台、网站
     * @param extra
     * @return
     */
    @RequestMapping(value = "/start", method = RequestMethod.POST)
    public Object createTask(@RequestParam String appid, @RequestParam("uniqueId") String uniqueId, @RequestParam("platform") String platform,
        @RequestParam("extra") String extra) {
        RequestChecker.notEmpty("appid", appid);
        RequestChecker.notEmpty("uniqueId", uniqueId);
        RequestChecker.notEmpty("platform", platform);
        RequestChecker.notEmpty("extra", extra);

        Long taskId = taskService.createTask(appid, uniqueId, EBizType.OPINION_DETECT);

        return webDetectService.startCrawler(taskId, platform, extra);
    }

    /**
     * 获取数据
     * 
     * @param appid
     * @param uniqueId
     * @param saasid
     * @param size
     * @param start
     * @param platform
     * @param entryname
     * @param keyword
     * @return
     */
    @RequestMapping(value = "/getData", method = RequestMethod.POST)
    public Object getData(@RequestParam String appid, @RequestParam("uniqueId") String uniqueId, @RequestParam("saasid") Long saasid,
        @RequestParam(value = "size", required = false) Integer size, @RequestParam(value = "start", required = false) Long start,
        @RequestParam(value = "platform") String platform, @RequestParam(value = "entryname") String entryname, @RequestParam(value = "keyword") String keyword) {
        RequestChecker.notEmpty("appid", appid);
        RequestChecker.notEmpty("uniqueId", uniqueId);
        RequestChecker.notEmpty("platform", platform);
        RequestChecker.notEmpty("entryname", entryname);
        RequestChecker.notEmpty("keyword", keyword);

        taskService.validateTask(appid, uniqueId, EBizType.OPINION_DETECT);

        return webDetectService.getData(appid, uniqueId, saasid, size, start, platform, entryname, keyword);
    }

}
