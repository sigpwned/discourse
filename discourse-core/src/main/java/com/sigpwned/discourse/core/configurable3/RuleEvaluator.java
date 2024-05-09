package com.sigpwned.discourse.core.configurable3;

import com.sigpwned.discourse.core.configurable3.rule.NamedRule;
import java.util.Map;
import java.util.Optional;

public interface RuleEvaluator {

  public Optional<Object> run(Map<String, Object> input, NamedRule rule);
}
