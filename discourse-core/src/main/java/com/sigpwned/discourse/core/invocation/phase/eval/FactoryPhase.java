package com.sigpwned.discourse.core.invocation.phase.eval;

import java.util.Map;

public interface FactoryPhase {

  public Object getInstance(Map<String, Object> state);
}
