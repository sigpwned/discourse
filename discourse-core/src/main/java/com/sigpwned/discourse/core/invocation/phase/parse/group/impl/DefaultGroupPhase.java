package com.sigpwned.discourse.core.invocation.phase.parse.group.impl;

import static java.util.Collections.*;
import static java.util.stream.Collectors.toSet;

import com.sigpwned.discourse.core.invocation.phase.parse.group.GroupPhase;
import com.sigpwned.discourse.core.util.MoreSets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class DefaultGroupPhase implements GroupPhase {

  public Map<String, List<String>> group(List<Entry<Object, String>> parsedArgs,
      Map<Object, String> propertyNames) {
    if (!parsedArgs.stream().allMatch(e -> propertyNames.containsKey(e.getKey()))) {
      // TODO better exception
      throw new IllegalArgumentException("Missing property for " + MoreSets.difference(
          parsedArgs.stream().map(Entry::getKey).collect(toSet()), propertyNames.keySet()));
    }

    // For each argument, assign it to the only property that matches it. If there is no property
    // that matches it, then throw an exception because the argument is invalid. If there are
    // multiple properties that match it, then throw an exception because the developer has made a
    // mistake. If there is exactly one property that matches it, then add the argument to the list
    // of arguments for that property. Note that we process arguments in order, so the order of the
    // arguments is preserved in the order of the arguments for each property.
    Map<String, List<String>> groupedArgs = new HashMap<>(propertyNames.size());
    for (Map.Entry<Object, String> argument : parsedArgs) {
      Object argumentCoordinate = argument.getKey();
      String argumentValue = argument.getValue();

      // Find the property that matches the coordinate.
      String propertyName = propertyNames.get(argumentCoordinate);

      // Add the argument to the list of arguments for the property.
      groupedArgs.computeIfAbsent(propertyName, k -> new ArrayList<>()).add(argumentValue);
    }

    // Let's be immutable, please.
    for (Map.Entry<String, List<String>> groupedArg : groupedArgs.entrySet()) {
      groupedArg.setValue(unmodifiableList(groupedArg.getValue()));
    }

    return unmodifiableMap(groupedArgs);
  }
}
