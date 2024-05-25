package com.sigpwned.discourse.core.pipeline.invocation.args;

import java.util.List;
import java.util.Map;
import com.sigpwned.discourse.core.args.Coordinate;
import com.sigpwned.discourse.core.args.Token;

public interface ArgsInvocationPipelineListener {
  // PREPROCESS COORDINATES STEP //////////////////////////////////////////////////////////////////
  default void beforePreprocessCoordinatesStep(Map<Coordinate, String> originalCoordinates) {}

  default void afterPreprocessCoordinatesStep(Map<Coordinate, String> originalCoordinates,
      Map<Coordinate, String> preprocessedCoordinates) {}

  default void catchPreprocessCoordinatesStep(Throwable t) {}

  default void finallyPreprocessCoordinatesStep() {}

  // TOKENIZE STEP ////////////////////////////////////////////////////////////////////////////////
  default void beforeTokenizeStep(List<String> args) {}

  default void afterTokenizeStep(List<String> args, List<Token> tokens) {}

  default void catchTokenizeStep(Throwable t) {}

  default void finallyTokenizeStep() {}

  // PARSE STEP ///////////////////////////////////////////////////////////////////////////////////
  default void beforeParseStep(List<Token> tokens) {}

  default void afterParseStep(List<Token> tokens, List<Map.Entry<Coordinate, String>> parsedArgs) {}

  default void catchParseStep(Throwable t) {}

  default void finallyParseStep() {}

  // ATTRIBUTE STEP ///////////////////////////////////////////////////////////////////////////////
  default void beforeAttributeStep(List<Map.Entry<Coordinate, String>> parsedArgs) {}

  default void afterAttributeStep(List<Map.Entry<Coordinate, String>> parsedArgs,
      List<Map.Entry<String, String>> attributedArgs) {}

  default void catchAttributeStep(Throwable t) {}

  default void finallyAttributeStep() {}

  // GROUP STEP ///////////////////////////////////////////////////////////////////////////////////
  default void beforeGroupStep(List<Map.Entry<String, String>> attributedArgs) {}

  default void afterGroupStep(List<Map.Entry<String, String>> attributedArgs,
      Map<String, List<String>> groupedArgs) {}

  default void catchGroupStep(Throwable t) {}

  default void finallyGroupStep() {}

  // MAP STEP /////////////////////////////////////////////////////////////////////////////////////
  default void beforeMapStep(Map<String, List<String>> groupedArgs) {}

  default void afterMapStep(Map<String, List<String>> groupedArgs,
      Map<String, List<Object>> mappedArgs) {}

  default void catchMapStep(Throwable t) {}

  default void finallyMapStep() {}

  // REDUCE STEP //////////////////////////////////////////////////////////////////////////////////
  default void beforeReduceStep(Map<String, List<Object>> mappedArgs) {}

  default void afterReduceStep(Map<String, List<Object>> mappedArgs,
      Map<String, Object> sinkedArgs) {}

  default void catchReduceStep(Throwable t) {}

  default void finallyReduceStep() {}

  // FINISH STEP //////////////////////////////////////////////////////////////////////////////////
  default void beforeFinishStep(Map<String, Object> sinkedArgs) {}

  default void afterFinishStep(Map<String, Object> sinkedArgs, Object result) {}

  default void catchFinishStep(Throwable t) {}

  default void finallyFinishStep() {}
}
