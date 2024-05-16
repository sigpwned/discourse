package com.sigpwned.discourse.core.invocation.phase.scan.impl.rules;

import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.RulesEngine;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.model.rules.NamedRule;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DefaultRulesEngine implements RulesEngine {

  private final RuleEvaluator evaluator;

  public DefaultRulesEngine(RuleEvaluator evaluator) {
    this.evaluator = requireNonNull(evaluator);
  }

  public Map<String, Object> run(Map<String, Object> input, List<NamedRule> rules) {
    rules = new ArrayList<>(rules);

    Map<String, Object> state = new HashMap<>(input);

    boolean updated;
    do {
      updated = false;

      List<NamedRule> ready = rules.stream()
          .filter(rule -> rule.antecedents().stream().allMatch(state::containsKey)).toList();

      // TODO Get a proper ordering here, thank you
      NamedRule next = ready.stream()
          .min(Comparator.<NamedRule>comparingInt(rule -> -rule.antecedents().size())).orElse(null);

      if (next != null) {
        Optional<Object> result = evaluator.run(state, next);

        if (result.isPresent() != next.consequent().isPresent()) {
          // TODO better exception
          throw new IllegalStateException(
              "Rule " + next + " produced " + result.isPresent() + " but expected "
                  + next.consequent().isPresent());
        }

        rules.remove(next);

        if (result.isPresent()) {
          state.put(next.consequent().get(), result.get());
        }

        updated = true;
      }
    } while (updated);

    return unmodifiableMap(state);
  }
}
