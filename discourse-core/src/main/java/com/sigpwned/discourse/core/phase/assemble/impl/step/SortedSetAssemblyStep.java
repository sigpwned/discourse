package com.sigpwned.discourse.core.phase.assemble.impl.step;

import java.util.List;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

public class SortedSetAssemblyStep extends AssemblyStepBase {

  public SortedSetAssemblyStep(String name) {
    super(name);
  }

  @Override
  protected Optional<Object> assemble(List<Object> values) {
    if (values.isEmpty()) {
      return Optional.empty();
    }
    SortedSet<Object> result = newSortedSet();
    result.addAll(values);
    return Optional.of(result);
  }

  /**
   * extension hook
   */
  protected SortedSet<Object> newSortedSet() {
    return new TreeSet<>();
  }
}
