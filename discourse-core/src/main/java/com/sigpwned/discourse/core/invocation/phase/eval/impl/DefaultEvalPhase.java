package com.sigpwned.discourse.core.invocation.phase.eval.impl;

import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.configurable3.RulesEngine;
import com.sigpwned.discourse.core.configurable3.rule.NamedRule;
import com.sigpwned.discourse.core.invocation.phase.eval.EvalPhase;
import java.util.List;
import java.util.Map;

public class DefaultEvalPhase implements EvalPhase {

  private final RulesEngine engine;

  public DefaultEvalPhase(RulesEngine engine) {
    this.engine = requireNonNull(engine);
  }

  @Override
  public Map<String, Object> eval(Map<String, Object> initialState, List<NamedRule> rules) {
    return getEngine().run(initialState, rules);
  }

  protected RulesEngine getEngine() {
    return engine;
  }
}
