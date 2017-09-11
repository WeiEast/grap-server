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

package com.treefinance.saas.grapserver.biz.cache;

import com.datatrees.toolkits.util.kryo.KryoUtils;
import java.lang.reflect.Method;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.util.DigestUtils;

/**
 * <p/>
 *
 * @author Jerry
 * @version 1.0.4.2
 * @since 1.0.0 [16:03, 08/22/15]
 */
public class CacheKeyGenerator implements KeyGenerator {

  @Override
  public Object generate(Object target, Method method, Object... params) {
    String className = target.getClass().getName();
    String methodName = method.getName();

    try {
      String signature = this.convertParams(params);

      return String.format("%s#%s(%s)", className, methodName, signature);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private String convertParams(Object... params) throws Exception {
    String signature = this.convertSimple(params);

    if (signature == null) {
      byte[] data = KryoUtils.serialize(params);
      signature = DigestUtils.md5DigestAsHex(data);
    }

    return signature;
  }

  private String convertSimple(Object... params) {
    if (params == null || params.length == 0) {
      return "";
    }

    if (params.length <= 3) {
      StringBuilder builder = new StringBuilder();
      for (Object obj : params) {
        if (obj instanceof String) {
          if (((String) obj).length() > 20) {
            return null;
          }

          builder.append((String) obj).append(",");
        } else if (obj instanceof Number) {
          builder.append(obj.toString()).append(",");
        } else {
          Class clazz = obj.getClass();
          if (clazz.isPrimitive() || clazz.isEnum()) {
            builder.append(obj).append(",");
          } else {
            return null;
          }
        }
      }

      builder.deleteCharAt(builder.length() - 1);

      return builder.toString();
    }

    return null;
  }
}
