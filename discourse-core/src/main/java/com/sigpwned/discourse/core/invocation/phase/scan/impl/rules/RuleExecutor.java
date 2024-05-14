package com.sigpwned.discourse.core.invocation.phase.scan.impl.rules;

import com.sigpwned.discourse.core.invocation.phase.scan.impl.rules.model.NamedRule;
import java.util.Map;

public interface RuleExecutor {

  public boolean executeRule(NamedRule rule, Map<String, Object> state);
}
