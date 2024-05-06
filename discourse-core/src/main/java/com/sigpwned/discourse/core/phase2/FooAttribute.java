package com.sigpwned.discourse.core.phase2;

import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class FooAttribute {

  /**
   * The name of the attribute.
   */
  private final String name;

  /**
   * The coordinates of the attribute. This is a map from coordinate values to coordinate types. The
   * coordinates of all attributes collectively determines the parsing vocabulary.
   */
  private final Map<Object, String> coordinates;

  /**
   * A function that maps a string to an object. This is used to map a string argument to an object
   * value during the map phase.
   */
  private final Function<String, Object> mapper;

  /**
   * A function that reduces a list of objects to one object. This is used to combine all of the
   * object values for an attribute into a single object value during the reduce phase.
   */
  private final Function<List<Object>, Object> reducer;

  public FooAttribute(String name, Map<Object, String> coordinates, Function<String, Object> mapper,
      Function<List<Object>, Object> reducer) {
    this.name = requireNonNull(name);
    this.coordinates = unmodifiableMap(coordinates);
    this.mapper = requireNonNull(mapper);
    this.reducer = requireNonNull(reducer);
    if (coordinates.isEmpty()) {
      throw new IllegalArgumentException("coordinates must not be empty");
    }
  }

  public String getName() {
    return name;
  }

  public Map<Object, String> getCoordinates() {
    return coordinates;
  }

  public Function<String, Object> getMapper() {
    return mapper;
  }

  public Function<List<Object>, Object> getReducer() {
    return reducer;
  }
}
