package com.sigpwned.discourse.core.command;

import static java.util.Collections.unmodifiableSet;
import static java.util.Objects.requireNonNull;
import java.util.Set;
import com.sigpwned.discourse.core.args.Coordinate;
import com.sigpwned.discourse.core.module.value.deserializer.ValueDeserializer;
import com.sigpwned.discourse.core.module.value.sink.ValueSink;

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
   * Whether or not this property is a help flag.
   */
  private final boolean help;

  /**
   * Whether or not this property is a version flag.
   */
  private final boolean version;

  /**
   * <p>
   * The syntax for specifying the value of this property. For example:
   * </p>
   *
   * <pre>
   * Map.of("-h", "flag", "--help", "flag")
   * </pre>
   */
  private final Set<Coordinate> coordinates;


  private final ValueSink sink;


  private final ValueDeserializer<?> deserializer;

  public CommandProperty(String name, String description, boolean help, boolean version,
      Set<Coordinate> coordinates, ValueSink sink, ValueDeserializer<?> deserializer) {
    // TODO does syntax have to be non-empty?
    this.name = requireNonNull(name);
    this.description = requireNonNull(description);
    this.help = help;
    this.version = version;
    this.coordinates = unmodifiableSet(coordinates);
    this.sink = requireNonNull(sink);
    this.deserializer = requireNonNull(deserializer);
    if (help && version) {
      throw new IllegalArgumentException("help and version cannot both be true");
    }
    if (help) {
      if (sink.getGenericType() != boolean.class && sink.getGenericType() != Boolean.class) {
        throw new IllegalArgumentException("help must be boolean");
      }
      if (coordinates.isEmpty()) {
        throw new IllegalArgumentException("help must have syntax");
      }
      // TODO Where does this constant belong?
      // if (coordinates.values().stream().anyMatch(s -> !s.equals(ArgumentType.FLAG))) {
      // throw new IllegalArgumentException("help must have flag syntax");
      // }
    }
    if (version) {
      if (sink.getGenericType() != boolean.class && sink.getGenericType() != Boolean.class) {
        throw new IllegalArgumentException("version must be boolean");
      }
      if (coordinates.isEmpty()) {
        throw new IllegalArgumentException("version must have syntax");
      }
      // TODO Where does this constant belong?
      // if (coordinates.values().stream().anyMatch(s -> !s.equals(ArgumentType.FLAG))) {
      // throw new IllegalArgumentException("version must have flag syntax");
      // }
    }
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public boolean isHelp() {
    return help;
  }

  public boolean isVersion() {
    return version;
  }

  public Set<Coordinate> getCoordinates() {
    return coordinates;
  }

  public ValueSink getSink() {
    return sink;
  }

  public ValueDeserializer<?> getDeserializer() {
    return deserializer;
  }
}
