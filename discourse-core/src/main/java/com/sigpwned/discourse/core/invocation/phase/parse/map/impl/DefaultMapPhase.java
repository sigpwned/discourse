package com.sigpwned.discourse.core.invocation.phase.parse.map.impl;

import static java.util.Collections.*;

import com.sigpwned.discourse.core.invocation.phase.parse.map.MapPhase;
import com.sigpwned.discourse.core.util.MoreSets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class DefaultMapPhase implements MapPhase {

  @Override
  public Map<String, List<Object>> map(Map<String, List<String>> groupedArgs,
      Map<String, Function<String, Object>> mappers) {
    if (!mappers.keySet().containsAll(groupedArgs.keySet())) {
      // TODO better exception
      throw new IllegalArgumentException(
          "Missing mappers for " + MoreSets.difference(groupedArgs.keySet(), mappers.keySet()));
    }

    Map<String, List<Object>> mappedArgs = new HashMap<>();
    for (Map.Entry<String, List<String>> entry : groupedArgs.entrySet()) {
      String name = entry.getKey();
      List<String> stringValues = entry.getValue();

      Function<String, Object> mapper = mappers.get(name);
      List<Object> mappedValues = new ArrayList<>(stringValues.size());
      for (String stringValue : stringValues) {
        Object mappedValue = mapper.apply(stringValue);
        mappedValues.add(mappedValue);
      }

      mappedArgs.put(name, unmodifiableList(mappedValues));
    }

    return unmodifiableMap(mappedArgs);
  }
}
