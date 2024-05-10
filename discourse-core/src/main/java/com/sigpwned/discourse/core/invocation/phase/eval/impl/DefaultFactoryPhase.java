package com.sigpwned.discourse.core.invocation.phase.eval.impl;

import com.sigpwned.discourse.core.invocation.phase.eval.FactoryPhase;
import java.util.Map;

public class DefaultFactoryPhase implements FactoryPhase {

  @Override
  public Object getInstance(Map<String, Object> state) {
    // TODO instance name constant
    Object result = state.get("");
    if (result == null) {
      // TODO better exception
      throw new IllegalArgumentException("No instance found in state");
    }
    return result;
  }
}
