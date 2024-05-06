package com.sigpwned.discourse.core.phase.assemble.impl.step;

import java.util.List;
import java.util.Optional;

public class ScalarAssemblyStep extends AssemblyStepBase {

  public ScalarAssemblyStep(String name) {
    super(name);
  }

  @Override
  protected Optional<Object> assemble(List<Object> values) {
    if (values.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(values.get(values.size() - 1));
  }
}
