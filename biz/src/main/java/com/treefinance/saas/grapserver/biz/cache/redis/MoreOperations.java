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

/**
 * @author Jerry
 * @version 1.0.3
 * @since 1.0.0 [00:58, 10/29/15]
 */
interface MoreOperations<K, V> {

  /**
   * 返回key缓存的剩余时间
   *
   * @param key 缓存的key
   * @return {@link Long},以秒为单位，返回给定key的剩余生存时间
   */
  Long ttl(final K key);

  /**
   * 将 key 中储存的数字值增一。
   * <p>
   * 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 INCR 操作。 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。 本操作的值限制在 64
   * 位(bit)有符号数字表示之内。
   * </p>
   *
   * @param key 缓存的key
   * @return {@link Long},执行 INCR 命令之后 key 的值
   */
  Long incr(final K key);

  /**
   * 将 key 中储存的数字值增一,并对该值设置缓存时间。
   * <p>
   * 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 INCR 操作。 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。 本操作的值限制在 64
   * 位(bit)有符号数字表示之内。
   * </p>
   *
   * @param key 缓存的key
   * @param expireSeconds 缓存时间，单位：秒
   * @return {@link Long},执行 INCR 命令之后 key 的值
   */
  Long incr(final K key, final long expireSeconds);

  /**
   * 将 key 中储存的数字值增一,并对该值设置缓存时间。
   * <p>
   * 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 INCR 操作。 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。 本操作的值限制在 64
   * 位(bit)有符号数字表示之内。
   * </p>
   *
   * @param key 缓存的key
   * @param expired 缓存时间
   * @param timeUnit 缓存时间的单位
   * @return {@link Long},执行 INCR 命令之后 key 的值
   */
  Long incr(final K key, final long expired, final TimeUnit timeUnit);

  /**
   * 将 key 所储存的值加上增量 value。
   * <p>
   * 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 INCRBY 操作。 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。 本操作的值限制在
   * 64 位(bit)有符号数字表示之内。
   * </p>
   *
   * @param key 缓存的key
   * @param value 增量
   * @return {@link Long},加上 increment 之后 key 的值
   */
  Long incrBy(final K key, final long value);

  /**
   * 将 key 所储存的值加上增量 value,并对该值设置缓存时间。
   * <p>
   * 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 INCRBY 操作。 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。 本操作的值限制在
   * 64 位(bit)有符号数字表示之内。
   * </p>
   *
   * @param key 缓存的key
   * @param value 增量
   * @param expireSeconds 缓存时间，单位：秒
   * @return {@link Long},加上 increment 之后 key 的值
   */
  Long incrBy(final K key, final long value, final long expireSeconds);

  /**
   * 将 key 所储存的值加上增量 value,并对该值设置缓存时间。
   * <p>
   * 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 INCRBY 操作。 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。 本操作的值限制在
   * 64 位(bit)有符号数字表示之内。
   * </p>
   *
   * @param key 缓存的key
   * @param value 增量
   * @param expired 缓存时间
   * @param timeUnit 缓存时间的单位
   * @return {@link Long},加上 increment 之后 key 的值
   */
  Long incrBy(final K key, final long value, final long expired, final TimeUnit timeUnit);

  /**
   * 将 key 所储存的值加上增量 value。
   * <p>
   * 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 INCRBY 操作。 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。 本操作的值限制在
   * 64 位(bit)有符号数字表示之内。
   * </p>
   *
   * @param key 缓存的key
   * @param value 增量
   * @return {@link Double},加上 increment 之后 key 的值
   */
  Double incrBy(final K key, final double value);

  /**
   * 将 key 所储存的值加上增量 value,并对该值设置缓存时间。
   * <p>
   * 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 INCRBY 操作。 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。 本操作的值限制在
   * 64 位(bit)有符号数字表示之内。
   * </p>
   *
   * @param key 缓存的key
   * @param value 增量
   * @param expireSeconds 缓存时间，单位：秒
   * @return {@link Double},加上 increment 之后 key 的值
   */
  Double incrBy(final K key, final double value, final long expireSeconds);

  /**
   * 将 key 所储存的值加上增量 value,并对该值设置缓存时间。
   * <p>
   * 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 INCRBY 操作。 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。 本操作的值限制在
   * 64 位(bit)有符号数字表示之内。
   * </p>
   *
   * @param key 缓存的key
   * @param value 增量
   * @param expired 缓存时间
   * @param timeUnit 缓存时间的单位
   * @return {@link Double},加上 increment 之后 key 的值
   */
  Double incrBy(final K key, final double value, final long expired, final TimeUnit timeUnit);

  /**
   * 将 key 中储存的数字值增一。
   * <p>
   * 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 DECR 操作。 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。 本操作的值限制在 64
   * 位(bit)有符号数字表示之内。
   * </p>
   *
   * @param key 缓存的key
   * @return {@link Long},执行 DECR 命令之后 key 的值
   */
  Long decr(final K key);

  /**
   * 将 key 所储存的值减去减量 decrement。
   * <p>
   * 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 DECRBY 操作。 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。 本操作的值限制在
   * 64 位(bit)有符号数字表示之内。
   * </p>
   *
   * @param key 缓存的key
   * @param value 减量
   * @return {@link Long},执行 DECRBY 命令之后 key 的值
   */
  Long decrBy(final K key, final long value);

  /**
   * 将 key 的值设为 value ，当且仅当 key 不存在。
   * <p>
   * 若给定的 key 已经存在，则 SETNX 不做任何动作。 SETNX 是『SET if Not eXists』(如果不存在，则 SET)的简写。
   * </p>
   *
   * @param key 缓存的key
   * @param value 缓存的值
   * @return 设置成功，返回 true;设置失败，返回 false
   */
  Boolean setNX(final K key, final V value);

  /**
   * 将给定 key 的值设为 value ，并返回 key 的旧值(old value)。
   * <p>
   * 当 key 存在但不是字符串类型时，返回一个错误
   * </p>
   *
   * @param key 缓存的key
   * @param value 缓存的值
   * @return 返回 key 的旧值(old value)。当 key 存在但不是字符串类型时，返回一个错误
   */
  V getSet(final K key, final V value);
}
