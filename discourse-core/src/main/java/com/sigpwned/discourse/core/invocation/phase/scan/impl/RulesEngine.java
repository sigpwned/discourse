package com.sigpwned.discourse.core.invocation.phase.scan.impl;

import java.util.List;
import java.util.Map;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.model.rules.NamedRule;

public interface RulesEngine {

  public Map<String, Object> run(Map<String, Object> input, List<NamedRule> rules);
}
