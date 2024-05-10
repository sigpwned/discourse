package com.sigpwned.discourse.core.invocation.phase.parse.reduce.impl;

import static java.util.Collections.*;

import com.sigpwned.discourse.core.invocation.phase.parse.reduce.ReducePhase;
import com.sigpwned.discourse.core.util.MoreSets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class DefaultReducePhase implements ReducePhase {

  @Override
  public Map<String, Object> reduce(Map<String, List<Object>> mappedArgs,
      Map<String, Function<List<Object>, Object>> reducers) {
    if (!reducers.keySet().containsAll(mappedArgs.keySet())) {
      // TODO better exception
      throw new IllegalArgumentException(
          "Missing reducers for " + MoreSets.difference(mappedArgs.keySet(), reducers.keySet()));
    }

    Map<String, Object> reducedArgs = new HashMap<>();
    for (Map.Entry<String, List<Object>> entry : mappedArgs.entrySet()) {
      String name = entry.getKey();
      List<Object> mappedValues = entry.getValue();

      Function<List<Object>, Object> reducer = reducers.get(name);
      Object reducedValue = reducer.apply(mappedValues);

      reducedArgs.put(name, reducedValue);
    }

    return unmodifiableMap(reducedArgs);
  }
}
