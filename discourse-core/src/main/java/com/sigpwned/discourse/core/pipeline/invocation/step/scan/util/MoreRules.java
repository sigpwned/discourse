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

import static java.util.Collections.emptyMap;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.NamedRule;
import com.sigpwned.discourse.core.util.MoreCollectors;
import com.sigpwned.discourse.core.util.MoreLists;
import com.sigpwned.discourse.core.util.MoreSets;

public class MoreRules {
  private static final Logger LOGGER = LoggerFactory.getLogger(MoreRules.class);

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
   * @param availablePropertyNames
   * @return
   */
  public static List<Reaction> react(List<NamedRule> rules, Set<String> availablePropertyNames) {
    List<RuleWrapper> ruleWrappers = rules.stream().map(RuleWrapper::fromRule).collect(toList());

    Map<Optional<String>, Map<Set<String>, List<RuleWrapper>>> grouped = group(ruleWrappers);

    Map<Set<String>, List<RuleWrapper>> sinksMap =
        grouped.getOrDefault(Optional.empty(), emptyMap());
    List<RuleWrapper> sinksList =
        sinksMap.values().stream().flatMap(List::stream).collect(toList());

    Map<String, Map<Set<String>, List<RuleWrapper>>> sourcesMap =
        grouped.entrySet().stream().filter(e -> e.getKey().isPresent())
            .map(e -> Map.entry(e.getKey().orElseThrow(), e.getValue()))
            .collect(MoreCollectors.mapFromEntries());
    Map<String, List<RuleWrapper>> sourcesBestMap = new HashMap<>();
    for (Map.Entry<String, Map<Set<String>, List<RuleWrapper>>> sourcesEntry : sourcesMap
        .entrySet()) {
      String consequent = sourcesEntry.getKey();
      Map<Set<String>, List<RuleWrapper>> consequentsMap = sourcesEntry.getValue();
      for (Map.Entry<Set<String>, List<RuleWrapper>> consequentEntry : consequentsMap.entrySet()) {
        Set<String> antecedents = consequentEntry.getKey();

        List<RuleWrapper> rulesList = consequentEntry.getValue();

        if (rulesList.size() > 1) {
          // TODO Should we log here, or return them all?
          if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(
                "Consequent {} has multiple rules with the same antecedents {}, choosing one arbitrarily...",
                consequent, antecedents);
          }
        }

        RuleWrapper bestRule = rulesList.get(0);

        sourcesBestMap.computeIfAbsent(consequent, c -> new ArrayList<>()).add(bestRule);
      }
    }

    List<String> consequentsList = new ArrayList<>(sourcesBestMap.keySet());
    List<List<RuleWrapper>> consequentsSourcesList =
        consequentsList.stream().map(sourcesBestMap::get).collect(toList());
    List<List<RuleWrapper>> consequentsSourcesPermutationList =
        MoreLists.cartesianProduct(consequentsSourcesList);

    List<Reaction> result = new ArrayList<>();
    for (List<RuleWrapper> consequentSourcesPermutation : consequentsSourcesPermutationList) {
      List<RuleWrapper> rs = new ArrayList<>();
      rs.addAll(consequentSourcesPermutation);
      rs.addAll(sinksList);
      for (String availablePropertyName : availablePropertyNames) {
        rs.add(RuleWrapper.forProperty(availablePropertyName));
      }

      rs = prune(rs);

      WrappedReaction reaction = segmentedReact(rs);

      result.add(reaction.unwrap());
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
  protected static WrappedReaction segmentedReact(List<RuleWrapper> rules) {
    List<RuleWrapper> sinks = new ArrayList<>();
    List<RuleWrapper> sources = new ArrayList<>();
    for (RuleWrapper rule : rules) {
      if (rule.consequent().isPresent()) {
        sources.add(rule);
      } else {
        sinks.add(rule);
      }
    }

    List<RuleWrapper> evaluated = new ArrayList<>();
    Set<String> available = new HashSet<>();
    Set<String> produced = new HashSet<>();
    Set<String> consumed = new HashSet<>();

    boolean updated;
    do {
      updated = false;
      Iterator<RuleWrapper> iterator = sources.iterator();
      while (iterator.hasNext()) {
        RuleWrapper rule = iterator.next();
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

    for (RuleWrapper sink : sinks) {
      if (available.containsAll(sink.antecedents())) {
        consumed.addAll(sink.antecedents());
        evaluated.add(sink);
      }
    }

    return new WrappedReaction(evaluated, available, produced, consumed);
  }

  /**
   * @param rules
   * @param propertyNames
   * @return
   */
  private static List<RuleWrapper> prune(List<RuleWrapper> rules) {
    List<RuleWrapper> sources = new ArrayList<>();
    List<RuleWrapper> sinks = new ArrayList<>();
    for (RuleWrapper rule : rules) {
      if (rule.consequent().isPresent()) {
        sources.add(rule);
      } else {
        sinks.add(rule);
      }
    }

    // Prune rules that are not internally consistent. If a rule has antecedents that are not
    // produced by any other rule, then it is not possible for that rule to be satisfied. Similarly,
    // if a rule produces a value that is not consumed by any other rule, then it is not useful.
    boolean updated;
    do {
      updated = false;

      Set<String> consumed = new HashSet<>();
      for (RuleWrapper sink : sinks) {
        consumed.addAll(sink.antecedents());
      }
      for (RuleWrapper source : sources) {
        consumed.addAll(source.antecedents());
      }

      Iterator<RuleWrapper> sourcesConsumedScanIterator = sources.iterator();
      while (sourcesConsumedScanIterator.hasNext()) {
        RuleWrapper source = sourcesConsumedScanIterator.next();
        if (!consumed.contains(source.consequent().orElseThrow())) {
          sourcesConsumedScanIterator.remove();
          updated = true;
        }
      }

      Set<String> produced = new HashSet<>();
      for (RuleWrapper source : sources) {
        produced.add(source.consequent().orElseThrow());
      }

      Iterator<RuleWrapper> sinksProducedScanIterator = sinks.iterator();
      while (sinksProducedScanIterator.hasNext()) {
        RuleWrapper rule = sinksProducedScanIterator.next();
        if (!produced.containsAll(rule.antecedents())) {
          sinksProducedScanIterator.remove();
          updated = true;
        }
      }

      Iterator<RuleWrapper> sourcesProducedScanIterator = sources.iterator();
      while (sourcesProducedScanIterator.hasNext()) {
        RuleWrapper rule = sourcesProducedScanIterator.next();
        if (!produced.containsAll(rule.antecedents())) {
          sourcesProducedScanIterator.remove();
          updated = true;
        }
      }
    } while (updated);

    return MoreLists.concat(sources, sinks);
  }

  private static Map<Optional<String>, Map<Set<String>, List<RuleWrapper>>> group(
      List<RuleWrapper> rules) {
    Map<Optional<String>, Map<Set<String>, List<RuleWrapper>>> result = new HashMap<>();
    for (RuleWrapper rule : rules) {
      Optional<String> consequent = rule.consequent();

      Map<Set<String>, List<RuleWrapper>> consequentMap =
          result.computeIfAbsent(consequent, c -> new HashMap<>());

      Set<String> antecedents = rule.antecedents();

      List<RuleWrapper> antecedentsList =
          consequentMap.computeIfAbsent(antecedents, a -> new ArrayList<>());

      antecedentsList.add(rule);
    }

    return result;
  }

  private static class RuleWrapper {
    public static RuleWrapper fromRule(NamedRule rule) {
      if (rule == null)
        throw new NullPointerException();
      return new RuleWrapper(rule.antecedents(), rule.consequent().orElse(null), rule);
    }

    public static RuleWrapper forProperty(String property) {
      if (property == null)
        throw new NullPointerException();
      return new RuleWrapper(Set.of(), property, null);
    }

    private final Set<String> antecedents;
    private final String consequent;
    private final NamedRule wrapped;

    public RuleWrapper(Set<String> antecedents, String consequent, NamedRule wrapped) {
      this.antecedents = unmodifiableSet(antecedents);
      this.consequent = consequent;
      this.wrapped = wrapped;
    }

    public Set<String> antecedents() {
      return antecedents;
    }

    public Optional<String> consequent() {
      return Optional.ofNullable(consequent);
    }

    public Optional<NamedRule> wrapped() {
      return Optional.ofNullable(wrapped);
    }

    @Override
    public int hashCode() {
      return Objects.hash(antecedents, consequent, wrapped);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      RuleWrapper other = (RuleWrapper) obj;
      return Objects.equals(antecedents, other.antecedents)
          && Objects.equals(consequent, other.consequent) && Objects.equals(wrapped, other.wrapped);
    }

    @Override
    public String toString() {
      return "RuleWrapper [antecedents=" + antecedents + ", consequent=" + consequent + ", wrapped="
          + wrapped + "]";
    }
  }

  private static record WrappedReaction(List<RuleWrapper> evaluated, Set<String> available,
      Set<String> produced, Set<String> consumed) {
    public WrappedReaction {
      evaluated = List.copyOf(evaluated);
      available = Set.copyOf(available);
      produced = Set.copyOf(produced);
      consumed = Set.copyOf(consumed);
    }

    public Reaction unwrap() {
      return new Reaction(evaluated.stream().flatMap(w -> w.wrapped().stream()).collect(toList()),
          available, produced, consumed);
    }
  }
}
