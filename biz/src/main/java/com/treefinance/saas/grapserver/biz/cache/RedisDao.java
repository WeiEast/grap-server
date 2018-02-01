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

import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Map;

public interface RedisDao {

    boolean saveListString(final String key, final List<String> valueList);

    boolean saveString2List(final String key, final String value);

    String getStringFromList(final String key);

    boolean pushMessage(String submitRedisKey, String messageType);

    boolean pushMessage(String submitRedisKey, String messageType, int ttlSeconds);

    String pullResult(String obtainRedisKey);

    RedisTemplate<String, String> getRedisTemplate();

    void deleteKey(String key);

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
