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

/**
 * @author Jerry
 * @date 2018/12/11 15:56
 */
@Getter
@Setter
@ToString
public class CallbackConfigBO implements Serializable {
    private Integer id;
    private String appId;
    private String receiver;
    private Byte version;
    private Byte isNewKey;
    private String url;
    private Byte retryTimes;
    private Byte timeOut;
    private String remark;
    private Byte isNotifyCancel;
    private Byte isNotifyFailure;
    private Byte isNotifySuccess;
    private Byte notifyModel;
    private Byte dataType;
}
