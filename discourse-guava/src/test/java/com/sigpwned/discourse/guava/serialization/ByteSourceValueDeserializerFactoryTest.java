/*-
 * =================================LICENSE_START==================================
 * discourse-guava
 * ====================================SECTION=====================================
 * Copyright (C) 2022 Andy Boothe
 * ====================================SECTION=====================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ==================================LICENSE_END===================================
 */
package com.sigpwned.discourse.guava.serialization;

import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.junit.Test;
import com.google.common.io.ByteSource;
import com.google.common.io.Resources;

public class ByteSourceValueDeserializerFactoryTest {
  public static final byte[] BYTES = "Hello, world!".getBytes(StandardCharsets.UTF_8);

  /**
   * Make sure a URL works
   */
  @Test
  public void urlTest() throws IOException {
    URL url = Resources.getResource("bytes.txt");
    ByteSource bytes = ByteSourceValueDeserializerFactory.INSTANCE
        .getDeserializer(ByteSource.class, emptyList()).get().deserialize(url.toString());
    assertThat(bytes.read(), is(BYTES));
  }

  /**
   * Make sure a file works
   */
  @Test
  public void fileTest() throws IOException {
    File tmp = File.createTempFile("bytes.", ".txt");
    try {
      try (OutputStream out = new FileOutputStream(tmp)) {
        out.write(BYTES);
      }

      ByteSource bytes = ByteSourceValueDeserializerFactory.INSTANCE
          .getDeserializer(ByteSource.class, emptyList()).get().deserialize(tmp.getAbsolutePath());
      assertThat(bytes.read(), is(BYTES));
    } finally {
      tmp.delete();
    }
  }
}
