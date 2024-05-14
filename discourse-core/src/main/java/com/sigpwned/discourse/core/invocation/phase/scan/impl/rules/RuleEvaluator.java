package com.sigpwned.discourse.core.invocation.phase.scan.impl.rules;

import com.sigpwned.discourse.core.invocation.phase.scan.impl.rules.model.NamedRule;
import java.util.Map;
import java.util.Optional;

public interface RuleEvaluator {

  public Optional<Object> run(Map<String, Object> input, NamedRule rule);
}
