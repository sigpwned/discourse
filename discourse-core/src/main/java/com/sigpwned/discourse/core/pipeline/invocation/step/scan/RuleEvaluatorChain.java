package com.sigpwned.discourse.core.pipeline.invocation.step.scan;

import java.util.Map;
import java.util.Optional;
import com.sigpwned.discourse.core.Chain;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.NamedRule;

public class RuleEvaluatorChain extends Chain<RuleEvaluator> implements RuleEvaluator {
  @Override
  public Optional<Optional<Object>> run(Map<String, Object> input, NamedRule rule) {
    for (RuleEvaluator evaluator : this) {
      Optional<Optional<Object>> result = evaluator.run(input, rule);
      if (result.isPresent()) {
        return result;
      }
    }
    return Optional.empty();
  }
}
