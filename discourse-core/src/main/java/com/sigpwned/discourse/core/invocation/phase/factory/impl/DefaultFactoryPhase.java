package com.sigpwned.discourse.core.invocation.phase.factory.impl;

import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.CommandBody;
import com.sigpwned.discourse.core.invocation.phase.FactoryPhase;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.rules.RulesEngine;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.rules.model.NamedRule;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class DefaultFactoryPhase implements FactoryPhase {

  private final Supplier<RulesEngine> rulesEngineSupplier;
  private final DefaultFactoryPhaseListener listener;

  public DefaultFactoryPhase(Supplier<RulesEngine> rulesEngineSupplier,
      DefaultFactoryPhaseListener listener) {
    this.rulesEngineSupplier = requireNonNull(rulesEngineSupplier);
    this.listener = requireNonNull(listener);
  }

  @Override
  public final <T> T create(Command<T> command, Map<String, Object> initialState) {
    T result;
    try {
      getListener().beforeFactoryPhase();
      result = doCreate(command, initialState);
      getListener().afterFactoryPhase(result);
    } catch (Throwable problem) {
      getListener().catchFactoryPhase(problem);
      throw problem;
    } finally {
      getListener().finallyFactoryPhase();
    }
    return result;
  }

  private <T> T doCreate(Command<T> command, Map<String, Object> initialState) {
    final CommandBody<T> body = command.getBody().orElseThrow(() -> {
      // TODO better exception
      return new IllegalArgumentException("Command has no body");
    });
    final List<NamedRule> rules = body.getRules();
    final Class<T> clazz = command.getClazz();
    final RulesEngine rulesEngine = getRulesEngineSupplier().get();

    Map<String, Object> newState;
    try {
      getListener().beforeFactoryPhaseRulesStep(rulesEngine, initialState, rules);
      newState = rulesStep(rulesEngine, initialState, rules);
      getListener().afterFactoryPhaseRulesStep(rulesEngine, initialState, rules, newState);
    } catch (Throwable problem) {
      getListener().catchFactoryPhaseRulesStep(rulesEngine, initialState, rules, problem);
      throw problem;
    } finally {
      getListener().finallyFactoryPhaseRulesStep(rulesEngine, initialState, rules);
    }

    T instance;
    try {
      getListener().beforeFactoryPhaseCreateStep(newState);
      instance = factoryStep(clazz, newState);
      getListener().afterFactoryPhaseCreateStep(newState, instance);
    } catch (Throwable problem) {
      getListener().catchFactoryPhaseCreateStep(newState, problem);
      throw problem;
    } finally {
      getListener().finallyFactoryPhaseCreateStep(newState);
    }

    return instance;
  }

  protected Map<String, Object> rulesStep(RulesEngine rulesEngine, Map<String, Object> initialState,
      List<NamedRule> rules) {
    return rulesEngine.run(initialState, rules);
  }

  protected <T> T factoryStep(Class<T> clazz, Map<String, Object> newState) {
    // TODO constant
    return clazz.cast(newState.get(""));
  }

  private Supplier<RulesEngine> getRulesEngineSupplier() {
    return rulesEngineSupplier;
  }

  private DefaultFactoryPhaseListener getListener() {
    return listener;
  }
}
