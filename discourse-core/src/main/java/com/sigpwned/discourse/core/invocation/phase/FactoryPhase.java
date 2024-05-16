package com.sigpwned.discourse.core.invocation.phase;

import java.util.List;
import java.util.Map;
import com.sigpwned.discourse.core.invocation.phase.scan.impl.model.rules.NamedRule;

public interface FactoryPhase {

  public Object create(List<NamedRule> rules, Map<String, Object> state);
}
