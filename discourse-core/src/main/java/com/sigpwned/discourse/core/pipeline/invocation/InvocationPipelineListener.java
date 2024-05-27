package com.sigpwned.discourse.core.pipeline.invocation;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.sigpwned.discourse.core.args.Coordinate;
import com.sigpwned.discourse.core.args.Token;
import com.sigpwned.discourse.core.command.ResolvedCommand;
import com.sigpwned.discourse.core.command.RootCommand;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.PreparedClass;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.WalkedClass;

public interface InvocationPipelineListener {
  // SCAN STEP ////////////////////////////////////////////////////////////////////////////////////
  default <T> void beforeScanStep(Class<T> clazz) {}

  default <T> void afterScanStep(Class<T> clazz, RootCommand<T> root) {}

  default void catchScanStep(Throwable t) {}

  default void finallyScanStep() {}

  ////// WALK /////////////////////////////////////////////////////////////////////////////////////
  default <T> void beforeScanStepWalk(Class<T> clazz) {}

  default <T> void afterScanStepWalk(Class<T> clazz,
      List<WalkedClass<? extends T>> walkedClasses) {}

  default void catchScanStepWalk(Throwable t) {}

  default void finallyScanStepWalk() {}

  ////// PREPARE //////////////////////////////////////////////////////////////////////////////////
  default <T> void beforeScanStepPrepare(List<WalkedClass<? extends T>> walkedClasses) {}

  default <T> void afterScanStepPrepare(List<WalkedClass<? extends T>> walkedClasses,
      List<PreparedClass<? extends T>> preparedClasses) {}

  default void catchScanStepPrepare(Throwable t) {}

  default void finallyScanStepPrepare() {}

  ////// TREE /////////////////////////////////////////////////////////////////////////////////////
  default <T> void beforeScanStepTree(List<PreparedClass<? extends T>> preparedClasses) {}

  default <T> void afterScanStepTree(List<PreparedClass<? extends T>> preparedClasses,
      RootCommand<T> root) {}

  default void catchScanStepTree(Throwable t) {}

  default void finallyScanStepTree() {}

  // RESOLVE STEP /////////////////////////////////////////////////////////////////////////////////
  default void beforeResolveStep(List<String> args) {}

  default void afterResolveStep(List<String> args,
      Optional<ResolvedCommand<?>> maybeResolvedCommand) {}

  default void catchResolveStep(Throwable t) {}

  default void finallyResolveStep() {}

  // PREPROCESS COORDINATES STEP //////////////////////////////////////////////////////////////////
  default void beforePreprocessCoordinatesStep(Map<Coordinate, String> originalCoordinates) {}

  default void afterPreprocessCoordinatesStep(Map<Coordinate, String> originalCoordinates,
      Map<Coordinate, String> preprocessedCoordinates) {}

  default void catchPreprocessCoordinatesStep(Throwable t) {}

  default void finallyPreprocessCoordinatesStep() {}

  // PREPROCESS ARGS STEP /////////////////////////////////////////////////////////////////////////
  default void beforePreprocessArgsStep(List<String> args) {}

  default void afterPreprocessArgsStep(List<String> args, List<String> preprocessedArgs) {}

  default void catchPreprocessArgsStep(Throwable t) {}

  default void finallyPreprocessArgsStep() {}

  // TOKENIZE STEP ////////////////////////////////////////////////////////////////////////////////
  default void beforeTokenizeStep(List<String> args) {}

  default void afterTokenizeStep(List<String> args, List<Token> tokens) {}

  default void catchTokenizeStep(Throwable t) {}

  default void finallyTokenizeStep() {}

  // PREPROCESS TOKENS STEP ///////////////////////////////////////////////////////////////////////
  default void beforePreprocessTokensStep(List<Token> tokens) {}

  default void afterPreprocessTokensStep(List<Token> tokens, List<Token> preprocessedTokens) {}

  default void catchPreprocessTokensStep(Throwable t) {}

  default void finallyPreprocessTokensStep() {}

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
