package com.sigpwned.discourse.core.phase.map.impl;

import com.sigpwned.discourse.core.phase.map.MapPhase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class DefaultMapPhase implements MapPhase {

  @Override
  public Map<String, List<Object>> map(Map<String, Function<String, Object>> mappers,
      Map<String, List<String>> collectedArgs) {
    Map<String, List<Object>> mappedArgs = new HashMap<>();
    for (Map.Entry<String, List<String>> entry : collectedArgs.entrySet()) {
      String name = entry.getKey();
      List<String> stringValues = entry.getValue();
      Function<String, Object> mapper = mappers.get(name);
      if (mapper == null) {
        // TODO better exception
        throw new IllegalArgumentException("no mapper for key: " + name);
      }

      List<Object> mappedValues = new ArrayList<>(stringValues.size());
      for (String stringValue : stringValues) {
        Object mappedValue = mapper.apply(stringValue);
        mappedValues.add(mappedValue);
      }

      mappedArgs.put(name, mappedValues);
    }
    return mappedArgs;
  }
}
