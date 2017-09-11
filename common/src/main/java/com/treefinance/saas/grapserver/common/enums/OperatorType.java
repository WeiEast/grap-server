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
 * 运营商类型
 *
 * @author Jerry
 * @since 11:41 02/05/2017
 */
public enum OperatorType {
  /**
   * 中国移动
   */
  CMCC("中国移动"),
  /**
   * 中国联通
   */
  CUCC("中国联通"),
  /**
   * 中国电信
   */
  CTCC("中国电信");

  private String name;

  OperatorType(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public static OperatorType from(String name) {
    try {
      return OperatorType.valueOf(name);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("The operator type '" + name + "' is unsupported.", e);
    }
  }
}
