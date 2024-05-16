package com.sigpwned.discourse.core.invocation.phase.scan.util;

import static java.util.stream.Collectors.toCollection;

import com.sigpwned.discourse.core.expr.Rule;
import com.sigpwned.discourse.core.util.MoreSets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class MoreRules {

  /**
   * Determine whether the given set of guaranteed rules is sufficient to satisfy the given set of
   * necessary rules. If this method returns {@code true}, then it is guaranteed that a solution
   * exists that satisfies all necessary rules. If this method returns {@code false}, then it is not
   * the case that no solution exists that satisfies all necessary rules; instead, it is the case
   * that the given guaranteed rules do not naturally imply all necessary rules.
   *
   * @param guaranteed The set of guaranteed rules.
   * @param necessary  The set of necessary rules.
   * @param rules      The list of all rules.
   * @return {@code true} if the given guaranteed rules are sufficient to satisfy the given
   * necessary rules; {@code false} otherwise.
   */
  public static boolean isGuaranteedSatisfiable(Set<String> guaranteed, Set<String> necessary,
      List<Rule> rules) {
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
    if (necessary.size() > guaranteed.size() + rules.stream()
        .filter(r -> r.getConsequent().isPresent()).count()) {
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
   * @param necessary  The set of necessary rules.
   * @param rules      The list of all rules.
   * @return The set of necessary rules that are not necessarily satisfied by the given guaranteed
   * rules. If the result is empty, then all necessary rules are satisfied.
   */
  public static Set<String> computeGuaranteedSatisfiabilityGap(Set<String> guaranteed,
      Set<String> necessary, List<Rule> rules) {
    if (necessary.isEmpty()) {
      return Set.of();
    }
    if (guaranteed.containsAll(necessary)) {
      return Set.of();
    }

    final Set<String> satisfiable = new HashSet<>(guaranteed);
    final List<Rule> therules = rules.stream()
        .filter(r -> guaranteed.contains(r.getConsequent().orElse(null)))
        .collect(toCollection(ArrayList::new));

    boolean updated;
    do {
      updated = false;

      Iterator<Rule> iterator = therules.iterator();
      while (iterator.hasNext()) {
        Rule rule = iterator.next();
        if (satisfiable.containsAll(rule.getAntecedents())) {
          rule.getConsequent().ifPresent(consequent -> {
            satisfiable.add(consequent);
            therules.removeIf(r -> r.getConsequent().equals(Optional.of(consequent)));
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
