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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public final class MoreCollectors {

  private MoreCollectors() {}

  public static <K, V> Collector<Map.Entry<K, V>, ?, Map<K, V>> mapFromEntries() {
    return Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue);
  }

  /**
   * Returns a {@link Collector} that accumulates elements
   * 
   * @param <T>
   * @return
   */
  public static <T> Collector<T, ?, Optional<Set<T>>> duplicates() {
    return Collector.of(() -> {
      return (Map<T, Boolean>) new HashMap<T, Boolean>();
    }, (xs, x) -> {
      xs.merge(x, false, (a, b) -> true);
    }, (xs, ys) -> {
      for (Map.Entry<T, Boolean> ye : ys.entrySet())
        xs.merge(ye.getKey(), ye.getValue(), (a, b) -> true);
      return xs;
    }, xs -> {
      Set<T> duplicates = new HashSet<>();
      for (Map.Entry<T, Boolean> xe : xs.entrySet())
        if (xe.getValue())
          duplicates.add(xe.getKey());
      return duplicates.isEmpty() ? Optional.empty() : Optional.of(duplicates);
    });
  }
}
