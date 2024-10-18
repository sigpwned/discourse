/*-
 * =================================LICENSE_START==================================
 * discourse-core
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
package com.sigpwned.discourse.core.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import java.util.List;
import org.junit.Test;
import com.google.common.reflect.TypeToken;

public class TypesTest {
  @Test
  public void primitiveTest() {
    assertThat(Types.isPrimitive(byte.class), is(true));
    assertThat(Types.isPrimitive(short.class), is(true));
    assertThat(Types.isPrimitive(int.class), is(true));
    assertThat(Types.isPrimitive(long.class), is(true));
    assertThat(Types.isPrimitive(float.class), is(true));
    assertThat(Types.isPrimitive(double.class), is(true));
    assertThat(Types.isPrimitive(char.class), is(true));
    assertThat(Types.isPrimitive(boolean.class), is(true));

    assertThat(Types.isPrimitive(String.class), is(false));
  }
  
  @Test
  public void boxedTest() {
    assertThat(Types.boxed(byte.class), sameInstance(Byte.class));
    assertThat(Types.boxed(short.class), sameInstance(Short.class));
    assertThat(Types.boxed(int.class), sameInstance(Integer.class));
    assertThat(Types.boxed(long.class), sameInstance(Long.class));
    assertThat(Types.boxed(float.class), sameInstance(Float.class));
    assertThat(Types.boxed(double.class), sameInstance(Double.class));
    assertThat(Types.boxed(char.class), sameInstance(Character.class));
    assertThat(Types.boxed(boolean.class), sameInstance(Boolean.class));
  }
  
  public static final TypeToken<List<String>> LIST_OF_STRING = new TypeToken<List<String>>() {};
  
  public static class TokenTest<T> {
    public TypeToken<List<T>> token=new TypeToken<List<T>>() {};
  }

  @Test
  public void concreteTest() {
    assertThat(Types.isConcrete(String.class), is(true));
    assertThat(Types.isConcrete(LIST_OF_STRING.getType()), is(true));
    assertThat(Types.isConcrete(new TokenTest<String>().token.getType()), is(false));
  }
}
