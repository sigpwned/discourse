package com.sigpwned.discourse.core.invocation.phase.factory.impl;

import static java.util.Objects.requireNonNull;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import com.sigpwned.discourse.core.invocation.phase.FactoryPhase;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.rules.RulesEngine;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.rules.model.NamedRule;

public class DefaultFactoryPhase implements FactoryPhase {

  private final Supplier<RulesEngine> rulesEngineSupplier;
  private final DefaultFactoryPhaseListener listener;

  public DefaultFactoryPhase(Supplier<RulesEngine> rulesEngineSupplier,
      DefaultFactoryPhaseListener listener) {
    this.rulesEngineSupplier = requireNonNull(rulesEngineSupplier);
    this.listener = requireNonNull(listener);
  }

  @Override
  public final Object create(List<NamedRule> rules, Map<String, Object> initialState) {
    final RulesEngine rulesEngine = getRulesEngineSupplier().get();

    Map<String, Object> newState = doRulesStep(rulesEngine, initialState, rules);

    Object instance = doFactoryStep(newState);

    return instance;
  }

  private Map<String, Object> doRulesStep(RulesEngine rulesEngine, Map<String, Object> initialState,
      List<NamedRule> rules) {
    Map<String, Object> newState;
    try {
      getListener().beforeFactoryPhaseRulesStep(rulesEngine, initialState, rules);
      newState = rulesStep(rulesEngine, initialState, rules);
      getListener().afterFactoryPhaseRulesStep(rulesEngine, initialState, rules, newState);
    } catch (Throwable problem) {
      getListener().catchFactoryPhaseRulesStep(problem);
      throw problem;
    } finally {
      getListener().finallyFactoryPhaseRulesStep();
    }

    return newState;
  }

  protected Map<String, Object> rulesStep(RulesEngine rulesEngine, Map<String, Object> initialState,
      List<NamedRule> rules) {
    return rulesEngine.run(initialState, rules);
  }

  private Object doFactoryStep(Map<String, Object> newState) {
    Object instance;
    try {
      getListener().beforeFactoryPhaseCreateStep(newState);
      instance = factoryStep(newState);
      getListener().afterFactoryPhaseCreateStep(newState, instance);
    } catch (Throwable problem) {
      getListener().catchFactoryPhaseCreateStep(problem);
      throw problem;
    } finally {
      getListener().finallyFactoryPhaseCreateStep();
    }

    return instance;
  }

  protected Object factoryStep(Map<String, Object> newState) {
    return newState.get("");
  }

  private Supplier<RulesEngine> getRulesEngineSupplier() {
    return rulesEngineSupplier;
  }

  private DefaultFactoryPhaseListener getListener() {
    return listener;
  }
}
