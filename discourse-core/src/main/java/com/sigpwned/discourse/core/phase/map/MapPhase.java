package com.sigpwned.discourse.core.phase.map;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface MapPhase {

  public Map<String, List<Object>> map(Map<String, Function<String, Object>> mappers,
      Map<String, List<String>> collectedArgs);
}
