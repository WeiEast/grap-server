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

import org.springframework.data.redis.cache.RedisCachePrefix;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author Jerry
 * @datetime 2015-08-22 17:09
 */
public class CustomRedisCachePrefix implements RedisCachePrefix {
  private final RedisSerializer<String> serializer = new StringRedisSerializer();
  private String prefix;
  private String delimiter;

  public CustomRedisCachePrefix() {}

  public CustomRedisCachePrefix(String prefix, String delimiter) {
    this.prefix = prefix;
    this.delimiter = delimiter;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  public void setDelimiter(String delimiter) {
    this.delimiter = delimiter;
  }

  @Override
  public byte[] prefix(String cacheName) {
    if (this.delimiter == null)
      this.delimiter = ":";

    return serializer.serialize(this.prefix != null ? (this.prefix.concat(delimiter) + cacheName
        .concat(delimiter)) : cacheName.concat(delimiter));
  }
}
