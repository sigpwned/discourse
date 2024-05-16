package com.sigpwned.discourse.core.invocation.phase.factory.impl;

import java.util.List;
import java.util.Map;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.RulesEngine;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.model.rules.NamedRule;

public interface DefaultFactoryPhaseListener {
  // RULES STEP ///////////////////////////////////////////////////////////////////////////////////
  public void beforeFactoryPhaseRulesStep(RulesEngine rulesEngine, Map<String, Object> initialState,
      List<NamedRule> rules);

  public void afterFactoryPhaseRulesStep(RulesEngine rulesEngine, Map<String, Object> initialState,
      List<NamedRule> rules, Map<String, Object> newState);

  public void catchFactoryPhaseRulesStep(Throwable problem);

  public void finallyFactoryPhaseRulesStep();

  // CREATE STEP //////////////////////////////////////////////////////////////////////////////////
  public void beforeFactoryPhaseCreateStep(Map<String, Object> newState);

  public void afterFactoryPhaseCreateStep(Map<String, Object> newState, Object instance);

  public void catchFactoryPhaseCreateStep(Throwable problem);

  public void finallyFactoryPhaseCreateStep();
}
