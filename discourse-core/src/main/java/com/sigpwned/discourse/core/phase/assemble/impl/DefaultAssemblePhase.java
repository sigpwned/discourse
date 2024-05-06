package com.sigpwned.discourse.core.phase.assemble.impl;

import com.sigpwned.discourse.core.phase.assemble.AssemblePhase;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class DefaultAssemblePhase implements AssemblePhase {

  @Override
  public Object assemble(List<Consumer<Map<String, List<Object>>>> steps,
      Map<String, List<Object>> arguments) {

    for (Consumer<Map<String, List<Object>>> step : steps) {
      step.accept(arguments);
    }

    if (arguments.isEmpty()) {
      // TODO better exception
      throw new IllegalArgumentException("Assembly phase produced no output keys");
    }

    if (arguments.size() > 1) {
      // TODO better exception
      throw new IllegalArgumentException("Assembly phase produced multiple output keys");
    }

    List<Object> argumentsList = arguments.values().iterator().next();

    if (argumentsList.isEmpty()) {
      // TODO better exception
      throw new IllegalArgumentException("Assembly phase produced no output values");
    }

    if (argumentsList.size() > 1) {
      // TODO better exception
      throw new IllegalArgumentException("Assembly phase produced multiple output values");
    }

    return argumentsList.get(0);
  }
}
