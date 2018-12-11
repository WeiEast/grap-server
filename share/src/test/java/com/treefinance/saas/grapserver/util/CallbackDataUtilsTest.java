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

package com.treefinance.saas.grapserver.util;

import com.treefinance.saas.grapserver.exception.CallbackCryptoException;
import org.junit.Test;

/**
 * @author Jerry
 * @date 2018/12/11 20:49
 */
public class CallbackDataUtilsTest {

    private static final String PRIVATE_KEY =
        "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAKNc0IpE+q61AL57xuY9PXlafqbs5IxqtAj89YTWLvGG9okVr+bcOwQUKhkFvXrTblFaWCfa820lM4n1GbpZ2eHB5K3cy6XcQMKXXjyMqmH2rUHzoLq+dZHoOaTV7FwtwEJPQ9IAjgiUlxKckkmkZglsmPFYy4I9ptsOVTyCb2WPAgMBAAECgYEAhYNDxN3fa43vD78lreJ7LYUyYcbWe5Rxk676EhjiwO8m6p7Y5nszmH/KcCzq0UmfrcmCpwAhVyDCYIv6/PyWPsWwtC3sWjitfBhLLMgiLnRUYpLDLHXdyrsUc7G4DJhVhz2HXlei+oVZNgqotUaanYLMw9TyJ/rOIOeDKuyvDzECQQDljPBApJauWPu7xO/l7qUVSOE6K0oB4VIQNiS454YjNnjA8RSU26M/vrY7hB1zn8DHSXNl/jEI6HJCvsYn6MALAkEAti+GyTiF3mKlCgB0hneR1XB3NhmXbTMn1kI7iUHBE4+01iWQi73uzE4yoD8WHz5rwkD/XBqshMpcCSNNgjcPDQJAe2S4nTccXJour6/ceVBAY2Gq2Jb+kGYrs9U3BkJGg9U7MhQlySML7S86TGHnZwkAGuBr7O6oMy6ohSKB6GAgIwJAZx65lRXIOBrvvZyKWD4/rmctItMTcfzdlJoCpuswBQl3WysCCQblroCoiSmMNP0Y82fk4lY6xiLgO0/fJFBT1QJBAJ9p++Ut8lNotZ617SF50o/AKWTiheauKDj6kwFtZSeNwBnPXATfrtAh8jNvopsY8e3K+5RPuB9m5kpmVHMsNEw=";

    @Test
    public void decrypt() throws CallbackCryptoException {
        String text =
            "gDS6KnIyF7aQSbYqzlvI6sk5SRAxjQotGyXKzk+EIK41STTL1fogOzzm5B0jR/PabbBmia+NH++Z/EbcX8Q3Iod699uwcE0r/6+xp4lDvtjFEFl3jxez5x+lyQZqY/V8SGyF6vA1HZXiq3as7vfCJW3f5utw5E5N/laUkOpCvi+OdG0ei/5zIpetL+URwWcE7gx1gtud6tVpx89F0toOb+a9XsW6ma/8ZFSI9BIYBAnUKzCqfHmc0klTq2BbkMyRX8RgHm2I+igEcJBDA/WQNpn4yFpAS6ZGFg3arfLk+Y1AsdO9kTRE/y2UVWso9l2GoWfYGIskBxQj/bKPyAVrpS57SOxFcVYLCRxu3Xo78UmIwUJrfbrWVNYSVU3KeO7JRCINIUWa6A5lt0AvfVJQ23KHIsoXW0cakJmDQMDAg9y65Sc72S9OVBWhtvt7YV7XS4SudWo80gC58ru+SFkTBB9G8ycqiKQ7Ae+doDjjWMXNcfgIqDalselROq3tIH9kgZ5DTbgYP+sYxpyrp9K5CSyXytKZHHskj+8S9vVqvoMMGt+tissDDQFLj7vDRB1wc1mTofZov0C37hdTYyG+IE3eV9jBooClbbAvY2vEk4xDssd9zn7xHyDTs6HpuQzp+9MHKF7+/x0W/xm4KZdO3sZehiJrNY6/xfZTVNFIDMuQb3gnKexwAeacqotYkeBjyFJDMS/KiqZAoLbwdCV87sHj5tuPBkVBlxtmslvMmTKPmc1pxW7uW+fHKLGtUXs9fpd8w5AeBlrk4LmpP/PM57EQT4VWMAG6TXj+vpCRBXv6VGgq2JbsIOIiz0vtQ9OMV1PyFiM6LPwExcQrmputyH4saLZa/D3jeHmQYSbwgOQoJrbmlbjzQdMxZ+qdutfL/z3u8z/qw54yOkkq114FVuMBA1Cu81GY6y+gKY6PBdv0xSsBN4HMOESrKkVnnuvWW14MvNbm3OsHCyB43D6p4m+/rVfmI0Rvw3RMqsKXWo9XYRm+F8v4ZFflmKOxrMZ7D1grsz/16+lzz6XPc/xAQcA9QnsEV0ekrqmbeKhISxTRL39Dpu43ggmgcR2SOm/x45f3J+S1U2sCvRpNrWbv8I4ePQEgMZ1PJ4R6vENpB5tkJOelYZ5Z+fapUFYefuF+vsRJx9pvjOnOVOBYXtngxVfMcCV8cTulpC2FFwsOYvE=";
        String result = CallbackDataUtils.decrypt(text, PRIVATE_KEY);
        System.out.println(result);
    }
}