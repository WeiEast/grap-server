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

package com.treefinance.saas.grapserver.common.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.io.Serializable;
import java.util.List;

/**
 * @author Jerry
 * @date 2018/12/11 19:31
 */
@JsonInclude(Include.NON_NULL)
public class TongdunDetailData implements Serializable {

    /**
     * 关联借款人邮箱列表
     */
    private List<String> mails;
    /**
     * 关联借款人手机列表
     */
    private List<String> mobiles;
    /**
     * 关联身份证列表
     */
    private List<String> idcardNos;

    public List<String> getMails() {
        return mails;
    }

    public void setMails(List<String> mails) {
        this.mails = mails;
    }

    public List<String> getMobiles() {
        return mobiles;
    }

    public void setMobiles(List<String> mobiles) {
        this.mobiles = mobiles;
    }

    public List<String> getIdcardNos() {
        return idcardNos;
    }

    public void setIdcardNos(List<String> idcardNos) {
        this.idcardNos = idcardNos;
    }

    @Override
    public String toString() {
        return "TongdunDetailData{" + "mails=" + mails + ", mobiles=" + mobiles + ", idcardNos=" + idcardNos + '}';
    }
}
