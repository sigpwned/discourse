package com.sigpwned.discourse.core.command;

import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

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
   *   Map.of(
   *     "-h", "flag",
   *     "--help", "flag")
   * </pre>
   */
  private final Map<String, String> syntax;

  /**
   * A function that maps a string representation of the property value to the property value
   * itself. For example, the function {@code Boolean::parseBoolean} could be used to map the string
   * {@code "true"} to the boolean {@code true}.
   */
  private final Function<String, Object> mapper;

  /**
   * A function that reduces a list of property values to a single property value. For example, the
   * function {@code list -> list.get(0)} could be used to reduce a list of property values to the
   * first value in the list, or the value {@code list -> list} could be used to return the list
   * itself as the property value.
   */
  private final Function<List<Object>, Object> reducer;

  public CommandProperty(String name, String description, Map<String, String> syntax,
      Function<String, Object> mapper, Function<List<Object>, Object> reducer) {
    this.name = requireNonNull(name);
    this.description = requireNonNull(description);
    this.syntax = unmodifiableMap(syntax);
    this.mapper = requireNonNull(mapper);
    this.reducer = requireNonNull(reducer);
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

  public Function<String, Object> getMapper() {
    return mapper;
  }

  public Function<List<Object>, Object> getReducer() {
    return reducer;
  }
}
