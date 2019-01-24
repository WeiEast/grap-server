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

package com.treefinance.saas.grapserver.exception;

import com.treefinance.saas.grapserver.context.ErrorCode;

/**
 * 非法不正确的业务数据
 * 
 * @author Jerry
 * @date 2018/12/13 20:58
 */
public class InformException extends BadServiceException {

    public InformException(ErrorCode errorCode) {
        super(errorCode);
    }

    public InformException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public InformException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    public InformException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public InformException( String message) {
        super(ErrorCode.INTERNAL_SERVER_ERROR, message);
    }

    public InformException(String message, Throwable cause) {
        super(ErrorCode.INTERNAL_SERVER_ERROR, message, cause);
    }

    public InformException(Throwable cause) {
        super(ErrorCode.INTERNAL_SERVER_ERROR, cause);
    }
}
