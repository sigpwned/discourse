package com.sigpwned.discourse.core.invocation.phase.eval.impl;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface DefaultEvalPhaseListener {
  // GROUP STEP ///////////////////////////////////////////////////////////////////////////////////
  default void beforeEvalPhaseGroupStep(List<Map.Entry<String, String>> parsedArgs) {}

  default void afterEvalPhaseGroupStep(List<Map.Entry<String, String>> parsedArgs,
      Map<String, List<String>> groupedArgs) {}

  default void catchEvalPhaseGroupStep(Throwable problem) {}

  default void finallyEvalPhaseGroupStep() {}

  // EVAL STEP ////////////////////////////////////////////////////////////////////////////////////
  default void beforeEvalPhaseEvalStep(Map<String, Function<String, Object>> mappers,
      Map<String, List<String>> groupedArgs) {}

  default void afterEvalPhaseEvalStep(Map<String, Function<String, Object>> mappers,
      Map<String, List<String>> groupedArgs, Map<String, List<Object>> mappedArgs) {}

  default void catchEvalPhaseEvalStep(Throwable problem) {}

  default void finallyEvalPhaseEvalStep() {}

  // REDUCE STEP ///////////////////////////////////////////////////////////////////////////////////
  default void beforeEvalPhaseReduceStep(Map<String, Function<List<Object>, Object>> reducers,
      Map<String, List<Object>> mappedArgs) {}

  default void afterEvalPhaseReduceStep(Map<String, Function<List<Object>, Object>> reducers,
      Map<String, List<Object>> mappedArgs, Map<String, Object> reducedArgs) {}

  default void catchEvalPhaseReduceStep(Throwable problem) {}

  default void finallyEvalPhaseReduceStep() {}
}
