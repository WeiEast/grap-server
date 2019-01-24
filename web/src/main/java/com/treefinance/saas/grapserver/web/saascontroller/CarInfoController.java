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

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.grapserver.biz.service.CarInfoService;
import com.treefinance.saas.grapserver.biz.service.TaskService;
import com.treefinance.saas.grapserver.common.enums.EBizType;
import com.treefinance.saas.grapserver.web.RequestChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Good Luck Bro , No Bug !
 * 车辆信息
 *
 * @author haojiahong
 * @date 2018/5/31
 */
@RestController
@RequestMapping(value = {"/car_info", "/grap/car_info"})
public class CarInfoController {

    private static final Logger logger = LoggerFactory.getLogger(CarInfoController.class);

    @Autowired
    private CarInfoService carInfoService;
    @Autowired
    private TaskService taskService;

    /**
     * 车辆信息采集
     *
     * @return
     */
    @RequestMapping(value = "/collect", method = {RequestMethod.POST})
    public Object collect(@RequestParam("appid") String appId,
                          @RequestParam("modelNum") String modelNum) {
        logger.info("车辆信息采集,输入参数:appid={},modelNum={}", appId, modelNum);
        RequestChecker.notBlank("modelNum", modelNum);

        Long taskId = taskService.createTask(appId, modelNum, EBizType.CAR_INFO);

        Object result = carInfoService.processCollectTask(taskId, appId, modelNum);
        logger.info("车辆信息采集,返回结果:result={},taskId={},appid={},modelNum={}",
                JSON.toJSONString(result), taskId, appId, modelNum);
        return result;
    }

}
