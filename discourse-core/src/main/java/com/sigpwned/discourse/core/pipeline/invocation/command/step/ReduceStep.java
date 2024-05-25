package com.sigpwned.discourse.core.pipeline.invocation.command.step;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import com.sigpwned.discourse.core.pipeline.invocation.command.CommandInvocationContext;

public class ReduceStep {
  public Map<String, Object> reduce(Map<String, Function<List<Object>, Object>> reducers,
      Map<String, List<Object>> mappedArgs, CommandInvocationContext context) {
    Map<String, Object> reducedArgs = new HashMap<>();
    for (Map.Entry<String, List<Object>> entry : mappedArgs.entrySet()) {
      String name = entry.getKey();
      List<Object> values = entry.getValue();
      Function<List<Object>, Object> reducer = reducers.get(name);
      Object mappedValue = reducer.apply(values);
      reducedArgs.put(name, mappedValue);
    }
    return reducedArgs;
  }
}
