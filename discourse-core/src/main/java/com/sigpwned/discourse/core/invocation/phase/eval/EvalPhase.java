package com.sigpwned.discourse.core.invocation.phase.eval;

import com.sigpwned.discourse.core.configurable3.rule.NamedRule;
import java.util.List;
import java.util.Map;

public interface EvalPhase {

  public Map<String, Object> eval(Map<String, Object> initialState, List<NamedRule> rules);
}
