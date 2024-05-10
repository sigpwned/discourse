package com.sigpwned.discourse.core.invocation.phase.parse.map;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface MapPhase {

  public Map<String, List<Object>> map(Map<String, List<String>> groupedArgs,
      Map<String, Function<String, Object>> mappers);
}
