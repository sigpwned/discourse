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

import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public final class MoreCollectors {

  private MoreCollectors() {
  }

  public static <K, V> Collector<Map.Entry<K, V>, ?, Map<K, V>> mapFromEntries() {
    return Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue);
  }

  private static IllegalStateException duplicateKeyException(Object k, Object u, Object v) {
    return new IllegalStateException(
        String.format("Duplicate key %s (attempted merging values %s and %s)", k, u, v));
  }
}
