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
import java.util.Set;
import org.junit.Test;

/**
 * Test {@link SetType}
 */
public class SetTypeTest {

  public static final TypeToken<Collection<String>> COLLECTION_OF_STRING = new TypeToken<Collection<String>>() {
  };

  @Test(expected = IllegalArgumentException.class)
  public void givenCollectionTypeToken_whenParse_thenFailWithIllegalArgumentException() {
    SetType.parse(COLLECTION_OF_STRING.getType());
  }

  @Test(expected = IllegalArgumentException.class)
  public void givenObjectType_whenParse_thenFailWithIllegalArgumentException() {
    SetType.parse(Object.class);
  }

  @Test(expected = IllegalArgumentException.class)
  public void givenRawType_whenParse_thenFailWithIllegalArgumentException() {
    SetType.parse(List.class);
  }

  public static final TypeToken<Set<String>> SET_OF_STRING = new TypeToken<Set<String>>() {
  };

  @Test
  public void givenSetOfStringTypeToken_whenParse_thenSucceedWithExpectedValue() {
    SetType observed = SetType.parse(SET_OF_STRING.getType());

    assertThat(observed, is(SetType.of(String.class)));
  }

  public static class SetTest<T> {

    public TypeToken<Set<T>> token = new TypeToken<Set<T>>() {
    };
  }

  @Test(expected = IllegalArgumentException.class)
  public void givenUnresolvedSetTypeToken_whenParse_thenFailWithIllegalArgumentException() {
    SetType.parse(new SetTest<String>().token.getType());
  }
}
