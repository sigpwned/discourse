package com.sigpwned.discourse.core.command.planned;

import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public final class PlannedCommand<T> {
  private final List<ParentCommand> parents;
  private final String name;
  private final String version;
  private final String description;
  private final List<PlannedCommandProperty> properties;
  private final Consumer<Map<String, Object>> reactor;
  private final Function<Map<String, Object>, T> constructor;

  public PlannedCommand(List<ParentCommand> parents, String name, String version,
      String description, List<PlannedCommandProperty> properties,
      Consumer<Map<String, Object>> reactor, Function<Map<String, Object>, T> constructor) {
    this.parents = unmodifiableList(parents);
    this.name = name;
    this.version = version;
    this.description = description;
    this.properties = unmodifiableList(properties);
    this.reactor = requireNonNull(reactor);
    this.constructor = requireNonNull(constructor);
  }

  /**
   * @return the parents
   */
  public List<ParentCommand> getParents() {
    return parents;
  }

  /**
   * @return the name
   */
  public Optional<String> getName() {
    return Optional.ofNullable(name);
  }

  /**
   * @return the version
   */
  public Optional<String> getVersion() {
    return Optional.ofNullable(version);
  }

  /**
   * @return the description
   */
  public Optional<String> getDescription() {
    return Optional.ofNullable(description);
  }

  /**
   * @return the properties
   */
  public List<PlannedCommandProperty> getProperties() {
    return properties;
  }

  /**
   * @return the reactor
   */
  public Consumer<Map<String, Object>> getReactor() {
    return reactor;
  }

  /**
   * @return the constructor
   */
  public Function<Map<String, Object>, T> getConstructor() {
    return constructor;
  }
}
