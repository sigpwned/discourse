package com.sigpwned.discourse.core.command;

import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class LeafCommand<T> extends Command<T> {
  private final List<LeafCommandProperty> properties;
  private final Function<Map<String, Object>, T> constructor;

  public LeafCommand(String description, List<LeafCommandProperty> properties,
      Function<Map<String, Object>, T> constructor) {
    super(description);
    this.properties = unmodifiableList(properties);
    this.constructor = requireNonNull(constructor);
  }

  /**
   * @return the properties
   */
  public List<LeafCommandProperty> getProperties() {
    return properties;
  }

  /**
   * @return the constructor
   */
  public Function<Map<String, Object>, T> getConstructor() {
    return constructor;
  }
}
