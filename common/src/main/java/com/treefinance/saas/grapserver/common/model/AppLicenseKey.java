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

package com.treefinance.saas.grapserver.common.model;

import java.util.List;

/**
 * app 授权密钥
 */
public final class AppLicenseKey {

  private final String appId;
  private final String sdkPublicKey;
  private final String sdkPrivateKey;
  private final String serverPublicKey;
  private final String serverPrivateKey;
  private final List<Integer> bizTypeList;

  public AppLicenseKey(String appId, String sdkPublicKey, String sdkPrivateKey) {
    this(appId, sdkPublicKey, sdkPrivateKey, null);
  }

  public AppLicenseKey(String appId, String sdkPublicKey, String sdkPrivateKey, List<Integer> bizTypeList) {
    this(appId, sdkPublicKey, sdkPrivateKey, null, null, bizTypeList);
  }

  public AppLicenseKey(String appId, String sdkPublicKey, String sdkPrivateKey, String serverPublicKey, String serverPrivateKey,
                       List<Integer> bizTypeList) {
    this.appId = appId;
    this.sdkPublicKey = sdkPublicKey;
    this.sdkPrivateKey = sdkPrivateKey;
    this.serverPublicKey = serverPublicKey;
    this.serverPrivateKey = serverPrivateKey;
    this.bizTypeList = bizTypeList;
  }

  public String getAppId() {
    return appId;
  }

  public String getSdkPublicKey() {
    return sdkPublicKey;
  }

  public String getSdkPrivateKey() {
    return sdkPrivateKey;
  }

  public String getServerPublicKey() {
    return serverPublicKey;
  }

  public String getServerPrivateKey() {
    return serverPrivateKey;
  }

  public List<Integer> getBizTypeList() {
    return bizTypeList;
  }
}
