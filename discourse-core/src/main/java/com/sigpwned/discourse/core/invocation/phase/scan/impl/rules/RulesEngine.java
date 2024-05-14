package com.sigpwned.discourse.core.invocation.phase.scan.impl.rules;

import com.sigpwned.discourse.core.invocation.phase.scan.impl.rules.model.NamedRule;
import java.util.List;
import java.util.Map;

public interface RulesEngine {

  public Map<String, Object> run(Map<String, Object> input, List<NamedRule> rules);
}
