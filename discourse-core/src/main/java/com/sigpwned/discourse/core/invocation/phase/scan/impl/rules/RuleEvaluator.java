package com.sigpwned.discourse.core.invocation.phase.scan.impl.rules;

import java.util.Map;
import java.util.Optional;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.model.rules.NamedRule;

public interface RuleEvaluator {

  public Optional<Object> run(Map<String, Object> input, NamedRule rule);
}
