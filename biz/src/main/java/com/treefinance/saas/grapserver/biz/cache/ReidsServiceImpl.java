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

package com.treefinance.saas.grapserver.biz.cache;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class ReidsServiceImpl implements RedisService {
    private static final Logger logger = LoggerFactory.getLogger(ReidsServiceImpl.class);

    @Resource
    private RedisDao redisDao;

    @Override
    public boolean saveString(String key, String value) {
        try {
            return redisDao.pushMessage(key, value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean saveString(String key, String value, int ttlSeconds) {
        try {
            return redisDao.pushMessage(key, value, ttlSeconds);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean saveListString(String key, List<String> valueList) {
        try {
            if (!CollectionUtils.isEmpty(valueList)) {
                return redisDao.saveListString(key, valueList);
            }
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public String getStringFromList(String key) {
        try {
            return redisDao.getStringFromList(key);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public String getString(String key) {
        try {
            return redisDao.pullResult(key);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean deleteKey(String key) {
        try {
            redisDao.deleteKey(key);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }
}
