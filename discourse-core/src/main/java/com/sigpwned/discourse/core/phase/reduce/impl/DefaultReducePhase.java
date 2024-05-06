package com.sigpwned.discourse.core.phase.reduce.impl;

import com.sigpwned.discourse.core.phase.reduce.ReducePhase;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class DefaultReducePhase implements ReducePhase {

  @Override
  public Map<String, List<Object>> reduce(List<Consumer<Map<String, List<Object>>>> steps,
      Map<String, List<Object>> sortedArgs) {
    for (Consumer<Map<String, List<Object>>> step : steps) {
      step.accept(sortedArgs);
    }
    return sortedArgs;
  }
}
