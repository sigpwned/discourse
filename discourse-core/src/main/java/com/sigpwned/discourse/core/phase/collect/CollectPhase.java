package com.sigpwned.discourse.core.phase.collect;

import java.util.List;
import java.util.Map;

/**
 * Converts parsed arguments, which are a list of values associated with coordinates, into a map of
 * values associated with properties, which are identified by a string key.
 */
public interface CollectPhase {

  /**
   * <p>
   * Groups parsed arguments into a map of values associated with properties. For example, given the
   * parsed arguments:
   * </p>
   *
   * <pre>
   *   (-a, 1), (-b, 2), (--bravo, 3)  (0, hello), (1, world)
   * </pre>
   *
   * <p>
   * and the properties:
   * </p>
   *
   * <pre>
   *   -a &rarr; alpha
   *   -b &rarr; bravo
   *   --bravo &rarr; bravo
   *   0 &rarr; greeting
   *   1 &rarr; name
   * </pre>
   *
   * <p>
   * the result would be:
   * </p>
   *
   * <pre>
   *   alpha &rarr; [1]
   *   bravo &rarr; [2, 3]
   *   greeting &rarr; [hello]
   *   name &rarr; [world]
   * </pre>
   *
   * <p>
   * Arguments are processed in the order they are received, so the order of the arguments is
   * preserved in the order of the arguments for each property.
   * </p>
   *
   * @param arguments the parsed arguments
   * @return The map of values associated with properties. The keys are the property names and the
   * values are the lists of values associated with the properties. The order of the values is the
   * same as the order of the arguments. The output is deeply mutable.
   * @throws IllegalArgumentException if an argument does not match any property or if an argument
   *                                  matches multiple properties
   */
  public Map<String, List<String>> collect(Map<Object, String> properties,
      List<Map.Entry<Object, String>> arguments);
}
