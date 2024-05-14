package com.sigpwned.discourse.core.invocation.phase.parse;

import com.sigpwned.discourse.core.invocation.phase.parse.parse.ParsePhase;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface ParsePipelineListener {

  /**
   * Called before {@link ParsePhase the parse phase} starts.
   *
   * @param vocabulary  The vocabulary of the resolvedCommand line. This map is mutable, so listeners can
   *                    change the vocabulary. For more information about the vocabulary, see
   *                    {@link ParsePhase}. Example value: {@code {"-h": "flag", "-t": "option"}}.
   * @param commandArgs The arguments from the user. This list is mutable, so listeners can modify
   *                    the arguments.
   */
  default void beforeParse(Map<String, String> vocabulary, List<String> commandArgs) {
  }

  /**
   * Called before {@link ParsePhase the parse phase} starts.
   *
   * @param vocabulary  The vocabulary of the resolvedCommand line. This map is now immutable.
   * @param commandArgs The arguments from the user. This list is mutable, so listeners can modify
   *                    the arguments.
   * @param parsedArgs  The parsed arguments. This list is deeply mutable, so listeners can modify
   *                    the parsed arguments.
   */
  default void afterParse(Map<String, String> vocabulary, List<String> commandArgs,
      List<Map.Entry<Object, String>> parsedArgs) {
  }

  default void beforeGroup(Map<Object, String> propertyNames,
      List<Map.Entry<Object, String>> parsedArgs) {
  }

  default void afterGroup(Map<Object, String> propertyNames,
      List<Map.Entry<Object, String>> parsedArgs, Map<String, List<String>> groupedArgs) {
  }

  default void beforeMap(Map<String, Function<String, Object>> mappers,
      Map<String, List<String>> groupedArgs) {

  }

  default void afterMap(Map<String, Function<String, Object>> mappers,
      Map<String, List<String>> groupedArgs, Map<String, List<Object>> mappedArgs) {

  }

  default void beforeReduce(Map<String, Function<List<Object>, Object>> reducers,
      Map<String, List<Object>> mappedArgs) {

  }

  default void afterReduce(Map<String, Function<List<Object>, Object>> reducers,
      Map<String, List<Object>> mappedArgs, Map<String, Object> reducedArgs) {

  }
}
