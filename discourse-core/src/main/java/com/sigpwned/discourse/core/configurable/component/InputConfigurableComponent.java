package com.sigpwned.discourse.core.configurable.component;

import static java.util.Objects.requireNonNull;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.List;

/**
 * A {@link ConfigurableComponent} that represents an input parameter to the object Configurable
 * class' constructor.
 */
public final class InputConfigurableComponent extends ConfigurableComponent {

  private final int index;
  private final Parameter parameter;

  public InputConfigurableComponent(int index, Parameter parameter, Type genericType) {
    super(parameter.getName(), parameter.getType(), genericType,
        List.of(parameter.getAnnotations()));
    if (index < 0) {
      throw new IllegalArgumentException("index must not be negative");
    }
    this.index = index;
    this.parameter = requireNonNull(parameter);
  }

  public int getIndex() {
    return index;
  }

  public Parameter getParameter() {
    return parameter;
  }
}
