package com.sigpwned.discourse.core.invocation.phase.scan.rules;

import java.util.Map;
import java.util.Optional;
import com.sigpwned.discourse.core.Chain;
import com.sigpwned.discourse.core.invocation.phase.scan.model.rules.NamedRule;

public class RuleEvaluatorChain extends Chain<RuleEvaluator> implements RuleEvaluator {

  @Override
  public Optional<Optional<Object>> run(Map<String, Object> input, NamedRule rule) {
    for (RuleEvaluator evaluator : this) {
      Optional<Optional<Object>> maybeResult = evaluator.run(input, rule);
      if (maybeResult.isPresent()) {
        return maybeResult;
      }
    }
    return Optional.empty();
  }
}
