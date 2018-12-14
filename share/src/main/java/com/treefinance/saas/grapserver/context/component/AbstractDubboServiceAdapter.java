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

package com.treefinance.saas.grapserver.context.component;

import com.alibaba.fastjson.JSON;
import com.treefinance.b2b.saas.context.adapter.AbstractDomainObjectAdapter;
import com.treefinance.saas.grapserver.exception.RpcServiceException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Jerry
 * @date 2018/11/23 19:15
 */
public abstract class AbstractDubboServiceAdapter extends AbstractDomainObjectAdapter {

    protected <Response> void validateResponse(Response response, RpcActionEnum action, Object... args) {
        if (response == null) {
            throw new RpcServiceException("Bad response! - action: " + action + appendArgs(args));
        }
    }

    protected String appendArgs(Object... args) {
        return ArrayUtils.isNotEmpty(args) ? ", args: " + JSON.toJSONString(args) : StringUtils.EMPTY;
    }

}
