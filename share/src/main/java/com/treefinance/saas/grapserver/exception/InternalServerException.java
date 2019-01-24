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

package com.treefinance.saas.grapserver.exception;

import com.treefinance.saas.grapserver.context.ErrorCode;

/**
 * @author Jerry
 * @date 2019-01-24 20:38
 */
public class InternalServerException extends BadServiceException {
    public InternalServerException(String message) {
        super(ErrorCode.INTERNAL_SERVER_ERROR, message);
    }

    public InternalServerException(String message, Throwable cause) {
        super(ErrorCode.INTERNAL_SERVER_ERROR, message, cause);
    }

    public InternalServerException(Throwable cause) {
        super(ErrorCode.INTERNAL_SERVER_ERROR, cause);
    }
}
