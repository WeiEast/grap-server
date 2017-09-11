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

import com.alibaba.fastjson.JSON;
import com.datatrees.rawdatacentral.api.CrawlerService;
import com.datatrees.rawdatacentral.domain.model.WebsiteConf;
import com.datatrees.toolkits.util.json.Jackson;
import com.treefinance.saas.grapserver.dao.entity.TaskSupport;
import com.treefinance.saas.grapserver.common.enums.OperatorType;
import com.treefinance.saas.grapserver.common.enums.TaskSupportType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Jerry
 * @since 13:59 02/05/2017
 */
@Service
public class TaskConfigService {
  private static final Logger logger = LoggerFactory.getLogger(TaskDeviceService.class);

  @Autowired
  private TaskSupportService taskSupportService;
  @Autowired
  private CrawlerService crawlerService;

  /**
   * 获取配置信息
   *
   * @param type 分类 {@link TaskSupportType}
   */
//  @Cacheable(value = "DAY", key = "'saas_crawler_task_config:'+#type")
  public Object getTaskConfig(String type) {
    TaskSupportType supportType = TaskSupportType.from(type);

    List<TaskSupport> supportedList = taskSupportService
        .getSupportedList(supportType.name());
    if (CollectionUtils.isEmpty(supportedList)) {
      throw new IllegalArgumentException("Can not find any supported list.");
    }

    List<String> list = supportedList.stream().map(TaskSupport::getName)
        .collect(Collectors.toList());

    List<WebsiteConf> websiteConf = crawlerService.getWebsiteConf(list);

    return merge(supportedList, websiteConf, supportType);
  }


  private List<Map<String, Object>> merge(List<TaskSupport> supportedList,
      List<WebsiteConf> configs, TaskSupportType type) {
    if (type == TaskSupportType.OPERATOR) {
      List<Map<String, Object>> configList = new ArrayList<>();
      Map<String, List<TaskSupport>> collect = supportedList.stream()
          .collect(Collectors.groupingBy(TaskSupport::getType));

      collect.forEach((key, value) -> {
        OperatorType operatorType = OperatorType.from(key);

        Map<String, Object> map = new HashMap<>();
        map.put("name", operatorType.getName());
        map.put("list", merge(value, configs));
        configList.add(map);
      });

      return configList;
    } else {

      return merge(supportedList, configs);
    }
  }

  private List<Map<String, Object>> merge(List<TaskSupport> supportedList,
      List<WebsiteConf> configs) {
    List<Map<String, Object>> list = new ArrayList<>();

    for (TaskSupport taskSupport : supportedList) {
      Map<String, Object> map = new HashMap<>();
      map.put("id", taskSupport.getId());
      map.put("name", taskSupport.getName());
      map.put("image", taskSupport.getImage());
      map.put("loginConfig", getLoginConfig(taskSupport.getName(), configs));

      list.add(map);
    }

    return list;
  }

  private Object getLoginConfig(String name, List<WebsiteConf> configs) {
    Map<String, Object> loginConfig = null;
    Iterator<WebsiteConf> iterator = configs.iterator();
    logger.info("configs name={},configs={}",name,JSON.toJSONString(configs));
    while (iterator.hasNext()) {
      WebsiteConf conf = iterator.next();
      if(null == conf){
        continue;
      }

      if (conf.getName().equals(name)) {
        loginConfig = getLoginConfig(conf);

        iterator.remove();
      }
    }

    return loginConfig == null ? Collections.emptyMap() : loginConfig;
  }

  private Map<String, Object> getLoginConfig(WebsiteConf conf) {
    Map<String, Object> loginConfig = new HashMap<>();
    loginConfig.put("website", conf.getWebsiteName());
    loginConfig.put("isSimulate", conf.getSimulate());
    loginConfig.putAll(Jackson.parseMap(conf.getInitSetting(), String.class, Object.class));

    if (conf.getSimulate()) {
      loginConfig.put("loginTip", conf.getLoginTip());
      loginConfig.put("verifyTip", conf.getVerifyTip());

      loginConfig.put("resetType", conf.getResetType());
      loginConfig.put("smsTemplate", conf.getSmsTemplate());
      loginConfig.put("smsReceiver", conf.getSmsReceiver());
      loginConfig.put("resetURL", conf.getResetURL());
      loginConfig.put("resetTip", conf.getResetTip());
    }

    return loginConfig;
  }
}
