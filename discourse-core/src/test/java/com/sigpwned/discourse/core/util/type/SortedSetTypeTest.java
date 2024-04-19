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

import com.google.common.reflect.TypeToken;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import org.junit.Test;

/**
 * Test {@link SortedSetType}
 */
public class SortedSetTypeTest {

  public static final TypeToken<Collection<String>> COLLECTION_OF_STRING = new TypeToken<Collection<String>>() {
  };

  @Test(expected = IllegalArgumentException.class)
  public void parameterizedTest() {
    SortedSetType.parse(COLLECTION_OF_STRING.getType());
  }

  @Test(expected = IllegalArgumentException.class)
  public void objectTest() {
    SortedSetType.parse(Object.class);
  }

  @Test(expected = IllegalArgumentException.class)
  public void rawTest() {
    SortedSetType.parse(List.class);
  }

  public static final TypeToken<SortedSet<String>> SORTED_SET_OF_STRING = new TypeToken<SortedSet<String>>() {
  };

  @Test
  public void concreteTest() {
    SortedSetType observed = SortedSetType.parse(SORTED_SET_OF_STRING.getType());

    assertThat(observed, is(SortedSetType.of(String.class)));
  }

  public static class SortedSetTest<T> {

    public TypeToken<SortedSet<T>> token = new TypeToken<SortedSet<T>>() {
    };
  }

  @Test(expected = IllegalArgumentException.class)
  public void unresolvedTest() {
    SortedSetType.parse(new SortedSetTest<String>().token.getType());
  }
}
