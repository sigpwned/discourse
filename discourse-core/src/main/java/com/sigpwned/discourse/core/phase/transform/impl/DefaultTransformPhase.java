package com.sigpwned.discourse.core.phase.transform.impl;

import static java.util.stream.Collectors.toCollection;

import com.sigpwned.discourse.core.phase.transform.TransformPhase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Converts parsed arguments, which are a list of string values associated with coordinates, into a
 * map of values associated with properties, which are identified by a string key.
 */
public class DefaultTransformPhase implements TransformPhase {

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
   * @return The deserialized arguments. The keys are the property names and the values are the
   * deserialized values. The order of the arguments is preserved in the order of the arguments for
   * each property. The result is mutable.
   * @throws IllegalArgumentException if there are no properties that match an argument
   */
  public Map<String, List<Object>> transform(
      Map<String, Function<String, Object>> transformations, Map<String, List<String>> arguments) {
    Map<String, List<Object>> result = new HashMap<>(arguments.size());

    // For each argument, find the property that matches the argument and apply the corresponding
    // property deserializer to each argument value. If there are no properties that match the
    // argument, then throw an exception.
    for (Map.Entry<String, List<String>> argument : arguments.entrySet()) {
      String propertyName = argument.getKey();
      List<String> argumentValues = argument.getValue();

      // Find the property that matches the argument.
      Function<String, Object> transformation = transformations.get(propertyName);

      // If there are no properties that match the argument, then throw an exception.
      if (transformation == null) {
        // TODO better exception
        throw new IllegalArgumentException("No transformation for " + propertyName);
      }

      // Apply the property deserializer to each argument value.
      List<Object> propertyValues = argumentValues.stream().map(transformation)
          .collect(toCollection(ArrayList::new));

      // Add the argument to the list of arguments for the property.
      result.put(propertyName, propertyValues);
    }

    return result;
  }
}
