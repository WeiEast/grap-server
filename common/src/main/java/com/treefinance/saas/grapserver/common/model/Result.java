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

import org.apache.commons.lang3.StringUtils;

/**
 * @author Jerry
 * @since 17:56 25/04/2017
 */
public class Result<T> {

    private long timestamp = System.currentTimeMillis();
    private String errorMsg;
    private T data;
    private boolean success = true;//兼容老的返回值

    public Result() {
    }

    public Result(T data) {
        this.data = data;
    }

    public Result(String errorMsg) {
        this.errorMsg = errorMsg;
        if (StringUtils.isNotBlank(errorMsg)) {
            this.success = false;
        }
    }

    public Result(long timestamp, String errorMsg, T data) {
        this.timestamp = timestamp;
        this.errorMsg = errorMsg;
        this.data = data;
        if (StringUtils.isNotBlank(errorMsg)) {
            this.success = false;
        }
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * 成功返回
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> Result<T> successResult(T data) {
        Result<T> result = new Result<>(data);
        result.setSuccess(true);
        return result;
    }

    /**
     * 失败返回
     *
     * @param errorMsg
     * @param <T>
     * @return
     */
    public static <T> Result<T> failResult(String errorMsg) {
        return Result.failResult(null, errorMsg);
    }

    /**
     * 失败返回
     *
     * @param data
     * @param errorMsg
     * @param <T>
     * @return
     */
    public static <T> Result<T> failResult(T data, String errorMsg) {
        Result<T> result = new Result<>(System.currentTimeMillis(), errorMsg, data);
        result.setSuccess(false);
        return result;
    }
}
