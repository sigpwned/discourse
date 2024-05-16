package com.sigpwned.discourse.core.invocation.phase.scan.rules;

import java.util.Map;
import java.util.Optional;
import com.sigpwned.discourse.core.invocation.phase.scan.model.rules.NamedRule;

public interface RuleEvaluator {

  public Optional<Object> run(Map<String, Object> input, NamedRule rule);
}
