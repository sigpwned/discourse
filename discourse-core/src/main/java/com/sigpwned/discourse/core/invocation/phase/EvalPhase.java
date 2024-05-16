package com.sigpwned.discourse.core.invocation.phase;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface EvalPhase {

  public Map<String, Object> eval(Map<String, Function<String, Object>> mappers,
      Map<String, Function<List<Object>, Object>> reducers,
      List<Map.Entry<String, String>> parsedArgs);
}
