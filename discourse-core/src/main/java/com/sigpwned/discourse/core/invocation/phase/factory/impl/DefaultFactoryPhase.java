package com.sigpwned.discourse.core.invocation.phase.factory.impl;

import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.CommandBody;
import com.sigpwned.discourse.core.invocation.phase.FactoryPhase;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.rules.RulesEngine;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.rules.model.NamedRule;
import java.util.HashMap;
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
  public <T> T create(Command<T> command, Map<String, Object> initialState) {
    CommandBody<T> body = command.getBody().orElseThrow(() -> {
      // TODO better exception
      return new IllegalArgumentException("Command has no body");
    });

    RulesEngine rulesEngine = getRulesEngineSupplier().get();

    Map<String, Object> newState = rulesStep(rulesEngine, initialState, body.getRules());

    T instance = factoryStep(command.getClazz(), newState);

    return instance;
  }

  protected Map<String, Object> rulesStep(RulesEngine rulesEngine, Map<String, Object> state,
      List<NamedRule> rules) {
    // defensive copy
    state = new HashMap<>(state);

    getListener().beforeFactory(state);

    Map<String, Object> newState = rulesEngine.run(state, rules);

    return newState;
  }

  protected <T> T factoryStep(Class<T> clazz, Map<String, Object> newState) {
    getListener().beforeFactory(newState);

    T instance = clazz.cast(newState.get(""));

    getListener().afterFactory(newState, instance);

    return instance;
  }

  protected Supplier<RulesEngine> getRulesEngineSupplier() {
    return rulesEngineSupplier;
  }

  protected DefaultFactoryPhaseListener getListener() {
    return listener;
  }
}
