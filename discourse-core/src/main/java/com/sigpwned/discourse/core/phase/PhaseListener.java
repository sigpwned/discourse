package com.sigpwned.discourse.core.phase;

import com.sigpwned.discourse.core.phase.assemble.AssemblePhase;
import com.sigpwned.discourse.core.phase.parse.ParsePhase;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public interface PhaseListener {

  /**
   * Called before the resolve phase starts.
   *
   * @param choices The commands from which the pipeline can choose. This map is shallowly mutable,
   *                so listeners can change the commands available to the pipeline.
   * @param args    The arguments from the user. This list is mutable, so listeners can modify the
   *                arguments.
   */
  default void beforeResolve(Map<List<String>, Object> choices, List<String> args) {
  }

  /**
   * Called after the resolve phase finishes.
   *
   * @param choices   The commands from which the pipeline can choose. This map is now deeply
   *                  immutable.
   * @param args      The arguments from the user. This list is mutable, so listeners can modify the
   *                  arguments.
   * @param selection The command that was selected by the resolve phase. This entry is mutable, so
   *                  listeners can change the selected command.
   */
  default void afterResolve(Map<List<String>, Object> choices, List<String> args,
      Map.Entry<List<String>, Object> selection) {
  }

  /**
   * Called before {@link ParsePhase the parse phase} starts.
   *
   * @param vocabulary The vocabulary of the command line. This map is mutable, so listeners can
   *                   change the vocabulary. For more information about the vocabulary, see
   *                   {@link ParsePhase}. Example value: {@code {"-h": "flag", "-t": "option"}}.
   * @param args       The arguments from the user. This list is mutable, so listeners can modify
   *                   the arguments.
   */
  default void beforeParse(Map<String, String> vocabulary, List<String> args) {
  }

  /**
   * Called before {@link ParsePhase the parse phase} starts.
   *
   * @param vocabulary The vocabulary of the command line. This map is now immutable.
   * @param args       The arguments from the user. This list is mutable, so listeners can modify
   *                   the arguments.
   * @param parsedArgs The parsed arguments. This list is deeply mutable, so listeners can modify
   *                   the parsed arguments.
   */
  default void afterParse(Map<String, String> vocabulary, List<String> args,
      List<Map.Entry<Object, String>> parsedArgs) {
  }

  default void beforeCorrelate(Map<Object, String> propertyNames,
      List<Map.Entry<Object, String>> parsedArgs) {
  }

  default void afterCorrelate(Map<Object, String> propertyNames,
      List<Map.Entry<Object, String>> parsedArgs, List<Map.Entry<String, String>> correlatedArgs) {
  }

  default void beforeGroup(List<Map.Entry<String, String>> correlatedArgs) {
    // TODO should we roll this into correlate, since there's no more work to do here?
  }

  default void afterGroup(List<Map.Entry<String, String>> correlatedArgs,
      Map<String, List<String>> groupedArgs) {
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

  /**
   * Called before the assemble phase starts.
   *
   * @param steps           The steps to apply to the transformed arguments. This list is mutable,
   *                        so listeners can change the steps. For more information about the steps,
   *                        see {@link AssemblePhase}.
   * @param transformedArgs The transformed arguments. This map is deeply mutable, so listeners can
   *                        modify the transformed arguments.
   */
  default void beforeAssemble(List<Consumer<Map<String, Object>>> steps,
      Map<String, Object> reducedArgs) {
  }

  /**
   * Called after the assemble phase finishes.
   *
   * @param steps           The steps to apply to the transformed arguments. This list is now
   *                        immutable.
   * @param transformedArgs The transformed arguments. This map is now deeply immutable.
   * @param configuration   The configuration object that was assembled. Whether or not this object
   *                        is mutable depends on the implementation of the assembler.
   */
  default void afterAssemble(List<Consumer<Map<String, Object>>> steps,
      Map<String, Object> reducedArgs, Object configuration) {
    // TODO should this be some kind of mutable reference to configuration?
  }
}
