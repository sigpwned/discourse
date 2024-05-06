package com.sigpwned.discourse.core.phase.assemble.impl.step;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ListAssemblyStep extends AssemblyStepBase {

  public ListAssemblyStep(String name) {
    super(name);
  }

  @Override
  protected Optional<Object> assemble(List<Object> values) {
    if (values.isEmpty()) {
      return Optional.empty();
    }
    List<Object> result = newList();
    result.addAll(values);
    return Optional.of(result);
  }

  /**
   * extension hook
   */
  protected List<Object> newList() {
    return new ArrayList<>();
  }
}
