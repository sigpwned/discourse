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

import static java.util.Collections.*;

import java.util.HashSet;
import java.util.Set;

public final class MoreSets {

  private MoreSets() {
  }

  /**
   * Returns the set of elements that are in {@code xs} but not in {@code ys}.
   *
   * @param xs  the set of elements
   * @param ys  the set of elements to exclude
   * @param <T> the type of the elements in the sets
   * @return the set of elements that are in {@code xs} but not in {@code ys}
   */
  public static <T> Set<T> difference(Set<T> xs, Set<T> ys) {
    Set<T> result = new HashSet<>(xs);
    result.removeAll(ys);
    return unmodifiableSet(result);
  }

  /**
   * Returns the set of elements that are in both {@code xs} and {@code ys}.
   *
   * @param xs  the first set of elements
   * @param ys  the second set of elements
   * @param <T> the type of the elements in the sets
   * @return the set of elements that are in both {@code xs} and {@code ys}
   */
  public static <T> Set<T> intersection(Set<T> xs, Set<T> ys) {
    Set<T> result;
    if (xs.size() < ys.size()) {
      // Let's copy the smaller set to avoid the memory cost of the larger set.
      result = new HashSet<>(xs);
      result.retainAll(ys);
    } else {
      result = new HashSet<>(ys);
      result.retainAll(xs);
    }
    return unmodifiableSet(result);
  }
}
