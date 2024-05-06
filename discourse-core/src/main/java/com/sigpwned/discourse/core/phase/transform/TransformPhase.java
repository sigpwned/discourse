package com.sigpwned.discourse.core.phase.transform;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Converts parsed arguments, which are a list of string values associated with coordinates, into a
 * map of values associated with properties, which are identified by a string key.
 */
public interface TransformPhase {

  /**
   * <p>
   * Transform the given arguments into a map of values associated with properties. For example,
   * given the arguments:
   * </p>
   *
   * <pre>
   *   alpha &rarr; ["1"]
   *   bravo &rarr; ["2", "3"]
   *   greeting &rarr; ["hello"]
   *   name &rarr; ["world"]
   * </pre>
   *
   * <p>
   * and the properties:
   * </p>
   *
   * <pre>
   *   alpha &rarr; Integer::parseInt
   *   bravo &rarr; Integer::parseInt
   *   greeting &rarr; String::valueOf
   *   name &rarr; String::valueOf
   * </pre>
   *
   * <p>
   * the result would be:
   * </p>
   *
   * <pre>
   *   alpha &rarr; [1]
   *   bravo &rarr; [2, 3]
   *   greeting &rarr; ["hello"]
   *   name &rarr; ["world"]
   * </pre>
   *
   * @param transformations the properties
   * @param arguments       the arguments
   * @return The transformed arguments. The keys are the property names and the values are the
   * transformed values. The order of the arguments is preserved in the order of the arguments for
   * each property. The result is deeply mutable.
   * @throws IllegalArgumentException if there are no properties that match an argument
   */
  public Map<String, List<Object>> transform(Map<String, Function<String, Object>> transformations,
      Map<String, List<String>> arguments);
}
