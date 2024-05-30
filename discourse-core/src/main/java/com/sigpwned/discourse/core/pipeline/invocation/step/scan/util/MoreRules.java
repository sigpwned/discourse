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

import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toCollection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.NamedRule;
import com.sigpwned.discourse.core.util.MoreLists;
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

  /**
   * <p>
   * Returns a list of {@link Reaction reactions} that represent the result of applying the given
   * rules to the given initial set of available properties.
   * </p>
   * 
   * <p>
   * Each returned reaction will contain all rules that were evaluated, all properties that are
   * available after the evaluation, all properties that were produced during the evaluation, and
   * all properties that were consumed during the evaluation.
   * </p>
   * 
   * <p>
   * Application developers can use this data to test if construction process is deterministic, if
   * all properties are decidable, which rules are needed, and so on.
   * </p>
   * 
   * @param rules
   * @param propertyNames
   * @return
   */
  public static List<Reaction> react(List<NamedRule> rules, Set<String> propertyNames) {
    Map<String, List<NamedRule>> sources = new HashMap<>();
    List<NamedRule> sinks = new ArrayList<>();
    for (NamedRule rule : rules) {
      if (rule.consequent().isPresent()) {
        sources.computeIfAbsent(rule.consequent().orElseThrow(), s -> new ArrayList<>()).add(rule);
      } else {
        sinks.add(rule);
      }
    }

    List<Set<String>> sinksMap = new ArrayList<>();
    for (NamedRule sink : sinks)
      sinksMap.add(sink.antecedents());

    List<Reaction> result = new ArrayList<>();
    List<List<NamedRule>> sourcePermutations =
        MoreLists.cartesianProduct(new ArrayList<>(sources.values()));
    for (List<NamedRule> sourcePermutation : sourcePermutations) {
      List<NamedRule> rulesList = new ArrayList<>();
      rulesList.addAll(sourcePermutation);
      rulesList.addAll(sinks);

      Reaction reaction = segmentedReact(propertyNames, rulesList);

      result.add(reaction);
    }

    return unmodifiableList(result);
  }

  /**
   * Represents the complete evaluation of a set of rules against a set of available properties.
   */
  public static record Reaction(List<NamedRule> evaluated, Set<String> available,
      Set<String> produced, Set<String> consumed) {
    public Reaction {
      evaluated = List.copyOf(evaluated);
      available = Set.copyOf(available);
      produced = Set.copyOf(produced);
      consumed = Set.copyOf(consumed);
    }
  }

  /**
   * Returns a {@link Reaction reaction} that represents the result of applying the given rules to
   * the given initial set of available properties. The returned reaction will contain all rules
   * that were evaluated, all properties that are available after the evaluation, all properties
   * that were produced during the evaluation, and all properties that were consumed during the
   * evaluation.
   * 
   * @param initiallyAvailable The initial set of available properties.
   * @param rules The rules to apply. For each rule with a consequent, there should only be one rule
   *        for each unique consequent value. There can be any number of rules with no consequent
   *        value. The order of the rules doesn't matter.
   * @return A {@link Reaction reaction} that represents the result of applying the given rules to
   *         the given initial set of available properties.
   */
  protected static Reaction segmentedReact(Set<String> initiallyAvailable, List<NamedRule> rules) {
    List<NamedRule> sinks = new ArrayList<>();
    List<NamedRule> sources = new ArrayList<>();
    for (NamedRule rule : rules) {
      if (rule.consequent().isPresent()) {
        sources.add(rule);
      } else {
        sinks.add(rule);
      }
    }

    List<NamedRule> evaluated = new ArrayList<>();
    Set<String> available = new HashSet<>(initiallyAvailable);
    Set<String> produced = new HashSet<>();
    Set<String> consumed = new HashSet<>();

    boolean updated;
    do {
      updated = false;
      Iterator<NamedRule> iterator = sources.iterator();
      while (iterator.hasNext()) {
        NamedRule rule = iterator.next();
        Set<String> antecedents = rule.antecedents();
        String consequent = rule.consequent().orElseThrow();
        if (available.containsAll(antecedents) && !produced.contains(consequent)) {
          available.add(consequent);
          produced.add(consequent);
          consumed.addAll(antecedents);
          iterator.remove();
          evaluated.add(rule);
          updated = true;
        }
      }
    } while (updated);

    for (NamedRule sink : sinks) {
      if (available.containsAll(sink.antecedents())) {
        consumed.addAll(sink.antecedents());
        evaluated.add(sink);
      }
    }

    return new Reaction(evaluated, available, produced, consumed);
  }
}
