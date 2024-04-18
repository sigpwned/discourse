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
package com.sigpwned.discourse.core.util.type;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;
import org.junit.Test;
import com.google.common.reflect.TypeToken;

public class ArrayTypeTest {
  @Test
  public void primitiveArrayTest() {
    ArrayType type = ArrayType.parse(int[].class);
    assertThat(type, is(ArrayType.of(int.class)));
  }

  @Test
  public void referenceArrayTest() {
    ArrayType type = ArrayType.parse(String[].class);
    assertThat(type, is(ArrayType.of(String.class)));
  }

  public static final TypeToken<List<String>[]> ARRAY_OF_LIST_OF_STRING =
      new TypeToken<List<String>[]>() {};

  public static final TypeToken<List<String>> LIST_OF_STRING = new TypeToken<List<String>>() {};

  @Test
  public void genericArrayTest() {
    ArrayType type = ArrayType.parse(ARRAY_OF_LIST_OF_STRING.getType());
    assertThat(type, is(ArrayType.of(LIST_OF_STRING.getType())));
  }

  @Test(expected = IllegalArgumentException.class)
  public void nonArrayTest() {
    ArrayType.parse(String.class);
  }

  @Test(expected = IllegalArgumentException.class)
  public void voidTest() {
    new ArrayType(void.class);
  }

  @Test
  public void primitiveTest() {
    new ArrayType(int.class);
  }

  @Test
  public void referenceTest() {
    new ArrayType(String.class);
  }
  
  public static class UnresolvedArrayTest<T> {
    public TypeToken<T[]> token=new TypeToken<T[]>() {};
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void unresolvedTest() {
    UnresolvedArrayTest<String> test=new UnresolvedArrayTest<>();
    ArrayType.parse(test.token.getType());
  }
}
