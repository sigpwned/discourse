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
package com.sigpwned.discourse.core.invocation.phase.scan.util;

import static java.util.stream.Collectors.toCollection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.NamedRule;
import com.sigpwned.discourse.core.util.MoreSets;

public class MoreRules {

  /**
   * Determine whether the given set of guaranteed rules is sufficient to satisfy the given set of
   * necessary rules. If this method returns {@code true}, then it is guaranteed that a solution
   * exists that satisfies all necessary rules. If this method returns {@code false}, then it is not
   * the case that no solution exists that satisfies all necessary rules; instead, it is the case
   * that the given guaranteed rules do not naturally imply all necessary rules.
   *
   * @param guaranteed The set of guaranteed rules.
   * @param necessary The set of necessary rules.
   * @param rules The list of all rules.
   * @return {@code true} if the given guaranteed rules are sufficient to satisfy the given
   *         necessary rules; {@code false} otherwise.
   */
  public static boolean isGuaranteedSatisfiable(Set<String> guaranteed, Set<String> necessary,
      List<NamedRule> rules) {
    if (necessary.isEmpty()) {
      return true;
    }
    if (guaranteed.containsAll(necessary)) {
      return true;
    }
    if (necessary.size() > guaranteed.size() + rules.size()) {
      // I don't even have to look. There are more necessary rules than I can possibly satisfy.
      return false;
    }
    if (necessary.size() > guaranteed.size()
        + rules.stream().filter(r -> r.consequent().isPresent()).count()) {
      // I don't even have to look. There are more necessary rules than I can possibly satisfy.
      return false;
    }
    return computeGuaranteedSatisfiabilityGap(guaranteed, necessary, rules).isEmpty();
  }

  /**
   * <p>
   * Compute the set of necessary rules that are not necessarily satisfied by the given guaranteed
   * rules. If this method returns an empty set, then all necessary rules are implied by the given
   * guaranteed rules. If this method returns a non-empty set, then it is not the case that no
   * solution exists that satisfies all necessary rules; instead, it is the case that the given
   * guaranteed rules do not naturally imply all necessary rules.
   * </p>
   *
   * <p>
   * Depending on the size and nature of the inputs, this can be an expensive operation, so it
   * should be used mindfully.
   * </p>
   *
   * @param guaranteed The set of guaranteed rules.
   * @param necessary The set of necessary rules.
   * @param rules The list of all rules.
   * @return The set of necessary rules that are not necessarily satisfied by the given guaranteed
   *         rules. If the result is empty, then all necessary rules are satisfied.
   */
  public static Set<String> computeGuaranteedSatisfiabilityGap(Set<String> guaranteed,
      Set<String> necessary, List<NamedRule> rules) {
    if (necessary.isEmpty()) {
      return Set.of();
    }
    if (guaranteed.containsAll(necessary)) {
      return Set.of();
    }

    final Set<String> satisfiable = new HashSet<>(guaranteed);
    final List<NamedRule> therules =
        rules.stream().filter(r -> guaranteed.contains(r.consequent().orElse(null)))
            .collect(toCollection(ArrayList::new));

    boolean updated;
    do {
      updated = false;

      Iterator<NamedRule> iterator = therules.iterator();
      while (iterator.hasNext()) {
        NamedRule rule = iterator.next();
        if (satisfiable.containsAll(rule.antecedents())) {
          rule.consequent().ifPresent(consequent -> {
            satisfiable.add(consequent);
            therules.removeIf(r -> r.consequent().equals(Optional.of(consequent)));
          });
          updated = true;
          break;
        }
      }

      if (satisfiable.containsAll(necessary)) {
        return Set.of();
      }

    } while (updated);

    return MoreSets.difference(necessary, satisfiable);
  }
}
