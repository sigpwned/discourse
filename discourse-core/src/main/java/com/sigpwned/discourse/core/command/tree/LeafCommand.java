package com.sigpwned.discourse.core.command.tree;

import static java.util.Objects.requireNonNull;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public final class LeafCommand<T> extends Command<T> {
  private final List<LeafCommandProperty> properties;
  private final Consumer<Map<String, Object>> reactor;
  private final Function<Map<String, Object>, T> constructor;

  /**
   * @param description The description of the command.
   * @param properties The properties of the command. This list is not copied or made unmodifiable,
   *        so if the caller wants the list to be immutable, then they will need to handle that
   *        themselves.
   * @param reactor The reactor for the command.
   * @param constructor The constructor for the command.
   */
  public LeafCommand(String description, List<LeafCommandProperty> properties,
      Consumer<Map<String, Object>> reactor, Function<Map<String, Object>, T> constructor) {
    super(description);
    // Note that we do not make this unmodifiable or even perform a defensive copy. Where or not
    // not this list is mutable is up to the caller.
    this.properties = requireNonNull(properties);
    this.reactor = requireNonNull(reactor);
    this.constructor = requireNonNull(constructor);
  }

  /**
   * @return the properties
   */
  public List<LeafCommandProperty> getProperties() {
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
