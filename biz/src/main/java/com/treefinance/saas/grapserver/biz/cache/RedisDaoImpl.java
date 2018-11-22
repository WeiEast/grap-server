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

import com.treefinance.saas.grapserver.common.model.Constants;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component("redisDao")
public class RedisDaoImpl implements RedisDao {

    private static final Logger logger = LoggerFactory.getLogger(RedisDaoImpl.class);


    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public RedisTemplate<String, String> getRedisTemplate() {
        return redisTemplate;
    }


    @Override
    public String get(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Boolean setEx(String key, String value, long timeout, TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, unit);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public Long incrBy(String key, long increment, long timeout, TimeUnit unit) {
        try {
            Long count = redisTemplate.opsForValue().increment(key, increment);
            redisTemplate.expire(key, timeout, unit);
            return count;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean saveString2List(final String key, final String value) {
        return saveListString(key, Collections.singletonList(value));
    }

    @Override
    public boolean saveListString(final String key, final List<String> valueList) {
        Long result = redisTemplate.opsForList().rightPushAll(key, valueList.toArray(new String[valueList.size()]));
        redisTemplate.expire(key, Constants.REDIS_KEY_TIMEOUT, TimeUnit.SECONDS);
        return result != null;
    }

    @Override
    public String getStringFromList(final String key) {
        return redisTemplate.opsForList().rightPop(key);
    }

    @Override
    public String pullResult(final String obtainRedisKey) {
        try {
            return redisTemplate.opsForValue().get(obtainRedisKey);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean pushMessage(final String submitRedisKey, final String messageType) {
        return this.pushMessage(submitRedisKey, messageType, Constants.REDIS_KEY_TIMEOUT);
    }

    @Override
    public boolean pushMessage(String submitRedisKey, String messageType, int ttlSeconds) {
        try {
            redisTemplate.opsForValue().set(submitRedisKey, messageType, ttlSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public void deleteKey(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public Map<String, Object> acquireLock(String lockKey, long expired) {
        Map<String, Object> map = new HashMap<>();
        long value = System.currentTimeMillis() + expired + 1;
        boolean acquired = redisTemplate.opsForValue().setIfAbsent(lockKey, String.valueOf(value));
        if (acquired) {
            map.put("isSuccess", true);
            map.put("expireTimeStr", String.valueOf(value));
            return map;
        } else {
            String oldValueStr = redisTemplate.opsForValue().get(lockKey);
            //如果其他资源之前获得锁已经超时
            if (StringUtils.isNotBlank(oldValueStr) && Long.parseLong(oldValueStr) < System.currentTimeMillis()) {
                String getValue = redisTemplate.opsForValue().getAndSet(lockKey, String.valueOf(value));
                //上一个锁超时后会有很多线程去争夺锁，所以只有拿到oldValue的线程才是获得锁的。
                if (Long.parseLong(getValue) == Long.parseLong(oldValueStr)) {
                    map.put("isSuccess", true);
                    map.put("expireTimeStr", String.valueOf(value));
                    return map;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
    }

    @Override
    public void releaseLock(String lockKey, Map<String, Object> lockMap, long expireMsecs) {
        if (MapUtils.isEmpty(lockMap)) {
            return;
        }
        Boolean locked = (Boolean) lockMap.get("isSuccess");
        String lockExpiresStr = (String) lockMap.get("expireTimeStr");
        if (locked) {
            String oldValueStr = redisTemplate.opsForValue().get(lockKey);
            if (oldValueStr != null) {
                // 竞争的 redis.getSet 导致其时间跟原有的由误差，若误差在 超时范围内，说明仍旧是 原来的锁
                long diff = Long.parseLong(lockExpiresStr) - Long.parseLong(oldValueStr);
                if (diff < expireMsecs) {
                    redisTemplate.delete(lockKey);
                } else {
                    // 这个进程的锁超时了，被新的进程锁获得替换了。则不进行任何操作。打印日志，方便后续跟进
                    logger.error("the lockKey over time.lockKey:{}.expireMsecs:{},over time is",
                            lockKey, expireMsecs, System.currentTimeMillis() - Long.valueOf(lockExpiresStr));
                }
            }
        }

    }
}
