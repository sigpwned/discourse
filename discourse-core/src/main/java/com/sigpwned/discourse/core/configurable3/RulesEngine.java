package com.sigpwned.discourse.core.configurable3;

import com.sigpwned.discourse.core.configurable3.rule.NamedRule;
import java.util.List;
import java.util.Map;

public interface RulesEngine {

  public Map<String, Object> run(Map<String, Object> input, List<NamedRule> rules);
}
