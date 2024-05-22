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
package com.sigpwned.discourse.core;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.lang.reflect.Method;
import org.junit.Test;

/**
 * This does not test that the Java reflection API works. I'm pretty confident that it does! Rather,
 * this tests that it works the way I think it does.
 */
public class ReflectionTest {

  public static class Parent {

    public String foobar() {
      return "parent";
    }
  }

  public static class Child extends Parent {

    @Override
    public String foobar() {
      return "child";
    }
  }

  @Test
  public void givenMethodFromParentClass_whenInvokeOnChildInstanceWithOverride_thenChildOverrideIsInvoked()
      throws Exception {
    Method parentFoobar = Parent.class.getMethod("foobar");
    Child childInstance = new Child();
    String result = (String) parentFoobar.invoke(childInstance);
    assertThat(result, is("child"));
  }
}
