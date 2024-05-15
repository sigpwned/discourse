package com.sigpwned.discourse.core.invocation.phase.factory.impl;

import com.sigpwned.discourse.core.invocation.phase.scan.impl.rules.RulesEngine;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.rules.model.NamedRule;
import java.util.List;
import java.util.Map;

public interface DefaultFactoryPhaseListener {

  public void beforeFactoryPhase();

  public void afterFactoryPhase(Object instance);

  public void catchFactoryPhase(Throwable problem);

  public void finallyFactoryPhase();

  public void beforeFactoryPhaseRulesStep(RulesEngine rulesEngine, Map<String, Object> initialState,
      List<NamedRule> rules);

  public void afterFactoryPhaseRulesStep(RulesEngine rulesEngine, Map<String, Object> initialState,
      List<NamedRule> rules, Map<String, Object> newState);

  public void catchFactoryPhaseRulesStep(RulesEngine rulesEngine, Map<String, Object> initialState,
      List<NamedRule> rules, Throwable problem);

  public void finallyFactoryPhaseRulesStep(RulesEngine rulesEngine,
      Map<String, Object> initialState, List<NamedRule> rules);

  public void beforeFactoryPhaseCreateStep(Map<String, Object> newState);

  public void afterFactoryPhaseCreateStep(Map<String, Object> newState, Object instance);

  public void catchFactoryPhaseCreateStep(Map<String, Object> newState, Throwable problem);

  public void finallyFactoryPhaseCreateStep(Map<String, Object> newState);
}
