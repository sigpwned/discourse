package com.sigpwned.discourse.core.command;

import static java.util.Collections.unmodifiableMap;
import static java.util.Objects.requireNonNull;
import java.util.Map;
import com.sigpwned.discourse.core.value.deserializer.ValueDeserializer;
import com.sigpwned.discourse.core.value.sink.ValueSink;

public class CommandProperty {

  /**
   * The name of the property, for example, {@code "help"}.
   */
  private final String name;

  /**
   * The description of the property, for example, {@code "Display this help message"}.
   */
  private final String description;

  /**
   * <p>
   * The syntax for specifying the value of this property. For example:
   * </p>
   *
   * <pre>
   * Map.of("-h", "flag", "--help", "flag")
   * </pre>
   */
  private final Map<String, String> syntax;


  private final ValueSink sink;


  private final ValueDeserializer<?> deserializer;

  public CommandProperty(String name, String description, Map<String, String> syntax,
      ValueSink sink, ValueDeserializer<?> deserializer) {
    this.name = requireNonNull(name);
    this.description = requireNonNull(description);
    this.syntax = unmodifiableMap(syntax);
    this.sink = requireNonNull(sink);
    this.deserializer = requireNonNull(deserializer);
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public Map<String, String> getSyntax() {
    return syntax;
  }

  public ValueSink getSink() {
    return sink;
  }

  public ValueDeserializer<?> getDeserializer() {
    return deserializer;
  }
}
