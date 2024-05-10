package com.sigpwned.discourse.core.invocation.phase.parse.reduce;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface ReducePhase {

  public Map<String, Object> reduce(Map<String, List<Object>> mappedArgs,
      Map<String, Function<List<Object>, Object>> reducers);
}
