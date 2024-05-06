package com.sigpwned.discourse.core.phase.assemble.impl.step;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Other assembly steps to think about: How to set default values? How to set fallback values?
 *
 * How do we know which is the "real" root? Do we need to know that? Should we know that?
 */
public abstract class AssemblyStepBase implements Consumer<Map<String, List<Object>>> {

  private final String name;

  public AssemblyStepBase(String name) {
    this.name = requireNonNull(name);
  }

  @Override
  public void accept(Map<String, List<Object>> transformedArgs) {
    List<Object> originalValues = transformedArgs.get(getName());

    List<Object> newValues;
    if (originalValues != null) {
      newValues = assemble(originalValues).map(List::of)
          .orElseGet(this::defaultValue);
    } else {
      newValues = defaultValue();
    }

    transformedArgs.put(getName(), originalValues);
  }

  /**
   * extension hook
   */
  protected abstract Optional<Object> assemble(List<Object> values);

  /**
   * extension hook
   */
  protected List<Object> defaultValue() {
    return List.of();
  }

  public String getName() {
    return name;
  }
}
