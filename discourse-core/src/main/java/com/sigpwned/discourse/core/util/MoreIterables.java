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

import com.sigpwned.discourse.core.util.collectors.Only;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public final class MoreIterables {

  private MoreIterables() {
  }

  /**
   * Returns the first element of the given iterable, or {@link Optional#empty() empty} if the
   * iterable is empty.
   *
   * @param iterable the iterable
   * @param <T>      the type of the elements
   * @return the first element of the given iterable, or {@link Optional#empty() empty} if the
   * iterable is empty
   */
  public static <T> Optional<T> first(Iterable<T> iterable) {
    Iterator<T> iterator = iterable.iterator();
    if (iterator.hasNext()) {
      return Optional.ofNullable(iterator.next());
    }
    return Optional.empty();
  }

  /**
   * Returns the last element of the given iterable, or {@link Optional#empty() empty} if the
   * iterable is empty.
   *
   * @param iterable the iterable
   * @param <T>      the type of the elements
   * @return the last element of the given iterable, or {@link Optional#empty() empty} if the
   * iterable is empty
   */
  public static <T> Optional<T> last(Iterable<T> iterable) {
    if (iterable instanceof List) {
      List<T> list = (List<T>) iterable;
      if (list.isEmpty()) {
        return Optional.empty();
      }
      return Optional.of(list.get(list.size() - 1));
    }
    T last = null;
    for (T element : iterable) {
      last = element;
    }
    return Optional.ofNullable(last);
  }

  /**
   * Returns the first and only element of the given iterable, or {@link Only#empty()} if the
   * iterable is empty, or {@link Only#overflowed()} if the iterable has more than one element.
   *
   * @param iterable the iterable
   * @param <T>      the type of the elements
   * @return the first and only element of the given iterable, or {@link Only#empty()} if the
   * iterable is empty, or {@link Only#overflowed()} if the iterable has more than one element.
   */
  public static <T> Only<T> only(Iterable<T> iterable) {
    return Only.fromIterable(iterable);
  }
}
