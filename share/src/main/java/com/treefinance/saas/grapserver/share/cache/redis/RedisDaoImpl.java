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

package com.treefinance.saas.grapserver.share.cache.redis;

import com.google.common.collect.ImmutableMap;
import com.treefinance.b2b.saas.context.conf.PropertiesConfiguration;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service("redisDao")
public class RedisDaoImpl implements RedisDao {

    private static final Logger logger = LoggerFactory.getLogger(RedisDaoImpl.class);
    private static final int REDIS_KEY_TIMEOUT = PropertiesConfiguration.getInstance().getInt("platform.redisKey.timeout", 600);

    private final StringRedisTemplate redisTemplate;

    @Autowired
    public RedisDaoImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void enqueue(String key, String value) {
        redisTemplate.opsForList().leftPush(key, value);
    }

    @Override
    public String dequeue(String key) {
        return redisTemplate.opsForList().rightPop(key);
    }

    @Override
    public String getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public String getValueQuietly(String key) {
        try {
            return getValue(key);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean setExpiredValueQuietly(String key, final String value) {
        return setValueQuietly(key, value, REDIS_KEY_TIMEOUT);
    }

    @Override
    public boolean setValueQuietly(String key, String value) {
        try {
            setValue(key, value);
            return true;
        } catch (Exception e) {
            logger.error("Error setting value into redis!", e);
        }
        return false;
    }

    @Override
    public boolean setValueQuietly(String key, String value, long ttlSeconds) {
        return setValueQuietly(key, value, ttlSeconds, TimeUnit.SECONDS);
    }

    @Override
    public boolean setValueQuietly(String key, String value, long timeout, TimeUnit unit) {
        try {
            setValue(key, value, timeout, unit);
            return true;
        } catch (Exception e) {
            logger.error("Error setting value into redis!", e);
        }
        return false;
    }

    @Override
    public void setValue(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void setValue(String key, String value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    @Override
    public Boolean setIfAbsent(String key, String value) {
        return redisTemplate.opsForValue().setIfAbsent(key, value);
    }

    @Override
    public void putHash(String key, String hashKey, String hashValue) {
        redisTemplate.opsForHash().put(key, hashKey, hashValue);
    }

    @Override
    public void putHash(String key, Map<String, String> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    @Override
    public Map<String, String> getHash(String key) {
        return redisTemplate.<String, String>opsForHash().entries(key);
    }

    @Override
    public String getHashValue(String key, String hashKey) {
        return redisTemplate.<String, String>opsForHash().get(key, hashKey);
    }

    @Override
    public boolean deleteQuietly(String key) {
        try {
            delete(key);
            return true;
        } catch (Exception e) {
            logger.error("Error deleting redis key!", e);
        }
        return true;
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    @Override
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key);
    }

    @Override
    public Long getExpire(String key, TimeUnit unit) {
        return redisTemplate.getExpire(key, unit);
    }

    @Override
    public Boolean expire(String key, final long timeout, final TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
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
    public Map<String, Object> acquireLock(String lockKey, long expired) {
        long value = System.currentTimeMillis() + expired + 1;
        boolean acquired = redisTemplate.opsForValue().setIfAbsent(lockKey, String.valueOf(value));
        if (acquired) {
            return ImmutableMap.of("isSuccess", true, "expireTimeStr", String.valueOf(value));
        } else {
            String oldValueStr = redisTemplate.opsForValue().get(lockKey);
            //如果其他资源之前获得锁已经超时
            if (StringUtils.isNotBlank(oldValueStr) && Long.parseLong(oldValueStr) < System.currentTimeMillis()) {
                String getValue = redisTemplate.opsForValue().getAndSet(lockKey, String.valueOf(value));
                //上一个锁超时后会有很多线程去争夺锁，所以只有拿到oldValue的线程才是获得锁的。
                if (Long.parseLong(getValue) == Long.parseLong(oldValueStr)) {
                    return ImmutableMap.of("isSuccess", true, "expireTimeStr", String.valueOf(value));
                }
            }
        }
        return null;
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
