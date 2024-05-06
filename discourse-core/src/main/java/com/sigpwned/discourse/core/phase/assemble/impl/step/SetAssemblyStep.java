package com.sigpwned.discourse.core.phase.assemble.impl.step;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class SetAssemblyStep extends AssemblyStepBase {

  public SetAssemblyStep(String name) {
    super(name);
  }

  @Override
  protected Optional<Object> assemble(List<Object> values) {
    if (values.isEmpty()) {
      return Optional.empty();
    }
    Set<Object> result = newSet();
    result.addAll(values);
    return Optional.of(result);
  }

  /**
   * extension hook
   */
  protected Set<Object> newSet() {
    return new HashSet<>();
  }
}
