package com.sigpwned.discourse.core.invocation.phase.eval;

import com.sigpwned.discourse.core.configurable3.rule.NamedRule;
import java.util.List;
import java.util.Map;

public interface EvalPipelineListener {

  public void beforeEval(Map<String, Object> initialState, List<NamedRule> rules);

  public void afterEval(Map<String, Object> initialState, List<NamedRule> rules,
      Map<String, Object> newState);

  public void beforeFactory(Map<String, Object> newState);

  public void afterFactory(Map<String, Object> newState, Object instance);
}
