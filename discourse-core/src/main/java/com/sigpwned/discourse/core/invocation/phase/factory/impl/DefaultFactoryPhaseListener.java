package com.sigpwned.discourse.core.invocation.phase.factory.impl;

import java.util.Map;

public interface DefaultFactoryPhaseListener {

  public void beforeFactory(Map<String, Object> state);

  public void afterFactory(Map<String, Object> state, Object instance);
}
