package com.sigpwned.discourse.core.phase.collect.impl;

import com.sigpwned.discourse.core.phase.collect.CollectPhase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultCollectPhase implements CollectPhase {

  public Map<String, List<String>> collect(Map<Object, String> properties,
      List<Map.Entry<Object, String>> arguments) {
    Map<String, List<String>> result = new HashMap<>(properties.size());

    // For each argument, assign it to the only property that matches it. If there is no property
    // that matches it, then throw an exception because the argument is invalid. If there are
    // multiple properties that match it, then throw an exception because the developer has made a
    // mistake. If there is exactly one property that matches it, then add the argument to the list
    // of arguments for that property. Note that we process arguments in order, so the order of the
    // arguments is preserved in the order of the arguments for each property.
    for (Map.Entry<Object, String> argument : arguments) {
      Object argumentCoordinate = argument.getKey();
      String argumentValue = argument.getValue();

      // Find the property that matches the coordinate.
      String propertyName = properties.get(argumentCoordinate);
      if (propertyName == null) {
        // TODO better exception
        throw new IllegalArgumentException("No property for " + argumentCoordinate);
      }

      // Add the argument to the list of arguments for the property.
      result.computeIfAbsent(propertyName, k -> new ArrayList<>()).add(argumentValue);
    }

    return result;
  }
}
