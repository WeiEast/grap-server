/*
 * Copyright © 2015 - 2017 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.treefinance.saas.grapserver.manager.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Jerry
 * @date 2018/12/11 01:27
 */
@Getter
@Setter
@ToString
public class BizLicenseInfoBO implements Serializable {
    private String appId;
    private Byte bizType;
    private String bizName;
    private Integer dailyLimit;
    private BigDecimal trafficLimit;
    private Byte isShowLicense;
    private Byte isValid;
    private Byte h5Access;
    private Byte sdkAccess;
    private Byte apiAccess;
    private String licenseTemplate;
    private Integer questionaireRate;
    private Integer feedbackRate;
}
