package com.sigpwned.discourse.core.phase.reduce;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public interface ReducePhase {

  public Map<String, List<Object>> reduce(List<Consumer<Map<String, List<Object>>>> steps,
      Map<String, List<Object>> sortedArgs);
}
