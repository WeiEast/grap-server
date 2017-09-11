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

package com.treefinance.saas.grapserver.web.request;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * <p/>
 *
 * @author Jerry
 * @version 1.0.1.4
 * @since 1.0.1.3 [14:43, 11/21/15]
 */
public class ByteServletInputStream extends ServletInputStream {

  private final ByteArrayInputStream inputStream;

  public ByteServletInputStream(byte[] content) {
    this(new ByteArrayInputStream(content));
  }

  public ByteServletInputStream(ByteArrayInputStream inputStream) {
    this.inputStream = inputStream;
  }

  @Override
  public int read() throws IOException {
    return inputStream.read();
  }

  public boolean isFinished() {
    return inputStream.available() == 0;
  }

  public boolean isReady() {
    return true;
  }

  public void setReadListener(ReadListener readListener) {
    throw new RuntimeException("This operation is unavailable");
  }
}
