package com.sigpwned.discourse.core.configurable3.rule;

import java.util.Map;

public interface RuleExecutor {

  public boolean executeRule(NamedRule rule, Map<String, Object> state);
}
