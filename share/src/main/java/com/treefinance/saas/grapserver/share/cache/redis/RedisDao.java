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

import java.util.Map;
import java.util.concurrent.TimeUnit;

public interface RedisDao {

    void enqueue(String key, String value);

    String dequeue(String key);

    String getValue(String key);

    String getValueQuietly(String key);

    boolean setExpiredValueQuietly(String key, String value);

    boolean setValueQuietly(String key, String value);

    boolean setValueQuietly(String key, String value, long ttlSeconds);

    boolean setValueQuietly(String key, String value, long timeout, TimeUnit unit);

    void setValue(String key, String value);

    void setValue(String key, String value, long timeout, TimeUnit unit);

    Boolean setIfAbsent(String key, String value);

    void putHash(String key, String hashKey, String hashValue);

    void putHash(String key, Map<String, String> map);

    Map<String, String> getHash(String key);

    String getHashValue(String key, String hashKey);

    boolean deleteQuietly(String key);

    void delete(String key);

    boolean hasKey(String key);

    Long getExpire(String key);

    Long getExpire(String key, TimeUnit unit);

    Boolean expire(String key, long timeout, TimeUnit unit);

    /**
     * String操作:增加,负数则为自减,并设置过期时间
     *
     * @param key
     * @param increment
     * @param timeout
     * @param unit
     * @return
     */
    Long incrBy(String key, long increment, long timeout, TimeUnit unit);

    /**
     * 分布式锁,获取锁
     *
     * @param lockKey 锁key
     * @param expired 锁的超时时间(毫秒),超时时间后自动释放锁,防止死锁
     * @return
     */
    Map<String, Object> acquireLock(String lockKey, long expired);

    /**
     * 分布式锁,释放锁
     *
     * @param lockKey 锁key
     * @param lockMap 比较值,判断所要释放的锁是否是当前锁
     * @param expired 锁设定的超时时间(毫秒)
     */
    void releaseLock(String lockKey, Map<String, Object> lockMap, long expired);


}
