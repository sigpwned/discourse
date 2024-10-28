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
package com.sigpwned.discourse.core.pipeline.invocation.step.scan.util;

import com.sigpwned.discourse.core.util.MoreSets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class Ruling {

  public static record ProducedAndConsumed(Set<String> produced, Set<String> consumed) {
    public ProducedAndConsumed {
      produced = Set.copyOf(produced);
      consumed = Set.copyOf(consumed);
    }
  }

  private static ProducedAndConsumed intersection(ProducedAndConsumed a, ProducedAndConsumed b) {
    return new ProducedAndConsumed(MoreSets.intersection(a.produced(), b.produced()),
        MoreSets.intersection(a.consumed(), b.consumed()));
  }

  /**
   * Given a set of start symbols, return the maximal sets of symbols are guaranteed to be
   * producible and consumable under the given rules.
   *
   * @param start the set of start symbols
   * @param rules the rules
   * @return the minimal sets of symbols are guaranteed to be producible and consumable under the
   * given rules
   */
  public static ProducedAndConsumed reachable(Set<String> start,
      Map<List<String>, Optional<String>> rules) {
    return subreachable(Set.of(), start, rules);
  }

  private static ProducedAndConsumed subreachable(Set<String> consumed, Set<String> produced,
      Map<List<String>, Optional<String>> rules) {
    List<List<String>> satisfied = rules.keySet().stream().filter(produced::containsAll).toList();

    if (satisfied.isEmpty()) {
      return new ProducedAndConsumed(produced, consumed);
    }

    rules = new HashMap<>(rules);

    List<ProducedAndConsumed> pcs = new ArrayList<>();
    for (List<String> consume : satisfied) {
      Optional<String> produce = rules.remove(consume);

      Set<String> myconsumed = new HashSet<>(consumed);
      myconsumed.addAll(consume);

      Set<String> myproduced;
      if (produce.isPresent()) {
        myproduced = new HashSet<>(produced);
        myproduced.add(produce.orElseThrow());
      } else {
        myproduced = produced;
      }

      pcs.add(subreachable(myconsumed, myproduced, Map.copyOf(rules)));

      rules.put(consume, produce);
    }

    return pcs.stream().reduce(Ruling::intersection).orElseThrow();
  }
}
