package com.sigpwned.discourse.core.invocation.phase.scan.rules;

import com.sigpwned.discourse.core.Chain;
import com.sigpwned.discourse.core.invocation.phase.scan.model.rules.NamedRule;
import java.util.Map;
import java.util.Optional;

public class RuleEvaluatorChain extends Chain<RuleEvaluator> implements RuleEvaluator {

  @Override
  public Optional<Object> run(Map<String, Object> input, NamedRule rule) {
    return Optional.empty();
  }
}
