package com.sigpwned.discourse.core.invocation.phase.factory;

import java.util.List;
import java.util.Map;
import com.sigpwned.discourse.core.invocation.phase.scan.RulesEngine;
import com.sigpwned.discourse.core.invocation.phase.scan.model.rules.NamedRule;

public interface FactoryPhaseListener {
  // RULES STEP ///////////////////////////////////////////////////////////////////////////////////
  default void beforeFactoryPhaseRulesStep(RulesEngine rulesEngine,
      Map<String, Object> initialState, List<NamedRule> rules) {}

  default void afterFactoryPhaseRulesStep(RulesEngine rulesEngine, Map<String, Object> initialState,
      List<NamedRule> rules, Map<String, Object> newState) {}

  default void catchFactoryPhaseRulesStep(Throwable problem) {}

  default void finallyFactoryPhaseRulesStep() {}

  // CREATE STEP //////////////////////////////////////////////////////////////////////////////////
  default void beforeFactoryPhaseCreateStep(Map<String, Object> newState) {}

  default void afterFactoryPhaseCreateStep(Map<String, Object> newState, Object instance) {}

  default void catchFactoryPhaseCreateStep(Throwable problem) {}

  default void finallyFactoryPhaseCreateStep() {}
}
