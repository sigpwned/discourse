/*-
 * =================================LICENSE_START==================================
 * discourse-core
 * ====================================SECTION=====================================
 * Copyright (C) 2022 - 2024 Andy Boothe
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
package com.sigpwned.discourse.core.util;

import static org.junit.Assert.assertTrue;
import java.lang.reflect.Field;
import org.junit.Test;

public class ReflectionTest {
  /**
   * Example class used for reflection testing
   */
  public static class ReflectionExample {
    public int foo;
  }

  @Test
  public void givenTwoFieldObjectsRepresentingSameField_whenCompare_thenEqualsEqual()
      throws Exception {
    Field foo1 = ReflectionExample.class.getDeclaredField("foo");
    Field foo2 = ReflectionExample.class.getDeclaredField("foo");
    assertTrue(foo1.equals(foo2));
  }
}
