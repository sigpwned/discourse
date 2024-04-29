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

import com.sigpwned.discourse.core.chain.Chain;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class Chains {

  private Chains() {
  }

  /**
   * Returns a stream of the elements in the given chain. The elements are provided in order from
   * the first element to the last element.
   *
   * @param chain the chain
   * @param <T>   the type of the elements
   * @return a stream of the elements in the given chain
   */
  public static <T> Stream<T> stream(Chain<T> chain) {
    return StreamSupport.stream(chain.spliterator(), false);
  }
}
