package com.sigpwned.discourse.core.invocation.phase.scan;

import java.util.List;
import java.util.Map;
import com.sigpwned.discourse.core.invocation.phase.scan.model.rules.NamedRule;

public interface RulesEngine {

  public Map<String, Object> run(Map<String, Object> input, List<NamedRule> rules);
}
