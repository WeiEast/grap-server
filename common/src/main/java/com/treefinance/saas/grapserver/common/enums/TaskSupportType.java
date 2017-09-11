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

package com.treefinance.saas.grapserver.common.enums;

/**
 * 支持的任务类型
 *
 * @author Jerry
 * @since 10:52 02/05/2017
 */
public enum TaskSupportType {
  /**
   * 运营商
   */
  OPERATOR,
  /**
   * 电商
   */
  ECOMMERCE,
  /**
   * 邮箱
   */
  EMAIL;

  public static TaskSupportType from(String name) {
    try {
      return TaskSupportType.valueOf(name);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("The task type '" + name + "' is unsupported.", e);
    }
  }
}
