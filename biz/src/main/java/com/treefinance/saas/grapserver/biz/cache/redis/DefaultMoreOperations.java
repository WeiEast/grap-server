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

package com.treefinance.saas.grapserver.biz.cache.redis;

import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.TimeoutUtils;

/**
 * @author Jerry
 * @version 1.0.3
 * @since 1.0.0 [00:58, 10/29/15]
 */
public class DefaultMoreOperations<K, V> extends AbstractOperations<K, V> implements
    MoreOperations<K, V> {

  public DefaultMoreOperations(RedisTemplate<K, V> template) {
    super(template);
  }

  @Override
  public Long ttl(final K key) {
    final byte[] cacheKey = rawKey(key);
    return execute(connection -> connection.ttl(cacheKey));
  }

  @Override
  public Long incr(final K key) {
    final byte[] cacheKey = rawKey(key);
    return execute(connection -> connection.incr(cacheKey));
  }

  @Override
  public Long incr(final K key, final long expireSeconds) {
    final byte[] cacheKey = rawKey(key);
    return execute(connection -> {
      Long value = connection.incr(cacheKey);

      if (value <= 1) {
        connection.expire(cacheKey, expireSeconds);
      }

      return value;
    });
  }

  @Override
  public Long incr(final K key, final long expired, final TimeUnit timeUnit) {
    final byte[] cacheKey = rawKey(key);
    final long timeout = TimeoutUtils.toMillis(expired, timeUnit);
    return execute(connection -> {
      Long value = connection.incr(cacheKey);

      if (value <= 1) {
        connection.pExpire(cacheKey, timeout);
      }

      return value;
    });
  }

  @Override
  public Long incrBy(final K key, final long value) {
    final byte[] cacheKey = rawKey(key);
    return execute(connection -> connection.incrBy(cacheKey, value));
  }

  @Override
  public Long incrBy(final K key, final long value, final long expireSeconds) {
    final byte[] cacheKey = rawKey(key);
    return execute(connection -> {
      Long result = connection.incrBy(cacheKey, value);

      if (result <= value) {
        connection.expire(cacheKey, expireSeconds);
      }

      return result;
    });
  }

  @Override
  public Long incrBy(final K key, final long value, final long expired, final TimeUnit timeUnit) {
    final byte[] cacheKey = rawKey(key);
    final long timeout = TimeoutUtils.toMillis(expired, timeUnit);
    return execute(connection -> {
      Long result = connection.incrBy(cacheKey, value);

      if (result <= value) {
        connection.pExpire(cacheKey, timeout);
      }

      return result;
    });
  }

  @Override
  public Double incrBy(final K key, final double value) {
    final byte[] cacheKey = rawKey(key);
    return execute(connection -> connection.incrBy(cacheKey, value));
  }

  @Override
  public Double incrBy(final K key, final double value, final long expireSeconds) {
    final byte[] cacheKey = rawKey(key);
    return execute(connection -> {
      Double result = connection.incrBy(cacheKey, value);

      if (result <= value) {
        connection.expire(cacheKey, expireSeconds);
      }

      return result;
    });
  }

  @Override
  public Double incrBy(final K key, final double value, final long expired,
      final TimeUnit timeUnit) {
    final byte[] cacheKey = rawKey(key);
    final long timeout = TimeoutUtils.toMillis(expired, timeUnit);
    return execute(connection -> {
      Double result = connection.incrBy(cacheKey, value);

      if (result <= value) {
        connection.pExpire(cacheKey, timeout);
      }

      return result;
    });
  }

  @Override
  public Long decr(final K key) {
    final byte[] cacheKey = rawKey(key);
    return execute(connection -> connection.decr(cacheKey));
  }

  @Override
  public Long decrBy(final K key, final long value) {
    final byte[] cacheKey = rawKey(key);
    return execute(connection -> connection.decrBy(cacheKey, value));
  }

  @Override
  public Boolean setNX(final K key, final V value) {
    final byte[] cacheKey = rawKey(key);
    final byte[] cacheValue = rawValue(value);
    return execute(connection -> connection.setNX(cacheKey, cacheValue));
  }

  @Override
  public V getSet(final K key, final V value) {
    final byte[] cacheValue = rawValue(value);
    return execute(new ValueDeserializingRedisCallback(key) {

      @Override
      protected byte[] inRedis(byte[] rawKey, RedisConnection connection) {
        return connection.getSet(rawKey, cacheValue);
      }

    });
  }
}
