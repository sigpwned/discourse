package com.sigpwned.discourse.core.pipeline.invocation.command.step;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import com.sigpwned.discourse.core.pipeline.invocation.command.CommandInvocationContext;

public class MapStep {
  public Map<String, List<Object>> map(Map<String, Function<String, Object>> mappers,
      Map<String, List<String>> groupedArgs, CommandInvocationContext context) {
    Map<String, List<Object>> mappedArgs = new HashMap<>();
    for (Map.Entry<String, List<String>> entry : groupedArgs.entrySet()) {
      String name = entry.getKey();
      List<String> values = entry.getValue();
      Function<String, Object> mapper = mappers.get(name);
      List<Object> mappedValues = values.stream().map(mapper).collect(Collectors.toList());
      mappedArgs.put(name, mappedValues);
    }
    return mappedArgs;
  }
}
