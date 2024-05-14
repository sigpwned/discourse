package com.sigpwned.discourse.core.invocation.phase.factory.impl;

import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.invocation.model.command.Command;
import com.sigpwned.discourse.core.invocation.phase.FactoryPhase;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class DefaultFactoryPhase implements FactoryPhase {

  private final DefaultFactoryPhaseListener listener;

  public DefaultFactoryPhase(DefaultFactoryPhaseListener listener) {
    this.listener = requireNonNull(listener);
  }

  @Override
  public <T> T create(Command<T> command, Map<String, Object> state) {
    T instance = bodyStep(command, state);

    return instance;
  }

  protected <T> T bodyStep(Command<T> command, Map<String, Object> state) {
    // defensive copy
    state = new HashMap<>(state);

    getListener().beforeFactory(state);

    Function<Map<String, Object>, ? extends T> body = command.getBody().orElseThrow(() -> {
      // TODO better exception
      return new IllegalArgumentException("no body");
    });

    T instance = body.apply(state);

    getListener().afterFactory(unmodifiableMap(state), instance);

    return instance;
  }

  protected DefaultFactoryPhaseListener getListener() {
    return listener;
  }
}
